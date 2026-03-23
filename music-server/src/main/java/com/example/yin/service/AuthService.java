package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.constant.Constants;
import com.example.yin.mapper.AdminMapper;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.model.domain.Admin;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.request.AuthRequest;
import com.example.yin.model.response.AuthResponse;
import com.example.yin.security.JwtTokenProvider;
import com.example.yin.security.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
public class AuthService {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private TokenBlacklistService blacklistService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private ConsumerMapper consumerMapper;

    public AuthResponse login(AuthRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String userType = request.getUserType();

        if ("admin".equals(userType)) {
            return loginAsAdmin(username, password);
        } else {
            return loginAsConsumer(username, password);
        }
    }

    private AuthResponse loginAsAdmin(String username, String password) {
        Admin admin = findAdminByUsername(username);
        if (admin == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        String roles = "ROLE_ADMIN";
        String accessToken = tokenProvider.generateAccessToken(username, roles);
        String refreshToken = tokenProvider.generateRefreshToken(username);

        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(admin.getId(), username, roles);
        AuthResponse response = new AuthResponse(accessToken, refreshToken,
                tokenProvider.getAccessTokenExpiration() / 1000);
        response.setUser(userInfo);
        return response;
    }

    private AuthResponse loginAsConsumer(String username, String password) {
        Consumer consumer = findConsumerByUsername(username);
        if (consumer == null) {
            throw new RuntimeException("用户不存在");
        }
        String secretPassword = DigestUtils.md5DigestAsHex((Constants.SALT + password).getBytes(StandardCharsets.UTF_8));
        if (!secretPassword.equals(consumer.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        String roles = "ROLE_USER";
        String accessToken = tokenProvider.generateAccessToken(username, roles);
        String refreshToken = tokenProvider.generateRefreshToken(username);

        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(consumer.getId(), username, roles);
        AuthResponse response = new AuthResponse(accessToken, refreshToken,
                tokenProvider.getAccessTokenExpiration() / 1000);
        response.setUser(userInfo);
        return response;
    }

    public AuthResponse refresh(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("无效的刷新令牌");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        String newAccessToken = tokenProvider.generateAccessToken(username, "ROLE_USER");
        String newRefreshToken = tokenProvider.generateRefreshToken(username);

        return new AuthResponse(newAccessToken, newRefreshToken,
                tokenProvider.getAccessTokenExpiration() / 1000);
    }

    public void logout(String token) {
        long expiration = getTokenExpiration(token);
        if (expiration > 0) {
            blacklistService.blacklistToken(token, expiration);
        }
    }

    public AuthResponse.UserInfo getCurrentUser(String token) {
        String username = tokenProvider.getUsernameFromToken(token);
        String roles = tokenProvider.getRolesFromToken(token);
        return new AuthResponse.UserInfo(null, username, roles);
    }

    private long getTokenExpiration(String token) {
        try {
            return tokenProvider.getAccessTokenExpiration();
        } catch (Exception e) {
            return 0;
        }
    }

    private Admin findAdminByUsername(String username) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", username);
        return adminMapper.selectOne(queryWrapper);
    }

    private Consumer findConsumerByUsername(String username) {
        QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return consumerMapper.selectOne(queryWrapper);
    }
}