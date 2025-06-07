package com.dealsfinder.dealservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "deals")
@Data
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private double discount;
    private String category;
    private LocalDateTime expiryDate;
    private boolean isActive;
}
