package com.myweb.webapp.service.impl;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.myweb.webapp.entity.ImageMetadata;
import com.myweb.webapp.entity.User;
import com.myweb.webapp.repository.ImageRepository;
import com.myweb.webapp.repository.UserRepository;
import com.myweb.webapp.service.ImageService;
import com.myweb.webapp.service.S3Service;

@Service
public class ImageServiceImpl implements ImageService {

    private final S3Service s3Service; // Assume you have a service to handle S3 operations
    private final ImageRepository imageRepository; // Repository for saving metadata to the database
    private UserRepository userRepo;

    @Autowired
    public ImageServiceImpl(S3Service s3Service, ImageRepository imageRepository, UserRepository userRepo) {
        this.s3Service = s3Service;
        this.imageRepository = imageRepository;
        this.userRepo = userRepo;
    }

    @Override
    public ImageMetadata uploadProfilePicture(MultipartFile profilePic, User user) throws Exception {
        // Retrieve the existing image metadata
        Optional<ImageMetadata> existingImageOptional = imageRepository.findByUserId(user.getId());

        // Delete the existing image from S3 and the database
        if (existingImageOptional.isPresent()) {
            ImageMetadata existingImage = existingImageOptional.get();
            // Delete from S3
            s3Service.deleteFile(existingImage.getUrl());
            // Remove the existing metadata from the database
            imageRepository.delete(existingImage);
        }

        // Generate a unique ID for the image
        UUID imageId = UUID.randomUUID();

        // Upload the file to S3 and get the URL
        String s3Url = s3Service.uploadFile(profilePic, user.getId().toString());

        // Create and save the metadata
        ImageMetadata metadata = new ImageMetadata();
        metadata.setFileName(profilePic.getOriginalFilename());
        metadata.setId(imageId);
        metadata.setUrl(s3Url);
        metadata.setUser(user);
        metadata.setUploadDate(LocalDate.now());
        metadata.setUserId(user.getId());

        // Save metadata to the database
        imageRepository.save(metadata);

        return metadata;
    } 

    @Override
    public Optional<ImageMetadata> getProfilePictureMetadata(User user) {
        // Fetch the metadata from the database for the user
        return imageRepository.findByUserId(user.getId());
    }

    @Override
    public boolean deleteProfilePicture(User user) throws Exception {
        // Fetch the image metadata for the user
        Optional<ImageMetadata> imageMetadataOptional = imageRepository.findByUserId(user.getId());

        if (imageMetadataOptional.isPresent()) {
            ImageMetadata imageMetadata = imageMetadataOptional.get();

            // Delete from S3
            s3Service.deleteFile(imageMetadata.getUrl());

            // Delete from the database
            imageRepository.delete(imageMetadata);
            
            return true;
        }

        return false; // Return false if no metadata was found
    }

}
