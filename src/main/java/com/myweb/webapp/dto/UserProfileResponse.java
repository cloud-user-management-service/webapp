package com.myweb.webapp.dto;

import java.time.LocalDate;
import java.util.UUID;

public class UserProfileResponse {

    private final String fileName;
    private final UUID id;
    private final String url;
    private final LocalDate uploadDate;
    private final UUID userId;

    // Constructor to set final fields
    public UserProfileResponse(String fileName, UUID id, String url, LocalDate uploadDate, UUID userId) {
        this.fileName = fileName;
        this.id = id;
        this.url = url;
        this.uploadDate = uploadDate;
        this.userId = userId;
    }

    // Getters only, no setters
    public String getFileName() {
        return fileName;
    }

    public UUID getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public UUID getUserId() {
        return userId;
    }
}
