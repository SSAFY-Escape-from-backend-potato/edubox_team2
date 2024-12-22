package com.backend_potato.edubox_team2.domain.users.controller;

import com.backend_potato.edubox_team2.domain.users.entity.*;
import com.backend_potato.edubox_team2.domain.users.service.EmailService;
import com.backend_potato.edubox_team2.domain.users.service.KakaoService;
import com.backend_potato.edubox_team2.domain.users.service.UserService;
import com.backend_potato.edubox_team2.global.jwt.JwtFilter;
import com.backend_potato.edubox_team2.global.jwt.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/user")
//@RequiredArgsConstructor
@Slf4j
public class UserRestController implements UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final KakaoService kakaoService;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtFilter jwtFilter;


    public UserRestController(UserService userService, EmailService emailService, KakaoService kakaoService, JwtTokenUtil jwtTokenUtil, JwtFilter jwtFilter) { // 생성자 명시적 선언
        this.userService = userService;
        this.emailService=emailService;
        this.kakaoService=kakaoService;
        this.jwtTokenUtil=jwtTokenUtil;
        this.jwtFilter=jwtFilter;
    }
    @Override
    @PostMapping("/signup")
    public ResponseEntity<Void> createUser(@RequestBody SignupRequestDTO signupRequestDTO) {
        userService.createUser(signupRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Override
    @GetMapping("/verify-link")
    public ResponseEntity<String> verifyLink(@RequestParam("token") String token) {
        try {
            String message = userService.verifyEmailLink(token);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<String> getUserByEmailAndPw(@RequestBody @Valid LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        User user = userService.authenticate(loginRequestDTO);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        if (user.getRole() == Role.UNAUTH) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email verification required. Check your email.");
        }
        String accessToken = jwtTokenUtil.generateAccessToken(user);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user);

        // Refresh Token 쿠키 설정
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshCookie);

        // Access Token은 in-memory 저장소에 저장
        //userService.storeAccessToken(user.getEmail(), accessToken);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken) // Access Token 헤더에 포함
                .header("Set-Cookie", refreshCookie.toString())   // Refresh Token 쿠키 설정
                .body("로그인 성공");
    }

    @Override
    @GetMapping("/oauth/kakao")
    public ResponseEntity<KakaoLoginResponseDTO> kakaoLogin(@RequestParam("code") String code, HttpServletRequest request){
        try{
            String currentDomain = request.getServerName();
            return ResponseEntity.ok(kakaoService.login(code, currentDomain));
        }catch(NullPointerException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "올바르지 않은 유저입니다.");
        }
    }


    @Override
    @PatchMapping(value = "/update-profile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProfile(
            @RequestPart(value = "request") ProfileUpdateRequestDTO profileUpdateRequestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request) {

        try {
            userService.updateProfile(image, profileUpdateRequestDTO, request);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @Override
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtFilter.resolveToken(request);

        if (accessToken == null || !jwtTokenUtil.isValidToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are already logged out.");
        }
        userService.removeAccessToken(accessToken);
        // Refresh Token 쿠키 삭제
        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0); // 즉시 만료
        response.addCookie(refreshCookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    @PatchMapping("/restore/{email}")
    public ResponseEntity<Void> restoreUser(@PathVariable("email") String email) {
        try {
            userService.restoreUser(email);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }
    @DeleteMapping("/soft-delete")
    public ResponseEntity<Void> softDeleteUser(HttpServletRequest request) {
        try {
            // JwtTokenUtil을 사용하여 토큰에서 이메일 추출
            String email = jwtTokenUtil.getUserEmailFromToken(jwtFilter.resolveToken(request));
            // Soft-Delete 수행
            userService.softDeleteUserByEmail(email);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam("email") String email) {
        return ResponseEntity.ok().body(userService.existsByEmail(email));
    }
    @Override
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@RequestParam("nickname") String nickname){
        return ResponseEntity.ok().body(userService.existsByNickname(nickname));
    }

    @Override
    @GetMapping("/check-profileAddress")
    public ResponseEntity<Boolean> checkProfileAddressDuplicate(@RequestParam("profileAddress") String profileAddress) {
        return ResponseEntity.ok().body(userService.existsByProfileAddress(profileAddress));
    }


//    @PostMapping("/send-verification")
//    public ResponseEntity<String> sendVerificationEmail(@RequestBody EmailVerificationRequestDTO requestDTO) {
//        emailService.sendVerificationCode(requestDTO.getEmail());
//        return ResponseEntity.ok("Verification email sent.");
//    }
//
//
//    @PostMapping("/verify-code")
//    public ResponseEntity<String> verifyEmailCode(@RequestBody VerifyCodeRequestDTO requestDTO) {
//        boolean isValid = emailService.verifyCode(requestDTO.getEmail(), requestDTO.getCode());
//        if (isValid) {
//            return ResponseEntity.ok("Email verified successfully.");
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code.");
//        }
//    }
}
