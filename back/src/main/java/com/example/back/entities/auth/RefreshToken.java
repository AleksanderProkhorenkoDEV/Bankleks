package com.example.back.entities.auth;

import java.time.Instant;

import com.example.back.entities.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    public RefreshToken() {
    }

    public RefreshToken(User user, String token, Instant expiryDate) {

        if (user == null)
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        if (token == null || token.isBlank())
            throw new IllegalArgumentException("El token no puede ser nulo ni estar vacio");
        if (expiryDate == null)
            throw new IllegalArgumentException("La fecha de expiración no puede ser nula.");



        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

}
