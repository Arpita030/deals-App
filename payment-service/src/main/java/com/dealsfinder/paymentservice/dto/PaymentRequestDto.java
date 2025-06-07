package com.dealsfinder.paymentservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentRequestDto {
    private String paymentMethodNonce;
    private String amount;
    private String userEmail;
    private String dealId;


}

