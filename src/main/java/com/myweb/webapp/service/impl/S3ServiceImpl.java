package com.myweb.webapp.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.myweb.webapp.service.S3Service;
import com.amazonaws.services.s3.model.ObjectMetadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 amazonS3;
    private MetricsServiceImpl metricsService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    public S3ServiceImpl(AmazonS3 amazonS3, MetricsServiceImpl metricsService) {
        this.amazonS3 = amazonS3;
        this.metricsService = metricsService;
    }

    @Override
    public String uploadFile(MultipartFile file, String key) throws IOException {
        // Metadata for file type
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        // Upload file to S3
        long startS3 = System.currentTimeMillis();
        amazonS3.putObject(new PutObjectRequest(this.bucketName, key, file.getInputStream(), metadata));
        long durationS3 = System.currentTimeMillis() - startS3;
        // Record the duration of the S3 call
        metricsService.recordS3Call("upload_pic", durationS3);

        // Return the file's URL
        return amazonS3.getUrl(this.bucketName, key).toString();
    }

    @Override
    public void deleteFile(String url) {
        String key = url.substring(url.lastIndexOf('/') + 1); // Extract the key from the URL
        long startS3 = System.currentTimeMillis();
        amazonS3.deleteObject(this.bucketName, key);
        long durationS3 = System.currentTimeMillis() - startS3;
        // Record the duration of the S3 call
        metricsService.recordS3Call("delete_pic", durationS3);
    }

}
