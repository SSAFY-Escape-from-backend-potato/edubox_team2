package com.backend_potato.edubox_team2.domain.users.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasicInfoRequestDTO {
    private String email;
    private String pw;
    private String checkPw;
    private String phone;
}
