package com.example.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.back.entities.auth.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {}
