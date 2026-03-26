package com.example.yin.aspect;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.annotation.RequirePermission;
import com.example.yin.mapper.PermissionMapper;
import com.example.yin.mapper.RolePermissionMapper;
import com.example.yin.mapper.UserRoleMapper;
import com.example.yin.model.domain.Permission;
import com.example.yin.model.domain.RolePermission;
import com.example.yin.model.domain.UserRole;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Admin;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.AdminMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private ConsumerMapper consumerMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Before("@annotation(com.example.yin.annotation.RequirePermission)")
    public void checkPermission(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);

        if (requirePermission == null) {
            requirePermission = joinPoint.getTarget().getClass().getAnnotation(RequirePermission.class);
        }

        if (requirePermission == null) {
            return;
        }

        String[] codes = getPermissionCodes(requirePermission);
        if (codes == null || codes.length == 0) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }

        String username = authentication.getName();
        String userType = getUserTypeFromAuthorities(authentication.getAuthorities());

        Integer userId = findUserIdByUsername(username);
        if (userId == null) {
            userId = findAdminIdByName(username);
        }

        if (userId == null) {
            throw new RuntimeException("用户未找到");
        }

        List<String> userPermissions = getUserPermissions(userId, userType);

        boolean hasPermission;
        if (requirePermission.requireAll()) {
            hasPermission = Arrays.stream(codes).allMatch(userPermissions::contains);
        } else {
            hasPermission = Arrays.stream(codes).anyMatch(userPermissions::contains);
        }

        if (!hasPermission) {
            throw new RuntimeException("权限不足");
        }
    }

    private String getUserTypeFromAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return null;
        }
        return "consumer";
    }

    private String[] getPermissionCodes(RequirePermission requirePermission) {
        if (requirePermission.codes() != null && requirePermission.codes().length > 0) {
            return requirePermission.codes();
        }
        if (requirePermission.value() != null && requirePermission.value().length > 0) {
            return requirePermission.value();
        }
        return new String[0];
    }

    private Integer findUserIdByUsername(String username) {
        QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Consumer consumer = consumerMapper.selectOne(queryWrapper);
        return consumer != null ? consumer.getId() : null;
    }

    private Integer findAdminIdByName(String name) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        Admin admin = adminMapper.selectOne(queryWrapper);
        return admin != null ? admin.getId() : null;
    }

    private List<String> getUserPermissions(Integer userId, String userType) {
        if (userId == null || userType == null) {
            return new ArrayList<>();
        }

        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.eq("user_id", userId);
        userRoleQueryWrapper.eq("user_type", userType);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleQueryWrapper);

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        QueryWrapper<RolePermission> rolePermissionQueryWrapper = new QueryWrapper<>();
        rolePermissionQueryWrapper.in("role_id", roleIds);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(rolePermissionQueryWrapper);

        if (rolePermissions.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .distinct()
                .collect(Collectors.toList());

        List<Permission> permissions = permissionMapper.selectBatchIds(permissionIds);

        return permissions.stream()
                .map(Permission::getCode)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }
}
