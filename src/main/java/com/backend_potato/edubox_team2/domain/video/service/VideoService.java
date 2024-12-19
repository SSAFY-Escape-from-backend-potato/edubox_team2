package com.backend_potato.edubox_team2.domain.video.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final S3Client s3Client;
    private final String bucketName = "";

    public String uploadVideo(MultipartFile video) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + video.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try (InputStream inputStream = video.getInputStream()) {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, video.getSize()));
        }

        return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
    }
}
