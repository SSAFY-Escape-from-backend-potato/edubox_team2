package com.backend_potato.edubox_team2.domain.video.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class VideoUploadRequestDTO {
    private String title;
    private String description;
    private MultipartFile videoFile;
}
