package com.myweb.webapp.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Images")
public class ImageMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "id")
    private UUID id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "url", nullable = false)
    private String url;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "upload_date", updatable = false)
    private LocalDate uploadDate;

    @OneToOne
    @JoinColumn(name = "user", referencedColumnName = "id", nullable = true)
    private User user;

}
