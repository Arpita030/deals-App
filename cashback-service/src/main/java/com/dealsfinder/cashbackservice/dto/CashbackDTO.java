package com.dealsfinder.cashbackservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CashbackDTO {
    private String dealId;
    private double cashbackAmount;
    private LocalDateTime timestamp;
}
