package com.backend_potato.edubox_team2.domain.video.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class VideoUploadResponseDTO {
    private String fileUrl;
    private String message;
}
