package com.backend_potato.edubox_team2.domain.users.controller;

import com.backend_potato.edubox_team2.domain.users.entity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User API", description = "회원 API입니다.")
public interface UserController {

    @Operation(summary = "회원 가입", description = "새로운 회원을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "회원 가입에 성공하였습니다.")
    @PostMapping("/signup")
    ResponseEntity<Void> createUser(@RequestBody SignupRequestDTO signupRequestDTO);

    @Operation(summary = "이메일 인증 링크 검증", description = "이메일로 전송된 인증 링크를 클릭하면 해당 토큰을 검증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "유효하지 않거나 만료된 토큰입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생", content = @Content)
    })
    @GetMapping("/verify-link")
    ResponseEntity<String> verifyLink(@RequestParam("token") String token);

    @Operation(summary="로그인", description= "사용자가 로그인 시 사용되는 api입니다.")
    @ApiResponse(responseCode = "201", description = "로그인에 성공하였습니다.")
    @PostMapping("/login")
    ResponseEntity<String> getUserByEmailAndPw(@RequestBody @Valid LoginRequestDTO loginRequestDTO, HttpServletResponse response);

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃에 성공했습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생", content = @Content)
    })
    @PostMapping("/logout")
    ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response);


    @Operation(summary = "프로필 업데이트", description = "프로필 업데이트 시 사용되는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 업데이트에 성공했습니다."),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생", content = @Content)
    })
    @PatchMapping(value = "/update-profile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> updateProfile(
            @RequestPart(value = "request") ProfileUpdateRequestDTO profileUpdateRequestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request);

    @Operation(summary = "사용자 복구", description = "Soft-Delete 상태의 사용자를 복구합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 복구 성공"),
            @ApiResponse(responseCode = "404", description = "해당 이메일을 가진 사용자가 존재하지 않음", content = @Content),
            @ApiResponse(responseCode = "409", description = "사용자가 이미 활성화 상태임", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생", content = @Content)
    })
    @PatchMapping("/restore/{email}")
    ResponseEntity<Void> restoreUser(@PathVariable("email") String email);

    @Operation(summary = "사용자 Soft-Delete", description = "현재 로그인된 사용자를 Soft-Delete 처리하고 자동으로 로그아웃합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Soft-Delete 성공 및 사용자 로그아웃 완료"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "409", description = "사용자가 이미 Soft-Delete 상태임", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생", content = @Content)
    })
    @DeleteMapping("/soft-delete")
    ResponseEntity<Void> softDeleteUser(HttpServletRequest request);

    @Operation(summary = "이메일 중복 확인", description = "입력한 이메일이 이미 등록되어 있는지 확인합니다.")
    @ApiResponse(responseCode = "200", description = "이메일 중복 확인에 성공하였습니다.")
    @Parameters(value = {
            @Parameter(name="email", description = "사용자 이메일", example = "spancer1@naver.com")
    })
    @GetMapping("/check-email")
    ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam("email") String email);


    @Operation(summary = "닉네임 중복 확인", description = "입력한 닉네임이 이미 등록되어 있는지 확인합니다")
    @ApiResponse(responseCode = "200", description = "닉네임 중복 확인에 성공하였습니다.")
    @Parameters(value = {
            @Parameter(name="nickname", description = "닉네임", example = "KSH00610")
    })
    @GetMapping("/check-nickname")
    ResponseEntity<Boolean> checkNicknameDuplicate(@RequestParam("nickname") String nickname);

    @Operation(summary = "프로필 주소 중복 확인", description = "입력한 프로필 주소가 이미 등록되어 있는지 확인합니다")
    @ApiResponse(responseCode = "200", description = "프로필 주소 중복 확인에 성공하였습니다.")
    @Parameters(value = {
            @Parameter(name="profileAddress", description = "프로필 주소", example = "KSH")
    })
    @GetMapping("/check-profileAddress")
    ResponseEntity<Boolean> checkProfileAddressDuplicate(@RequestParam("profileAddress") String profileAddress);
}
