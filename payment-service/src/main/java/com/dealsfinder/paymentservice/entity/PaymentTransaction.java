package com.dealsfinder.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    private String transactionId;

    private String userEmail;
    private String dealId;
    private String status;
    private String amount;
    private String paymentMethod;
    private LocalDateTime createdAt;
}
