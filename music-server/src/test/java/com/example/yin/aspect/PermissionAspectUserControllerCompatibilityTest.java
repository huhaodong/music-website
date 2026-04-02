package com.example.yin.aspect;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.controller.UserController;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.PermissionMapper;
import com.example.yin.mapper.RolePermissionMapper;
import com.example.yin.mapper.UserRoleMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Permission;
import com.example.yin.model.domain.RolePermission;
import com.example.yin.model.domain.UserRole;
import com.example.yin.model.request.ConsumerRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController 权限码兼容性测试")
class PermissionAspectUserControllerCompatibilityTest {

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private RolePermissionMapper rolePermissionMapper;

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private ConsumerMapper consumerMapper;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @InjectMocks
    private PermissionAspect permissionAspect;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("admin 拥有 user:create 时可以调用 /system/user/add")
    void shouldAllowAddUserWhenUserHasLegacyCreatePermission() throws NoSuchMethodException {
        mockAuthenticationAsAdminConsumer("admin");
        mockPermissionChain("user:create");
        mockJoinPointMethod(UserController.class.getMethod("addUser", ConsumerRequest.class));

        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    @Test
    @DisplayName("缺少新增用户权限时应返回权限不足")
    void shouldDenyAddUserWhenPermissionNotMatched() throws NoSuchMethodException {
        mockAuthenticationAsAdminConsumer("admin");
        mockPermissionChain("user:list");
        mockJoinPointMethod(UserController.class.getMethod("addUser", ConsumerRequest.class));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> permissionAspect.checkPermission(joinPoint));
        assertEquals("权限不足", exception.getMessage());
    }

    private void mockJoinPointMethod(Method method) {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
    }

    private void mockAuthenticationAsAdminConsumer(String username) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_1"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Consumer consumer = new Consumer();
        consumer.setId(2);
        consumer.setUsername(username);
        when(consumerMapper.selectOne(any(QueryWrapper.class))).thenReturn(consumer);
    }

    private void mockPermissionChain(String permissionCode) {
        UserRole userRole = new UserRole();
        userRole.setId(1);
        userRole.setUserId(2);
        userRole.setUserType("consumer");
        userRole.setRoleId(1);
        when(userRoleMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(userRole));

        RolePermission rolePermission = new RolePermission();
        rolePermission.setId(1);
        rolePermission.setRoleId(1);
        rolePermission.setPermissionId(11);
        when(rolePermissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(rolePermission));

        Permission permission = new Permission();
        permission.setId(11);
        permission.setCode(permissionCode);
        when(permissionMapper.selectBatchIds(anyList()))
                .thenReturn(Collections.singletonList(permission));
    }
}
