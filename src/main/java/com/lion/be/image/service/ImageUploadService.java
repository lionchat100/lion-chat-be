package com.lion.be.image.service;

import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.image.domain.entity.Image;
import com.lion.be.image.repository.ImageRepository;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final S3Client s3Client;
    private final ImageRepository imageRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public Image upload(MultipartFile file, String dirName) throws IOException {
        if (file.isEmpty()) {
            throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
        }

        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = createUniqueFilename(originalFilename);
        String storedFilePath = dirName + "/" + uniqueFilename;

        uploadToS3(file, storedFilePath);
        String imageUrl = getImageUrl(storedFilePath);
        Image image = new Image(originalFilename, storedFilePath, imageUrl);

        return imageRepository.save(image);
    }

    private String createUniqueFilename(String originalFilename) {
        return UUID.randomUUID() + "_" + originalFilename;
    }

    private void uploadToS3(MultipartFile file, String key) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    private String getImageUrl(String key) {
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(key)).toExternalForm();
    }

    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.fetchById(imageId);
        deleteFromS3(image.getStoredFileName());
        imageRepository.deleteById(image.getId());
    }

    private void deleteFromS3(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

}