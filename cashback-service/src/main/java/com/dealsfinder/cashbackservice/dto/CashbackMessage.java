package com.dealsfinder.cashbackservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashbackMessage {
    private String userEmail;
    private String dealId;
    private double cashbackAmount;
}

