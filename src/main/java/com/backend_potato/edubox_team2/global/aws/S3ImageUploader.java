package com.backend_potato.edubox_team2.global.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ImageUploader {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;
    @Value("${cloud.aws.region.static}")
    private String region;

    private S3Client s3Client;

    public S3Client getS3Client() {
        if (s3Client == null) {
            s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                    .build();
        }
        return s3Client;
    }

    public String upload(MultipartFile file) {
        String fileName = generateFileName(file.getOriginalFilename());
        Path tempFilePath = saveTempFile(file);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .acl("public-read") // 파일을 공개로 설정
                    .build();

            PutObjectResponse response = getS3Client().putObject(putObjectRequest, tempFilePath);
            if (response.sdkHttpResponse().isSuccessful()) {
                return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
            } else {
                throw new RuntimeException("S3에 파일 업로드 실패");
            }
        } finally {
            deleteTempFile(tempFilePath);
        }
    }

    private Path saveTempFile(MultipartFile file) {
        try {
            Path tempDir = Files.createTempDirectory("temp");
            Path tempFile = tempDir.resolve(file.getOriginalFilename());
            file.transferTo(tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException("임시 파일 저장 실패", e);
        }
    }

    private void deleteTempFile(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("임시 파일 삭제 실패", e);
        }
    }

    private String generateFileName(String originalFilename) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + fileExtension;
    }

//    public String upload(MultipartFile image) throws IOException  {
//        //입력받은 이미지 파일이 빈 파일인지 검증
//        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
//            throw new IOException("잘못된 입력");
//        }
//        //uploadImage를 호출하여 S3에 저장된 이미지의 public url을 반환한다.
//        return this.uploadImage(image);
//    }
//
//    private String uploadImage(MultipartFile image) throws IOException {
//        this.validateImageFileExtention(image.getOriginalFilename());
//        try {
//            return this.uploadImageToS3(image);
//        } catch (IOException e) {
//            throw new IOException("잘못된 입력");
//        }
//    }
//    private void validateImageFileExtention(String filename) throws IOException  {
//        int lastDotIndex = filename.lastIndexOf(".");
//        if (lastDotIndex == -1) {
//            throw new IOException("잘못된 입력");
//        }
//
//        String extention = filename.substring(lastDotIndex + 1).toLowerCase();
//        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");
//
//        if (!allowedExtentionList.contains(extention)) {
//            throw new IOException("잘못된 입력");
//        }
//    }
//    private String uploadImageToS3(MultipartFile image) throws IOException {
//        String originalFilename = image.getOriginalFilename(); //원본 파일 명
//        String extention = originalFilename.substring(originalFilename.lastIndexOf(".")); //확장자 명
//
//        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename; //변경된 파일 명
//
//        InputStream is = image.getInputStream();
//        byte[] bytes = IOUtils.toByteArray(is); //image를 byte[]로 변환
//
//        ObjectMetadata metadata = new ObjectMetadata(); //metadata 생성
//        metadata.setContentType("image/" + extention);
//        metadata.setContentLength(bytes.length);
//
//        //S3에 요청할 때 사용할 byteInputStream 생성
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
//
//        try{
//            //S3로 putObject 할 때 사용할 요청 객체
//            //생성자 : bucket 이름, 파일 명, byteInputStream, metadata
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(s3FileName)
//                    .contentType("image/" + extention)
//                    .acl(ObjectCannedACL.PUBLIC_READ)
//                    .build();
//
//            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes)); // put image to S3
//        }catch (Exception e){
//            throw new IOException("잘못된 입력");
//        }finally {
//            byteArrayInputStream.close();
//            is.close();
//        }
//
//        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, s3FileName);
//    }
//
//    public void deleteImageFromS3(String imageAddress){
//        String key = getKeyFromImageAddress(imageAddress);
//        try{
//            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(key)
//                    .build();
//            s3Client.deleteObject(deleteObjectRequest);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    private String getKeyFromImageAddress(String imageAddress){
//        try{
//            URL url = new URL(imageAddress);
//            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
//            return decodingKey.substring(1); // 맨 앞의 '/' 제거
//        }catch (MalformedURLException | UnsupportedEncodingException e){
//            e.printStackTrace();
//        }
//        return imageAddress;
//    }

}
