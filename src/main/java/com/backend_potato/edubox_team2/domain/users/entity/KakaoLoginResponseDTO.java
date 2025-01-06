package com.backend_potato.edubox_team2.domain.users.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoLoginResponseDTO {
    private Long id;
    private String imageUrl;
    private String nickname;
    private String email;
    private String accessToken;
    private String refreshToken;
}
