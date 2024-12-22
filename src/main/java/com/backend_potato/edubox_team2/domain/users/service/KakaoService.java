package com.backend_potato.edubox_team2.domain.users.service;

import com.backend_potato.edubox_team2.domain.users.entity.KakaoLoginResponseDTO;
import io.swagger.v3.oas.annotations.Operation;

import java.util.HashMap;

public interface KakaoService {
    @Operation(summary = "카카오 로그인", description = "카카오 로그인 요청 로직을 처리하는 API 입니다.")
    KakaoLoginResponseDTO login(String code, String currentDomain);

    @Operation(summary = "카카오 토큰 요청", description = "카카오 로그인 시 사용자 액세스 토큰을 가져오는 API 입니다.")
    String getAccessToken(String code, String redirectUri);

    @Operation(summary = "카카오 유저 정보", description = "카카오에서 발급받은 토큰을 이용해 유저 정보를 요청하는 API 입니다.")
    HashMap<String, Object> getUserInfo(String accessToken);

    @Operation(summary = "카카오 유저 로그인", description = "카카오 유저 정보가 등록되어 있지 않다면 등록하고, 등록되어 있다면 서비스 토큰을 발급하는 API 입니다.")
    KakaoLoginResponseDTO loginKakaoUser(HashMap<String, Object> kakaoUserInfo);
}
