package com.backend_potato.edubox_team2.domain.users.service;

import com.backend_potato.edubox_team2.domain.users.entity.KakaoLoginResponseDTO;
import com.backend_potato.edubox_team2.domain.users.entity.Role;
import com.backend_potato.edubox_team2.domain.users.entity.User;
import com.backend_potato.edubox_team2.domain.users.repository.UserRepository;
import com.backend_potato.edubox_team2.global.jwt.JwtTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class KakaoServiceImpl implements KakaoService{

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${kakao.key.clientId}")
    private String clientId;
    @Value("${kakao.redirectUrl}")
    private String redirectUrl;

    @Override
    public KakaoLoginResponseDTO login(String code, String currentDomain) {
        String redirectUri = currentDomain+"api/user/oauth/kakao";
        String accessToken = getAccessToken(code, redirectUri);
        return null;
    }

    @Override
    public String getAccessToken(String code, String redirectUri) {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        //HTTP 요청
        HttpEntity<MultiValueMap<String,String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        //응답에서 access token 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try{
            jsonNode = mapper.readTree(response.getBody());
        }catch(JsonProcessingException e){
            e.printStackTrace();
        }
        return jsonNode.get("access_token").asText();
    }

    @Override
    public HashMap<String, Object> getUserInfo(String accessToken) {
        HashMap<String, Object> userInfo = new HashMap<>();

        //HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HTTP 요청
        HttpEntity<MultiValueMap<String,String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.exchange(
                        "https://kapi.kakao.com/v2/user/me",
                        HttpMethod.POST,
                        kakaoUserInfoRequest,
                        String.class
                );

        //응답에서 user 정보 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try{
            jsonNode = mapper.readTree(response.getBody());
        }catch(JsonProcessingException e){
            e.printStackTrace();
        }
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("kakao_account").get("profile").get("nickname").asText();
        String imageUrl = jsonNode.get("kakao_account").get("profile").get("profile_image_url").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();

        userInfo.put("id",id);
        userInfo.put("imageUrl",imageUrl);
        userInfo.put("nickname",nickname);
        userInfo.put("email",email);

        return userInfo;
    }

    @Override
    public KakaoLoginResponseDTO loginKakaoUser(HashMap<String, Object> kakaoUserInfo) {
        Long id = Long.parseLong(kakaoUserInfo.get("id").toString());
        String imageUrl = kakaoUserInfo.get("imageUrl").toString();
        String nickname = kakaoUserInfo.get("nickname").toString();
        String email = kakaoUserInfo.get("email").toString();

        User user = userRepository.findActiveUserByEmail(email).orElse(null);

        if (user == null) {
            user = User.builder()
                    .image(imageUrl)
                    .nickname(nickname)
                    .email(email)
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
        }

        String accessToken = jwtTokenUtil.generateAccessToken(user);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user);

        return KakaoLoginResponseDTO.builder()
                .id(id)
                .imageUrl(imageUrl)
                .nickname(nickname)
                .email(email)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
