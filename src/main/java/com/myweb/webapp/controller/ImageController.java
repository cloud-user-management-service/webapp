package com.myweb.webapp.controller;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.myweb.webapp.dto.CustomUserDetails;
import com.myweb.webapp.dto.UserProfileResponse;
import com.myweb.webapp.entity.ImageMetadata;
import com.myweb.webapp.entity.User;
import com.myweb.webapp.service.ImageService;
import com.myweb.webapp.service.MetricsService;
import com.myweb.webapp.service.UserService;

import jakarta.servlet.http.HttpServletRequest;


import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/v1/user/self/pic")
@Log4j2
public class ImageController {
    ImageService imageService;
    UserService userService;
    MetricsService metricsService;

    public ImageController(ImageService imageService, UserService userService, MetricsService metricsService) {
        this.imageService = imageService;
        this.userService = userService;
        this.metricsService = metricsService;
    }

    @PostMapping
    public ResponseEntity<UserProfileResponse> uploadProfilePicture(@RequestParam(value="profilePic", required = false) MultipartFile profilePic, HttpServletRequest request) {
        long start = System.currentTimeMillis();

        if (profilePic == null || !request.getContentType().startsWith("multipart/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(null); // Optionally, return an error message here
        }

        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        User user = customUserDetails.getUser();

        try {
            ImageMetadata imageMetadata = imageService.uploadProfilePicture(profilePic, user);

            // Map the metadata to ImageResponse
            UserProfileResponse imageResponse = new UserProfileResponse(
                    imageMetadata.getFileName(),
                    imageMetadata.getId(),
                    imageMetadata.getUrl(),
                    imageMetadata.getUploadDate(),
                    imageMetadata.getUserId());

            long duration = System.currentTimeMillis() - start;
            metricsService.recordApiCall("upload pic", duration);
            return ResponseEntity.status(HttpStatus.CREATED).body(imageResponse);

        } catch (Exception e) {
            System.out.println(e);
            long duration = System.currentTimeMillis() - start;
            metricsService.recordApiCall("upload pic", duration);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfilePicture(HttpServletRequest request) {
        long start = System.currentTimeMillis();

        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        User user = customUserDetails.getUser();

        // Retrieve the image metadata for the user
        long startDB = System.currentTimeMillis();
        Optional<ImageMetadata> imageMetadataOptional = imageService.getProfilePictureMetadata(user);
        long durationDB = System.currentTimeMillis() - startDB;
        metricsService.recordDatabaseQuery("get pic", durationDB);

        if (imageMetadataOptional.isPresent()) {
            ImageMetadata imageMetadata = imageMetadataOptional.get();

            // Map the metadata to ImageResponse
            UserProfileResponse imageResponse = new UserProfileResponse(
                    imageMetadata.getFileName(),
                    imageMetadata.getId(),
                    imageMetadata.getUrl(),
                    imageMetadata.getUploadDate(),
                    imageMetadata.getUserId());
            long duration = System.currentTimeMillis() - start;
            metricsService.recordApiCall("get pic", duration);
            return ResponseEntity.status(HttpStatus.OK).body(imageResponse);
        }
        long duration = System.currentTimeMillis() - start;
        metricsService.recordApiCall("get pic", duration);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProfilePicture(HttpServletRequest request) {
        long start = System.currentTimeMillis();

        UserDetails userDetails = (UserDetails) request.getAttribute("userDetails");

        if (userDetails == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        User user = customUserDetails.getUser();

        try {
            // Delete the image metadata and the image from S3
            boolean deleted = imageService.deleteProfilePicture(user);

            if (deleted) {
                long duration = System.currentTimeMillis() - start;
                metricsService.recordApiCall("delete pic", duration);
                return ResponseEntity.noContent().build(); // 204 No Content
            } else {
                long duration = System.currentTimeMillis() - start;
                metricsService.recordApiCall("delete pic", duration);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
            }
        } catch (Exception e) {
            System.out.println(e);
            long duration = System.currentTimeMillis() - start;
            metricsService.recordApiCall("delete pic", duration);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 500 Internal Server Error
        }
    }

}
