package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.model.request.AuthRequest;
import com.example.yin.model.response.AuthResponse;
import com.example.yin.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<R> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(R.success("登录成功", response));
        } catch (Exception e) {
            return ResponseEntity.ok(R.error(e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<R> refresh(@RequestBody String refreshToken) {
        try {
            AuthResponse response = authService.refresh(refreshToken);
            return ResponseEntity.ok(R.success("刷新成功", response));
        } catch (Exception e) {
            return ResponseEntity.ok(R.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<R> logout(@RequestHeader("Authorization") String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }
        return ResponseEntity.ok(R.success("登出成功"));
    }

    @GetMapping("/info")
    public ResponseEntity<R> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            AuthResponse.UserInfo userInfo = authService.getCurrentUser(token);
            return ResponseEntity.ok(R.success("获取成功", userInfo));
        }
        return ResponseEntity.ok(R.error("未授权"));
    }
}
