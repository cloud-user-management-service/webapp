package com.myweb.webapp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Table(name = "EmailVerification")
public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "id")
    private UUID id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "email", nullable = false)
    private String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "token", nullable = false)
    private String token;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "expireTime", updatable = false)
    private LocalDateTime expireTime;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @OneToOne
    @JoinColumn(name = "user", referencedColumnName = "id", nullable = true)
    private User user;
}
