package com.dealsfinder.notificationservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Notification Controller Working!");
    }
}

