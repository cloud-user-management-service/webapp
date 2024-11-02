package com.myweb.webapp.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.myweb.webapp.entity.ImageMetadata;
import com.myweb.webapp.entity.User;


public interface ImageService {

    ImageMetadata uploadProfilePicture(MultipartFile profilePic, User user) throws Exception;

    Optional<ImageMetadata> getProfilePictureMetadata(User user);

    boolean deleteProfilePicture(User user) throws Exception;


}
