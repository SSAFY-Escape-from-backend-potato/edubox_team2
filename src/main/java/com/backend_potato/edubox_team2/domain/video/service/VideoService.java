package com.backend_potato.edubox_team2.domain.video.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final S3Client s3Client;
    private final String bucketName = "";

    public String uploadVideo(MultipartFile video) throws IOException {
        String fileName = UUID.randomUUID().toString()+"_"+video.getOriginalFilename();

    }
}
