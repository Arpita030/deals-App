package com.dealsfinder.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CashbackMessage {
    private String userEmail;
    private long dealId;
    private double cashbackAmount;
}

