package com.expencetracker.authservice.util;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponseHandler {
    private String message;
    private int status;
    private Object data;
    private Instant instant= Instant.now();
}
