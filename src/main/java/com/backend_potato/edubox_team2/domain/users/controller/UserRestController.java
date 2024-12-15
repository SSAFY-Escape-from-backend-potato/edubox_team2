package com.backend_potato.edubox_team2.domain.users.controller;

import com.backend_potato.edubox_team2.domain.users.entity.*;
import com.backend_potato.edubox_team2.domain.users.service.EmailService;
import com.backend_potato.edubox_team2.domain.users.service.EmailServiceImpl;
import com.backend_potato.edubox_team2.domain.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    //private final JWTUtil jwtUtil;

    public UserRestController(UserService userService, EmailService emailService) { // 생성자 명시적 선언
        this.userService = userService;
        this.emailService=emailService;
    }
    @Override
    @PostMapping("/signup")
    public ResponseEntity<Void> createUser(@RequestBody SignupRequestDTO signupRequestDTO) {
        userService.createUser(signupRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @PostMapping("/loginup")
    public ResponseEntity<String> getUserByEmailAndPw(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        String token = userService.getUserByEmailAndPw(loginRequestDTO);
        return ResponseEntity.ok(token);
    }

    @Override
    @PatchMapping("/update-profile")
    public ResponseEntity<Void> updateProfile(@RequestPart(required = false) MultipartFile image,
                                              @RequestPart ProfileUpdateRequestDTO profileUpdateRequestDTO) {
        userService.updateProfile(image,profileUpdateRequestDTO);
        return null;
    }
    @PostMapping("/send-verification")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody EmailVerificationRequestDTO requestDTO) {
        emailService.sendVerificationCode(requestDTO.getEmail());
        return ResponseEntity.ok("Verification email sent.");
    }

    // 2. Create another API endpoint in UserRestController to verify the code.
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyEmailCode(@RequestBody VerifyCodeRequestDTO requestDTO) {
        boolean isValid = emailService.verifyCode(requestDTO.getEmail(), requestDTO.getCode());
        if (isValid) {
            return ResponseEntity.ok("Email verified successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code.");
        }
    }

//    @Override
//    @PostMapping("/logout")
//    public ResponseEntity<Void> logout(HttpServletRequest request) {
//        String token = jwtUtil.validateAndReturnToken(request.getHeader("Authorization"));
//
//        userService.logout(token);
//
//        return ResponseEntity.ok().build();
//    }

}
