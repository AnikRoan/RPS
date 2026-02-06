package com.rpsB.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    private String id;
    @ManyToOne
    private User user;
    private Instant expiresAt;
    private boolean revoked;
}
