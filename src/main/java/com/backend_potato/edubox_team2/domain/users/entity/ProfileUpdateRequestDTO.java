package com.backend_potato.edubox_team2.domain.users.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequestDTO {
    private String nickname;
    private String profileAddress;
    private String discription ;
}
