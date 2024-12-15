package com.backend_potato.edubox_team2.domain.users.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyCodeRequestDTO {
    @NotBlank(message = "이메일은 필수 항목입니다.")
    @Email(message = "유효한 이메일 주소를 입력해야 합니다.")
    private String email;

    @NotBlank(message = "인증 코드는 필수 항목입니다.")
    private String code;
}
