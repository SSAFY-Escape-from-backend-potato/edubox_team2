package com.backend_potato.edubox_team2.domain.users.controller;

import com.backend_potato.edubox_team2.domain.users.entity.*;
import com.backend_potato.edubox_team2.domain.users.service.EmailService;
import com.backend_potato.edubox_team2.domain.users.service.UserService;
import com.backend_potato.edubox_team2.global.jwt.JwtFilter;
import com.backend_potato.edubox_team2.global.jwt.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
//@RequiredArgsConstructor
@Slf4j
public class UserRestController implements UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final JwtTokenUtil jwtTokenUtil;

    private final JwtFilter jwtFilter;

    public UserRestController(UserService userService, EmailService emailService, JwtTokenUtil jwtTokenUtil, JwtFilter jwtFilter) { // 생성자 명시적 선언
        this.userService = userService;
        this.emailService=emailService;
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
    @PostMapping("/login")
    public ResponseEntity<String> getUserByEmailAndPw(@RequestBody @Valid LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
        User user = userService.authenticate(loginRequestDTO);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        String accessToken = jwtTokenUtil.generateAccessToken(user, 1000L*60*30);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user, 1000L*60*60*24*15);

        // Refresh Token 쿠키 설정
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
        response.addCookie(refreshCookie);

        // Access Token은 in-memory 저장소에 저장
        //userService.storeAccessToken(user.getEmail(), accessToken);

        //return ResponseEntity.ok(accessToken);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken) // Access Token 헤더에 포함
                .header("Set-Cookie", refreshCookie.toString())   // Refresh Token 쿠키 설정
                .body("로그인 성공");
    }



    @Override
    @PatchMapping("/update-profile")
    public ResponseEntity<Void> updateProfile(@RequestPart(required = false) MultipartFile image,
                                              @RequestPart ProfileUpdateRequestDTO profileUpdateRequestDTO) {
        userService.updateProfile(image,profileUpdateRequestDTO);
        return null;
    }
    @Override
    @PostMapping("/send-verification")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody EmailVerificationRequestDTO requestDTO) {
        emailService.sendVerificationCode(requestDTO.getEmail());
        return ResponseEntity.ok("Verification email sent.");
    }


    @Override
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyEmailCode(@RequestBody VerifyCodeRequestDTO requestDTO) {
        boolean isValid = emailService.verifyCode(requestDTO.getEmail(), requestDTO.getCode());
        if (isValid) {
            return ResponseEntity.ok("Email verified successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code.");
        }
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtFilter.resolveToken(request);

        if (accessToken != null) {
            // Access Token 삭제
            userService.removeAccessToken(accessToken);
        }

        // Refresh Token 쿠키 삭제
        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0); // 즉시 만료
        response.addCookie(refreshCookie);

        return ResponseEntity.ok("Logged out successfully");
    }

}
