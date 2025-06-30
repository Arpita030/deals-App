//package com.dealsfinder.cashbackservice.dto;
//
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class NotificationMessage {
//    private String recipient;   // user's email
//    private String message;     // email body
//    private String type;        // type like "EMAIL", "SMS"
//}
package com.dealsfinder.cashbackservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private String recipient;  // Email address
    private String subject;    // Email subject
    private String message;    // Email body
}
