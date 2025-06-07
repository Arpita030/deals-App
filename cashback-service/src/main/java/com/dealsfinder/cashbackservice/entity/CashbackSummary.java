package com.dealsfinder.cashbackservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashbackSummary {

    @Id
    private String userEmail;

    private double totalCashback;
}
