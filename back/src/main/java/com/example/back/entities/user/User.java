package com.example.back.entities.user;

import com.example.back.entities.auth.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    public User() {
    }

    public User(String name, String email, String password, Role rol) {

        if(name == null) throw new IllegalArgumentException("El nombre no puede ser nulo");
        if(email == null) throw new IllegalArgumentException("El email no puede ser nulo");
        if(password == null) throw new IllegalArgumentException("El password no puede ser nulo");
        if(rol == null) throw new IllegalArgumentException("El rol no puede ser nulo");

        this.name = name;
        this.email = email;
        this.password = password;
        this.role = rol;
    }
}
