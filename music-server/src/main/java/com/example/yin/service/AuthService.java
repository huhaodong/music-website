package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.PermissionMapper;
import com.example.yin.mapper.RolePermissionMapper;
import com.example.yin.mapper.UserRoleMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Permission;
import com.example.yin.model.domain.RolePermission;
import com.example.yin.model.domain.UserRole;
import com.example.yin.model.request.AuthRequest;
import com.example.yin.model.response.AuthResponse;
import com.example.yin.security.JwtTokenProvider;
import com.example.yin.security.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private TokenBlacklistService blacklistService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConsumerMapper consumerMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private PermissionMapper permissionMapper;

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
        Consumer consumer = findConsumerByUsername(username);
        if (consumer == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(password, consumer.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        boolean hasAdminPermission = checkUserHasPermission(consumer.getId(), "system:admin:login");
        if (!hasAdminPermission) {
            throw new RuntimeException("无后台访问权限");
        }

        List<String> roles = getUserRoles(consumer.getId(), "consumer");
        String rolesStr = roles.isEmpty() ? "ROLE_USER" : String.join(",", roles);
        String accessToken = tokenProvider.generateAccessToken(username, rolesStr);
        String refreshToken = tokenProvider.generateRefreshToken(username);

        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(consumer.getId(), username, rolesStr);
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

        if (!passwordEncoder.matches(password, consumer.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        List<String> roles = getUserRoles(consumer.getId(), "consumer");
        String rolesStr = roles.isEmpty() ? "ROLE_USER" : String.join(",", roles);
        String accessToken = tokenProvider.generateAccessToken(username, rolesStr);
        String refreshToken = tokenProvider.generateRefreshToken(username);

        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(consumer.getId(), username, rolesStr);
        AuthResponse response = new AuthResponse(accessToken, refreshToken,
                tokenProvider.getAccessTokenExpiration() / 1000);
        response.setUser(userInfo);
        return response;
    }

    private boolean checkUserHasPermission(Integer userId, String permissionCode) {
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.eq("user_id", userId);
        userRoleQueryWrapper.eq("user_type", "consumer");
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleQueryWrapper);

        if (userRoles.isEmpty()) {
            return false;
        }

        for (UserRole userRole : userRoles) {
            QueryWrapper<RolePermission> rpQueryWrapper = new QueryWrapper<>();
            rpQueryWrapper.eq("role_id", userRole.getRoleId());
            List<RolePermission> rolePermissions = rolePermissionMapper.selectList(rpQueryWrapper);

            for (RolePermission rp : rolePermissions) {
                QueryWrapper<Permission> permQueryWrapper = new QueryWrapper<>();
                permQueryWrapper.eq("id", rp.getPermissionId());
                permQueryWrapper.eq("code", permissionCode);
                Long count = permissionMapper.selectCount(permQueryWrapper);
                if (count > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<String> getUserRoles(Integer userId, String userType) {
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.eq("user_id", userId);
        userRoleQueryWrapper.eq("user_type", userType);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleQueryWrapper);

        List<String> roles = new ArrayList<>();
        for (UserRole ur : userRoles) {
            roles.add("ROLE_" + ur.getRoleId());
        }
        return roles;
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

    private Consumer findConsumerByUsername(String username) {
        QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return consumerMapper.selectOne(queryWrapper);
    }
}
