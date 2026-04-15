package com.example.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.back.entities.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {}
