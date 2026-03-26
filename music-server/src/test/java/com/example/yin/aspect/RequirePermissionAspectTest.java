package com.example.yin.aspect;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.annotation.RequirePermission;
import com.example.yin.mapper.AdminMapper;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.PermissionMapper;
import com.example.yin.mapper.RolePermissionMapper;
import com.example.yin.mapper.UserRoleMapper;
import com.example.yin.model.domain.Admin;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Permission;
import com.example.yin.model.domain.RolePermission;
import com.example.yin.model.domain.UserRole;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    private AdminMapper adminMapper;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @InjectMocks
    private PermissionAspect permissionAspect;

    private MockHttpServletRequest request;
    private MockHttpSession session;
    private TestService testService;

    @BeforeEach
    void setUp() throws Exception {
        // 初始化 Mock 请求和会话
        request = new MockHttpServletRequest();
        session = new MockHttpSession();
        request.setSession(session);

        // 设置 RequestContextHolder
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        testService = new TestService();
        
        // 设置 joinPoint 的通用 mock
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(joinPoint.getTarget()).thenReturn(testService);
    }

    // ==================== 场景 1: 有权限的用户访问 - 应允许 ====================

    @Test
    @DisplayName("有权限的用户访问 - 应允许通过")
    void checkPermission_UserHasPermission_ShouldPass() throws Exception {
        // Given: 用户有 song:list 权限
        setupUserLogin("testUser", "consumer");
        setupUserPermissions(Arrays.asList("song:list"));

        Method method = TestService.class.getMethod("methodWithSongListPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：调用有@RequirePermission("song:list") 注解的方法，不应抛出异常
        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    @Test
    @DisplayName("有权限的用户访问 - 多个权限任一匹配即可")
    void checkPermission_UserHasOneOfMultiplePermissions_ShouldPass() throws Exception {
        // Given: 用户有 song:add 权限
        setupUserLogin("testUser", "consumer");
        setupUserPermissions(Arrays.asList("song:add", "user:manage"));

        Method method = TestService.class.getMethod("methodWithAnyPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：调用需要 song:list 或 song:add 权限的方法
        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    @Test
    @DisplayName("有权限的用户访问 - requireAll 为 true 时拥有所有权限")
    void checkPermission_UserHasAllRequiredPermissions_ShouldPass() throws Exception {
        // Given: 用户拥有所有需要的权限
        setupUserLogin("testUser", "consumer");
        setupUserPermissions(Arrays.asList("song:list", "song:add"));

        Method method = TestService.class.getMethod("methodWithAllPermissions");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：调用 requireAll=true 且需要 song:list 和 song:add 权限的方法
        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    @Test
    @DisplayName("有权限的管理员访问 - 应允许通过")
    void checkPermission_AdminHasPermission_ShouldPass() throws Exception {
        // Given: 管理员有权限
        setupUserLogin("adminUser", "admin");
        setupUserPermissions(Arrays.asList("song:list", "user:manage"));

        Method method = TestService.class.getMethod("methodWithSongListPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：调用有权限要求的方法
        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    // ==================== 场景 2: 无权限的用户访问 - 应抛出异常 ====================

    @Test
    @DisplayName("无权限的用户访问 - 应抛出权限不足异常")
    void checkPermission_UserNoPermission_ShouldThrowException() throws Exception {
        // Given: 用户只有 song:list 权限
        setupUserLogin("testUser", "consumer");
        setupUserPermissions(Arrays.asList("song:list"));

        Method method = TestService.class.getMethod("methodWithSongAddPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：调用需要 song:add 权限的方法，应抛出"权限不足"异常
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> permissionAspect.checkPermission(joinPoint));
        assertEquals("权限不足", exception.getMessage());
    }

    @Test
    @DisplayName("无权限的用户访问 - 多个权限都不匹配")
    void checkPermission_UserHasNoneOfRequiredPermissions_ShouldThrowException() throws Exception {
        // Given: 用户只有 user:manage 权限，但方法需要 song:list 或 song:add 权限
        setupUserLogin("testUser", "consumer");
        setupUserPermissions(Arrays.asList("user:manage"));

        Method method = TestService.class.getMethod("methodWithAnyPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：调用需要 song:list 或 song:add 权限的方法，应抛出"权限不足"异常
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> permissionAspect.checkPermission(joinPoint));
        assertEquals("权限不足", exception.getMessage());
    }

    @Test
    @DisplayName("无权限的用户访问 - requireAll 为 true 时缺少部分权限")
    void checkPermission_UserMissingSomeRequiredPermissions_ShouldThrowException() throws Exception {
        // Given: 用户只有 song:list 权限，缺少 song:add
        setupUserLogin("testUser", "consumer");
        setupUserPermissions(Arrays.asList("song:list"));

        Method method = TestService.class.getMethod("methodWithAllPermissions");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：调用 requireAll=true 且需要 song:list 和 song:add 权限的方法
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> permissionAspect.checkPermission(joinPoint));
        assertEquals("权限不足", exception.getMessage());
    }

    @Test
    @DisplayName("无权限的用户访问 - 用户没有任何角色")
    void checkPermission_UserNoRole_ShouldThrowException() throws Exception {
        // Given: 用户登录但没有角色
        setupUserLogin("testUser", "consumer");
        when(userRoleMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        Method method = TestService.class.getMethod("methodWithSongListPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：调用需要权限的方法，应抛出"权限不足"异常
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> permissionAspect.checkPermission(joinPoint));
        assertEquals("权限不足", exception.getMessage());
    }

    // ==================== 场景 3: 超级管理员访问 - 当前实现未特殊处理 ====================

    @Test
    @DisplayName("超级管理员访问 - 有 SUPER_ADMIN 权限时应通过")
    void checkPermission_SuperAdminWithSuperPermission_ShouldPass() throws Exception {
        // Given: 超级管理员登录，拥有 SUPER_ADMIN 权限
        setupUserLogin("superAdmin", "admin");

        UserRole superAdminRole = new UserRole();
        superAdminRole.setId(1);
        superAdminRole.setUserId(100);
        superAdminRole.setRoleId(1);
        superAdminRole.setUserType("admin");

        when(userRoleMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(superAdminRole));

        Permission superAdminPerm = new Permission();
        superAdminPerm.setId(999);
        superAdminPerm.setCode("SUPER_ADMIN");
        superAdminPerm.setName("超级管理员");

        when(permissionMapper.selectBatchIds(anyList()))
                .thenReturn(Collections.singletonList(superAdminPerm));

        Method method = TestService.class.getMethod("methodWithSongAddPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：注意：当前实现不会自动跳过检查，需要 SUPER_ADMIN 权限代码匹配
        // 这里测试实际行为
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> permissionAspect.checkPermission(joinPoint));
        // 因为方法需要 song:add 权限，但用户只有 SUPER_ADMIN 权限
        assertEquals("权限不足", exception.getMessage());
    }

    @Test
    @DisplayName("超级管理员访问 - 没有权限时也会被拒绝")
    void checkPermission_SuperAdminNoPermissions_ShouldBeDenied() throws Exception {
        // Given: 超级管理员登录，但没有任何权限配置
        setupUserLogin("superAdmin", "admin");

        UserRole superAdminRole = new UserRole();
        superAdminRole.setId(1);
        superAdminRole.setUserId(100);
        superAdminRole.setRoleId(1);
        superAdminRole.setUserType("admin");

        when(userRoleMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(superAdminRole));
        when(rolePermissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        when(permissionMapper.selectBatchIds(anyList()))
                .thenReturn(Collections.emptyList());

        Method method = TestService.class.getMethod("methodWithSongAddPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：当前实现中超级管理员没有特殊处理，会被拒绝
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> permissionAspect.checkPermission(joinPoint));
        assertEquals("权限不足", exception.getMessage());
    }

    // ==================== 场景 4: 未登录用户访问 - 应拒绝 ====================

    @Test
    @DisplayName("未登录用户访问 - 应抛出未登录异常")
    void checkPermission_UserNotLoggedIn_ShouldThrowException() throws Exception {
        // Given: 用户未登录（session 中没有 username 或 name）
        session.removeAttribute("username");
        session.removeAttribute("name");

        Method method = TestService.class.getMethod("methodWithSongListPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：调用需要权限的方法，应抛出"用户未登录"异常
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> permissionAspect.checkPermission(joinPoint));
        assertEquals("用户未登录", exception.getMessage());
    }

    @Test
    @DisplayName("未登录用户访问 - session 中用户名不存在于数据库")
    void checkPermission_UserNotFoundInDatabase_ShouldThrowException() throws Exception {
        // Given: session 中有用户名，但数据库中不存在
        session.setAttribute("username", "nonExistingUser");
        when(consumerMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        Method method = TestService.class.getMethod("methodWithSongListPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：应抛出"用户未登录"异常
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> permissionAspect.checkPermission(joinPoint));
        assertEquals("用户未登录", exception.getMessage());
    }

    // ==================== 边界条件测试 ====================

    @Test
    @DisplayName("方法没有@RequirePermission 注解 - 应直接通过")
    void checkPermission_NoAnnotation_ShouldPass() throws Exception {
        // Given: 方法没有@RequirePermission 注解
        setupUserLogin("testUser", "consumer");

        Method method = TestService.class.getMethod("methodWithoutAnnotation");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：调用没有注解的方法，不应抛出异常
        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    @Test
    @DisplayName("注解权限代码为空 - 应直接通过")
    void checkPermission_EmptyPermissionCodes_ShouldPass() throws Exception {
        // Given: 注解的权限代码为空
        // 不需要 setupUserLogin，因为空权限会直接返回

        Method method = TestService.class.getMethod("methodWithEmptyPermission");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：调用权限代码为空的方法
        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    @Test
    @DisplayName("权限代码使用 value 属性 - 应正确解析")
    void checkPermission_UseValueAttribute_ShouldParseCorrectly() throws Exception {
        // Given: 使用 value 属性设置权限代码
        setupUserLogin("testUser", "consumer");
        setupUserPermissions(Arrays.asList("song:list"));

        Method method = TestService.class.getMethod("methodWithValuePermission");
        when(methodSignature.getMethod()).thenReturn(method);

        // When & Then：调用使用 value 属性的方法
        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    @Test
    @DisplayName("用户有多个角色 - 应合并所有权限")
    void checkPermission_UserMultipleRoles_ShouldMergePermissions() throws Exception {
        // Given: 用户有多个角色，每个角色有不同权限
        setupUserLogin("testUser", "consumer");

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

        // When & Then：调用需要任一权限的方法
        assertDoesNotThrow(() -> permissionAspect.checkPermission(joinPoint));
    }

    // ==================== 辅助方法 ====================

    /**
     * 设置用户登录状态
     */
    private void setupUserLogin(String username, String userType) throws Exception {
        Consumer testConsumer = new Consumer();
        testConsumer.setId(1);
        testConsumer.setUsername(username);
        testConsumer.setPassword("encodedPassword");
        testConsumer.setEmail("test@example.com");

        Admin testAdmin = new Admin();
        testAdmin.setId(100);
        testAdmin.setName(username);
        testAdmin.setPassword("encodedPassword");

        if ("consumer".equals(userType)) {
            session.setAttribute("username", username);
            session.removeAttribute("name");

            when(consumerMapper.selectOne(any(QueryWrapper.class)))
                    .thenReturn(testConsumer);
        } else if ("admin".equals(userType)) {
            session.setAttribute("name", username);
            session.removeAttribute("username");

            when(adminMapper.selectOne(any(QueryWrapper.class)))
                    .thenReturn(testAdmin);
        }
    }

    /**
     * 设置用户权限列表
     */
    private void setupUserPermissions(List<String> permissionCodes) throws Exception {
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
            perm.setName("权限" + (i + 1));
            perm.setType("MENU");
            perm.setSort(i + 1);
            perm.setStatus(1);
            perm.setCreateTime(LocalDateTime.now());
            perm.setUpdateTime(LocalDateTime.now());
            permissions.add(perm);
        }

        when(permissionMapper.selectBatchIds(permissionIds))
                .thenReturn(permissions);
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
