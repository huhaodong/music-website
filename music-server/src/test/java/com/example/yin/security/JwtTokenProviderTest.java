package com.example.yin.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=test-secret-key-for-jwt-token-generation-minimum-32-characters-long",
    "jwt.access-token-expiration=7200000",
    "jwt.refresh-token-expiration=604800000"
})
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider tokenProvider;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = new UsernamePasswordAuthenticationToken(
            "testuser",
            null,
            Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
            )
        );
    }

    @Test
    void 生成Token_应包含正确的用户信息() {
        String token = tokenProvider.generateAccessToken(authentication);
        String username = tokenProvider.getUsernameFromToken(token);
        assertEquals("testuser", username);
    }

    @Test
    void 生成Token_应包含角色信息() {
        String token = tokenProvider.generateAccessToken(authentication);
        String roles = tokenProvider.getRolesFromToken(token);
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

    @Test
    void 生成Token_应设置正确的过期时间() {
        String token = tokenProvider.generateAccessToken(authentication);
        assertFalse(tokenProvider.isTokenExpired(token));
    }

    @Test
    void 解析有效Token_应返回用户信息() {
        String token = tokenProvider.generateAccessToken(authentication);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("testuser", tokenProvider.getUsernameFromToken(token));
    }

    @Test
    void 解析篡改Token_应返回false() {
        String token = tokenProvider.generateAccessToken(authentication) + "tampered";
        assertFalse(tokenProvider.validateToken(token));
    }

    @Test
    void 生成刷新令牌应成功() {
        String refreshToken = tokenProvider.generateRefreshToken("testuser");
        assertNotNull(refreshToken);
        assertTrue(tokenProvider.validateToken(refreshToken));
    }

    @Test
    void 根据用户名和角色生成Token应成功() {
        String token = tokenProvider.generateAccessToken("admin", "ROLE_ADMIN");
        assertEquals("admin", tokenProvider.getUsernameFromToken(token));
        assertEquals("ROLE_ADMIN", tokenProvider.getRolesFromToken(token));
    }
}