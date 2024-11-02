package com.myweb.webapp.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myweb.webapp.entity.ImageMetadata;

public interface ImageRepository extends JpaRepository<ImageMetadata, UUID> {

    Optional<ImageMetadata> findByUserId(UUID userId);

}
