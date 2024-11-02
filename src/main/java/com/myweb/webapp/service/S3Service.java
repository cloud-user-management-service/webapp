package com.myweb.webapp.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    String uploadFile(MultipartFile file, String key) throws IOException;

    void deleteFile(String url);

}
