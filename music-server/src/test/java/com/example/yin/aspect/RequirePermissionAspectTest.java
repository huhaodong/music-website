package com.example.yin.aspect;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.annotation.RequirePermission;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.PermissionMapper;
import com.example.yin.mapper.RolePermissionMapper;
import com.example.yin.mapper.UserRoleMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Permission;
import com.example.yin.model.domain.RolePermission;
import com.example.yin.model.domain.UserRole;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * RequirePermission 注解和 PermissionAspect 切面权限验证逻辑测试
 * 测试场景覆盖：
 * 1. 有权限的用户访问 - 应允许
 * 2. 无权限的用户访问 - 应抛出权限不足异常
 * 3. 超级管理员访问 - 当前实现未特殊处理
 * 4. 未登录用户访问 - 应拒绝
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RequirePermission 权限切面测试")
class RequirePermissionAspectTest {

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

    private TestService testService;

    @BeforeEach
    void setUp() throws Exception {
        testService = new TestService();
        
        // 设置 joinPoint 的通用 mock
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getTarget()).thenReturn(testService);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("有权限的用户访问 - 应允许通过")
    void checkPermission_UserHasPermission_ShouldPass() throws Exception {
        setupAuthentication("testUser", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        setupUser("testUser", 1);
        setupUserPermissions(Arrays.asList("song:list"));

        Method method = TestService.class.getMethod("methodWithSongListPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    @Test
    @DisplayName("有权限的用户访问 - 多个权限任一匹配即可")
    void checkPermission_UserHasOneOfMultiplePermissions_ShouldPass() throws Exception {
        setupAuthentication("testUser", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        setupUser("testUser", 1);
        setupUserPermissions(Arrays.asList("song:add", "user:manage"));

        Method method = TestService.class.getMethod("methodWithAnyPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    @Test
    @DisplayName("有权限的用户访问 - requireAll 为 true 时拥有所有权限")
    void checkPermission_UserHasAllRequiredPermissions_ShouldPass() throws Exception {
        setupAuthentication("testUser", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        setupUser("testUser", 1);
        setupUserPermissions(Arrays.asList("song:list", "song:add"));

        Method method = TestService.class.getMethod("methodWithAllPermissions");
        when(methodSignature.getMethod()).thenReturn(method);

        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    @Test
    @DisplayName("有权限的管理员访问 - 应允许通过")
    void checkPermission_AdminHasPermission_ShouldPass() throws Exception {
        setupAuthentication("adminUser", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        setupUser("adminUser", 100);
        setupUserPermissions(Arrays.asList("song:list", "user:manage"));

        Method method = TestService.class.getMethod("methodWithSongListPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    @Test
    @DisplayName("无权限的用户访问 - 应抛出权限不足异常")
    void checkPermission_UserNoPermission_ShouldThrowException() throws Exception {
        setupAuthentication("testUser", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        setupUser("testUser", 1);
        setupUserPermissions(Arrays.asList("song:list"));

        Method method = TestService.class.getMethod("methodWithSongAddPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> permissionAspect.checkPermission(joinPoint));
        assertEquals("权限不足", exception.getMessage());
    }

    @Test
    @DisplayName("无权限的用户访问 - 多个权限都不匹配")
    void checkPermission_UserHasNoneOfRequiredPermissions_ShouldThrowException() throws Exception {
        setupAuthentication("testUser", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        setupUser("testUser", 1);
        setupUserPermissions(Arrays.asList("user:manage"));

        Method method = TestService.class.getMethod("methodWithAnyPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> permissionAspect.checkPermission(joinPoint));
        assertEquals("权限不足", exception.getMessage());
    }

    @Test
    @DisplayName("无权限的用户访问 - requireAll 为 true 时缺少部分权限")
    void checkPermission_UserMissingSomeRequiredPermissions_ShouldThrowException() throws Exception {
        setupAuthentication("testUser", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        setupUser("testUser", 1);
        setupUserPermissions(Arrays.asList("song:list"));

        Method method = TestService.class.getMethod("methodWithAllPermissions");
        when(methodSignature.getMethod()).thenReturn(method);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> permissionAspect.checkPermission(joinPoint));
        assertEquals("权限不足", exception.getMessage());
    }

    @Test
    @DisplayName("无权限的用户访问 - 用户没有任何角色")
    void checkPermission_UserNoRole_ShouldThrowException() throws Exception {
        setupAuthentication("testUser", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        setupUser("testUser", 1);
        when(userRoleMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        Method method = TestService.class.getMethod("methodWithSongListPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> permissionAspect.checkPermission(joinPoint));
        assertEquals("权限不足", exception.getMessage());
    }

    @Test
    @DisplayName("未登录用户访问 - 应抛出未登录异常")
    void checkPermission_UserNotLoggedIn_ShouldThrowException() throws Exception {
        SecurityContextHolder.clearContext();

        Method method = TestService.class.getMethod("methodWithSongListPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> permissionAspect.checkPermission(joinPoint));
        assertEquals("用户未登录", exception.getMessage());
    }

    @Test
    @DisplayName("用户不存在时应抛出用户未找到异常")
    void checkPermission_UserNotFoundInDatabase_ShouldThrowException() throws Exception {
        setupAuthentication("nonExistingUser", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(consumerMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        Method method = TestService.class.getMethod("methodWithSongListPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> permissionAspect.checkPermission(joinPoint));
        assertEquals("用户未找到", exception.getMessage());
    }

    @Test
    @DisplayName("方法没有@RequirePermission 注解 - 应直接通过")
    void checkPermission_NoAnnotation_ShouldPass() throws Exception {
        setupAuthentication("testUser", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        setupUser("testUser", 1);

        Method method = TestService.class.getMethod("methodWithoutAnnotation");
        when(methodSignature.getMethod()).thenReturn(method);

        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    @Test
    @DisplayName("注解权限代码为空 - 应直接通过")
    void checkPermission_EmptyPermissionCodes_ShouldPass() throws Exception {
        Method method = TestService.class.getMethod("methodWithEmptyPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    @Test
    @DisplayName("权限代码使用 value 属性 - 应正确解析")
    void checkPermission_UseValueAttribute_ShouldParseCorrectly() throws Exception {
        setupAuthentication("testUser", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        setupUser("testUser", 1);
        setupUserPermissions(Arrays.asList("song:list"));

        Method method = TestService.class.getMethod("methodWithValuePermission");
        when(methodSignature.getMethod()).thenReturn(method);

        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    @Test
    @DisplayName("用户有多个角色 - 应合并所有权限")
    void checkPermission_UserMultipleRoles_ShouldMergePermissions() throws Exception {
        setupAuthentication("testUser", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        setupUser("testUser", 1);

        UserRole role1 = new UserRole();
        role1.setId(1);
        role1.setUserId(1);
        role1.setRoleId(1);
        role1.setUserType("consumer");

        UserRole role2 = new UserRole();
        role2.setId(2);
        role2.setUserId(1);
        role2.setRoleId(2);
        role2.setUserType("consumer");

        when(userRoleMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(role1, role2));

        RolePermission rp1 = new RolePermission();
        rp1.setId(1);
        rp1.setRoleId(1);
        rp1.setPermissionId(1);

        RolePermission rp2 = new RolePermission();
        rp2.setId(2);
        rp2.setRoleId(2);
        rp2.setPermissionId(2);

        when(rolePermissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(rp1, rp2));

        Permission perm1 = new Permission();
        perm1.setId(1);
        perm1.setCode("song:list");

        Permission perm2 = new Permission();
        perm2.setId(2);
        perm2.setCode("song:add");

        when(permissionMapper.selectBatchIds(Arrays.asList(1, 2)))
                .thenReturn(Arrays.asList(perm1, perm2));

        Method method = TestService.class.getMethod("methodWithAnyPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    private void setupAuthentication(String username, List<SimpleGrantedAuthority> authorities) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void setupUser(String username, Integer userId) {
        Consumer consumer = new Consumer();
        consumer.setId(userId);
        consumer.setUsername(username);
        when(consumerMapper.selectOne(any(QueryWrapper.class))).thenReturn(consumer);
    }

    private void setupUserPermissions(List<String> permissionCodes) {
        UserRole userRole = new UserRole();
        userRole.setId(1);
        userRole.setUserId(1);
        userRole.setRoleId(1);
        userRole.setUserType("consumer");

        when(userRoleMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(userRole));

        List<RolePermission> rolePermissions = new ArrayList<>();
        List<Integer> permissionIds = new ArrayList<>();

        for (int i = 0; i < permissionCodes.size(); i++) {
            RolePermission rp = new RolePermission();
            rp.setId(i + 1);
            rp.setRoleId(1);
            rp.setPermissionId(i + 1);
            rolePermissions.add(rp);
            permissionIds.add(i + 1);
        }

        when(rolePermissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(rolePermissions);

        List<Permission> permissions = new ArrayList<>();
        for (int i = 0; i < permissionCodes.size(); i++) {
            Permission perm = new Permission();
            perm.setId(i + 1);
            perm.setCode(permissionCodes.get(i));
            permissions.add(perm);
        }

        when(permissionMapper.selectBatchIds(permissionIds)).thenReturn(permissions);
    }

    // ==================== 测试服务类 ====================

    /**
     * 用于测试的模拟服务类
     */
    static class TestService {

        @RequirePermission("song:list")
        public void methodWithSongListPermission() {
        }

        @RequirePermission("song:add")
        public void methodWithSongAddPermission() {
        }

        @RequirePermission(codes = {"song:list", "song:add"})
        public void methodWithAnyPermission() {
        }

        @RequirePermission(codes = {"song:list", "song:add"}, requireAll = true)
        public void methodWithAllPermissions() {
        }

        @RequirePermission(value = "song:list")
        public void methodWithValuePermission() {
        }

        @RequirePermission
        public void methodWithEmptyPermission() {
        }

        public void methodWithoutAnnotation() {
        }
    }
}
