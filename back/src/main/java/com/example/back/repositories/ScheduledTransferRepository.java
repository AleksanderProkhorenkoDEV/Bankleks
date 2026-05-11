package com.example.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.back.entities.transactions.ScheduledTransfer;

@Repository
public interface ScheduledTransferRepository extends JpaRepository<ScheduledTransfer, Long> {}
