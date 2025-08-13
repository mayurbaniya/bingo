package com.hp.bingo.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntryForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private String email;
    private int tickets;
    private String imagePath; // NEW: local path of payment proof


    @Column(unique = true, length = 6, nullable = false)
    private String registrationId;

    private String status; // 0: Deleted, 1: Active, 99: Fraud
    private long amountPaid; // in paise
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private Instant createdAt = Instant.now();
    private boolean paymentConfirmed = false;
}
