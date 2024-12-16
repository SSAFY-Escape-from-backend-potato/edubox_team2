package com.backend_potato.edubox_team2.domain.users.controller;

import com.backend_potato.edubox_team2.domain.users.entity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User API", description = "회원 API입니다.")
public interface UserController {

    @Operation(summary = "회원 가입", description = "새로운 회원을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "회원 가입에 성공하였습니다.")
    @PostMapping("/signup")
    ResponseEntity<Void> createUser(@RequestBody SignupRequestDTO signupRequestDTO);

    @Operation(summary="로그인", description= "사용자가 로그인 시 사용되는 api입니다.")
    @ApiResponse(responseCode = "201", description = "로그인에 성공하였습니다.")
    @PostMapping("/login")
    ResponseEntity<String> getUserByEmailAndPw(@RequestBody @Valid LoginRequestDTO loginRequestDTO, HttpServletResponse response);

    @Operation(summary="프로필 업데이트", description= "프로필 업데이트 시 사용되는 api입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 업데이트에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "해당 이메일을 가진 회원이 없습니다.", content = @Content(mediaType = "application/json"))
    })
    @PatchMapping("/update-profile")
    ResponseEntity<Void> updateProfile(
            @RequestPart(required = false) MultipartFile image,
            @RequestPart ProfileUpdateRequestDTO profileUpdateRequestDTO);


    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃에 성공했습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생", content = @Content)
    })
    @PostMapping("/logout")
    ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "이메일 인증 코드 전송", description = "사용자 이메일로 인증 코드를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 코드 전송에 성공했습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생", content = @Content)
    })
    @PostMapping("/send-verification")
    ResponseEntity<String> sendVerificationEmail(@RequestBody EmailVerificationRequestDTO requestDTO);

    @Operation(summary = "이메일 인증 코드 검증", description = "사용자가 입력한 인증 코드를 검증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 코드 검증에 성공했습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생", content = @Content)
    })
    @PostMapping("/verify-code")
    ResponseEntity<String> verifyEmailCode(@RequestBody VerifyCodeRequestDTO requestDTO);
}
