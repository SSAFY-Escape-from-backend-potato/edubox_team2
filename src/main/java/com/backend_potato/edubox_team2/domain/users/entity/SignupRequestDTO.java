package com.backend_potato.edubox_team2.domain.users.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class SignupRequestDTO {
    @Schema(description = "회원 이메일", example = "spancer1@naver.com")
    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "유효한 이메일 주소를 입력해야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
            message = "올바른 이메일 형식을 입력해야 합니다.")
    private String email;

    @Schema(description = "비밀번호", example = "rlatngus@1")
    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String pw;

    @Schema(description = "비밀번호", example = "rlatngus@1")
    @NotBlank(message = "비밀번호 확인은 필수 항목입니다.")
    private String confirmPw;
}
