package com.dealsfinder.userservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;


@Data
public class Deal {

    @Id
    private Long id;

    private String title;
    private String description;
    private double discount;
    private String category;
    private LocalDateTime expiryDate;
    private boolean isActive;
}
