package com.example.yin.model.request;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
    private String userType; // "consumer" or "admin"
}