package org.tpjava.AuthService.exception;

import java.time.LocalDateTime;

public class CustomErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private String details;

    public CustomErrorResponse(LocalDateTime timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    // Getters and setters
}
