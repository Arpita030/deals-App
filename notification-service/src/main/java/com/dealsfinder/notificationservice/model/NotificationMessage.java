package com.dealsfinder.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
//    private String recipient;
//    private String message;
//    private String type; // e.g., EMAIL, SMS, PUSH
//}
private String recipient;  // user's email
    private String subject;    // email subject
    private String message;    // email body
}
