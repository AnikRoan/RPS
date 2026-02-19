package com.rpsB.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
public class RefreshToken {
    @Id
    private String id;
    @ManyToOne
    private User user;
    private Instant expiresAt;
    private Instant createdAt;
    private boolean revoked;
}
