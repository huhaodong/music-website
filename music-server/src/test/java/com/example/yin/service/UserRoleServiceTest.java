package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.common.R;
import com.example.yin.mapper.UserRoleMapper;
import com.example.yin.model.domain.UserRole;
import com.example.yin.model.request.UserRoleRequest;
import com.example.yin.service.impl.UserRoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserRoleService 单元测试
 */
@DisplayName("UserRoleService 测试")
class UserRoleServiceTest {

    @Mock
    private UserRoleMapper userRoleMapper;

    @InjectMocks
    private UserRoleServiceImpl userRoleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(userRoleService, "baseMapper", userRoleMapper);
    }

    // ==================== 分配角色到用户测试 ====================

    @Test
    @DisplayName("分配角色到用户 - 成功")
    void assignRolesToUser_Success() {
        UserRoleRequest request = new UserRoleRequest();
        request.setUserId(1);
        request.setUserType("ADMIN");
        request.setRoleIds(Arrays.asList(1, 2, 3));

        when(userRoleMapper.delete(any(QueryWrapper.class))).thenReturn(2);
        when(userRoleMapper.insert(any(UserRole.class))).thenReturn(1);

        R result = userRoleService.assignRolesToUser(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("分配角色成功", result.getMessage());
        verify(userRoleMapper, times(1)).delete(any(QueryWrapper.class));
        verify(userRoleMapper, times(3)).insert(any(UserRole.class));
    }

    @Test
    @DisplayName("分配角色到用户 - 用户ID为空")
    void assignRolesToUser_NullUserId() {
        UserRoleRequest request = new UserRoleRequest();
        request.setUserId(null);
        request.setUserType("ADMIN");
        request.setRoleIds(Arrays.asList(1, 2));

        R result = userRoleService.assignRolesToUser(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("用户ID和用户类型不能为空", result.getMessage());
        verify(userRoleMapper, never()).delete(any(QueryWrapper.class));
        verify(userRoleMapper, never()).insert(any(UserRole.class));
    }

    @Test
    @DisplayName("分配角色到用户 - 用户类型为空")
    void assignRolesToUser_NullUserType() {
        UserRoleRequest request = new UserRoleRequest();
        request.setUserId(1);
        request.setUserType(null);
        request.setRoleIds(Arrays.asList(1, 2));

        R result = userRoleService.assignRolesToUser(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("用户ID和用户类型不能为空", result.getMessage());
    }

    @Test
    @DisplayName("分配角色到用户 - 角色列表为空")
    void assignRolesToUser_EmptyRoleIds() {
        UserRoleRequest request = new UserRoleRequest();
        request.setUserId(1);
        request.setUserType("ADMIN");
        request.setRoleIds(Collections.emptyList());

        when(userRoleMapper.delete(any(QueryWrapper.class))).thenReturn(0);

        R result = userRoleService.assignRolesToUser(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("分配角色成功", result.getMessage());
        verify(userRoleMapper, times(1)).delete(any(QueryWrapper.class));
        verify(userRoleMapper, never()).insert(any(UserRole.class));
    }

    @Test
    @DisplayName("分配角色到用户 - 角色列表为null")
    void assignRolesToUser_NullRoleIds() {
        UserRoleRequest request = new UserRoleRequest();
        request.setUserId(1);
        request.setUserType("ADMIN");
        request.setRoleIds(null);

        when(userRoleMapper.delete(any(QueryWrapper.class))).thenReturn(0);

        R result = userRoleService.assignRolesToUser(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("分配角色成功", result.getMessage());
        verify(userRoleMapper, times(1)).delete(any(QueryWrapper.class));
        verify(userRoleMapper, never()).insert(any(UserRole.class));
    }

    // ==================== 取消用户角色测试 ====================

    @Test
    @DisplayName("取消用户角色 - 成功")
    void removeRolesFromUser_Success() {
        Integer userId = 1;
        String userType = "ADMIN";

        when(userRoleMapper.delete(any(QueryWrapper.class))).thenReturn(3);

        R result = userRoleService.removeRolesFromUser(userId, userType);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("移除角色成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(3, result.getData());
        verify(userRoleMapper, times(1)).delete(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("取消用户角色 - 用户ID为空")
    void removeRolesFromUser_NullUserId() {
        R result = userRoleService.removeRolesFromUser(null, "ADMIN");

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("用户ID和用户类型不能为空", result.getMessage());
        verify(userRoleMapper, never()).delete(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("取消用户角色 - 用户类型为空")
    void removeRolesFromUser_NullUserType() {
        R result = userRoleService.removeRolesFromUser(1, null);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("用户ID和用户类型不能为空", result.getMessage());
    }

    @Test
    @DisplayName("取消用户角色 - 用户无角色")
    void removeRolesFromUser_NoRoles() {
        Integer userId = 999;
        String userType = "ADMIN";

        when(userRoleMapper.delete(any(QueryWrapper.class))).thenReturn(0);

        R result = userRoleService.removeRolesFromUser(userId, userType);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("移除角色成功", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(0, result.getData());
    }

    // ==================== 查询用户角色测试 ====================

    @Test
    @DisplayName("查询用户角色 - 成功")
    void getUserRoles_Success() {
        Integer userId = 1;
        String userType = "ADMIN";

        UserRole userRole1 = new UserRole();
        userRole1.setUserId(1);
        userRole1.setUserType("ADMIN");
        userRole1.setRoleId(1);

        UserRole userRole2 = new UserRole();
        userRole2.setUserId(1);
        userRole2.setUserType("ADMIN");
        userRole2.setRoleId(2);

        List<UserRole> userRoles = Arrays.asList(userRole1, userRole2);
        when(userRoleMapper.selectList(any(QueryWrapper.class))).thenReturn(userRoles);

        R result = userRoleService.getUserRoles(userId, userType);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());
        assertNotNull(result.getData());
        List<Integer> roleIds = (List<Integer>) result.getData();
        assertEquals(2, roleIds.size());
        assertTrue(roleIds.contains(1));
        assertTrue(roleIds.contains(2));
    }

    @Test
    @DisplayName("查询用户角色 - 用户ID为空")
    void getUserRoles_NullUserId() {
        R result = userRoleService.getUserRoles(null, "ADMIN");

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("用户ID和用户类型不能为空", result.getMessage());
        verify(userRoleMapper, never()).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("查询用户角色 - 用户类型为空")
    void getUserRoles_NullUserType() {
        R result = userRoleService.getUserRoles(1, null);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("用户ID和用户类型不能为空", result.getMessage());
    }

    @Test
    @DisplayName("查询用户角色 - 用户无角色")
    void getUserRoles_NoRoles() {
        Integer userId = 999;
        String userType = "ADMIN";

        when(userRoleMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        R result = userRoleService.getUserRoles(userId, userType);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());
        assertNotNull(result.getData());
        List<Integer> roleIds = (List<Integer>) result.getData();
        assertTrue(roleIds.isEmpty());
    }

    // ==================== 查询角色用户列表测试 ====================

    @Test
    @DisplayName("查询角色用户列表 - 成功")
    void getUsersByRoleId_Success() {
        Integer roleId = 1;

        UserRole userRole1 = new UserRole();
        userRole1.setUserId(1);
        userRole1.setUserType("ADMIN");
        userRole1.setRoleId(1);

        UserRole userRole2 = new UserRole();
        userRole2.setUserId(2);
        userRole2.setUserType("USER");
        userRole2.setRoleId(1);

        List<UserRole> userRoles = Arrays.asList(userRole1, userRole2);
        when(userRoleMapper.selectList(any(QueryWrapper.class))).thenReturn(userRoles);

        R result = userRoleService.getUsersByRoleId(roleId);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());
        assertNotNull(result.getData());
        List<UserRole> resultData = (List<UserRole>) result.getData();
        assertEquals(2, resultData.size());
    }

    @Test
    @DisplayName("查询角色用户列表 - 角色ID为空")
    void getUsersByRoleId_NullRoleId() {
        R result = userRoleService.getUsersByRoleId(null);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("角色ID不能为空", result.getMessage());
        verify(userRoleMapper, never()).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("查询角色用户列表 - 角色无用户")
    void getUsersByRoleId_NoUsers() {
        Integer roleId = 999;

        when(userRoleMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        R result = userRoleService.getUsersByRoleId(roleId);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());
        assertNotNull(result.getData());
        List<UserRole> resultData = (List<UserRole>) result.getData();
        assertTrue(resultData.isEmpty());
    }
}
