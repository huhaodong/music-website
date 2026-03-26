package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.common.R;
import com.example.yin.mapper.RoleMapper;
import com.example.yin.model.domain.Role;
import com.example.yin.model.request.RoleRequest;
import com.example.yin.service.impl.RoleServiceImpl;
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
 * RoleService 单元测试
 */
@DisplayName("RoleService 测试")
class RoleServiceTest {

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(roleService, "baseMapper", roleMapper);
    }

    // ==================== addRole 方法测试 ====================

    @Test
    @DisplayName("添加角色 - 成功")
    void addRole_Success() {
        RoleRequest request = new RoleRequest();
        request.setName("测试角色");
        request.setCode("TEST_ROLE");
        request.setDescription("测试角色描述");

        when(roleMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenReturn(1);

        R result = roleService.addRole(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("添加角色成功", result.getMessage());
        assertNotNull(result.getData());
        verify(roleMapper, times(1)).insert(any(Role.class));
    }

    @Test
    @DisplayName("添加角色 - 角色代码已存在")
    void addRole_CodeExists() {
        RoleRequest request = new RoleRequest();
        request.setName("测试角色");
        request.setCode("EXISTING_ROLE");

        when(roleMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        R result = roleService.addRole(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("角色代码已存在", result.getMessage());
        verify(roleMapper, never()).insert(any(Role.class));
    }

    @Test
    @DisplayName("添加角色 - 插入失败")
    void addRole_InsertFailed() {
        RoleRequest request = new RoleRequest();
        request.setName("测试角色");
        request.setCode("NEW_ROLE");

        when(roleMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenReturn(0);

        R result = roleService.addRole(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("添加角色失败", result.getMessage());
    }

    // ==================== updateRole 方法测试 ====================

    @Test
    @DisplayName("更新角色 - 成功")
    void updateRole_Success() {
        RoleRequest request = new RoleRequest();
        request.setId(1);
        request.setName("更新后的角色名称");
        request.setCode("UPDATED_ROLE");
        request.setDescription("更新后的描述");

        Role existingRole = new Role();
        existingRole.setId(1);
        existingRole.setName("原角色名称");
        existingRole.setCode("OLD_ROLE");

        when(roleMapper.selectById(1)).thenReturn(existingRole);
        when(roleMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        R result = roleService.updateRole(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("更新角色成功", result.getMessage());
        assertNotNull(result.getData());
        verify(roleMapper, times(1)).updateById(any(Role.class));
    }

    @Test
    @DisplayName("更新角色 - ID 为空")
    void updateRole_IdIsNull() {
        RoleRequest request = new RoleRequest();
        request.setId(null);
        request.setName("测试角色");

        R result = roleService.updateRole(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("角色ID不能为空", result.getMessage());
        assertEquals("error", result.getType());
        verify(roleMapper, never()).selectById(any());
        verify(roleMapper, never()).updateById(any(Role.class));
    }

    @Test
    @DisplayName("更新角色 - 角色不存在")
    void updateRole_RoleNotFound() {
        RoleRequest request = new RoleRequest();
        request.setId(999);
        request.setName("不存在的角色");

        when(roleMapper.selectById(999)).thenReturn(null);

        R result = roleService.updateRole(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("角色不存在", result.getMessage());
        verify(roleMapper, never()).updateById(any(Role.class));
    }

    @Test
    @DisplayName("更新角色 - 角色代码重复")
    void updateRole_CodeDuplicate() {
        RoleRequest request = new RoleRequest();
        request.setId(1);
        request.setCode("EXISTING_CODE");

        Role existingRole = new Role();
        existingRole.setId(1);
        existingRole.setCode("OLD_CODE");

        when(roleMapper.selectById(1)).thenReturn(existingRole);
        when(roleMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        R result = roleService.updateRole(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("角色代码已存在", result.getMessage());
        verify(roleMapper, never()).updateById(any(Role.class));
    }

    @Test
    @DisplayName("更新角色 - 代码未变更不检查重复")
    void updateRole_CodeNotChanged() {
        RoleRequest request = new RoleRequest();
        request.setId(1);
        request.setName("新名称");
        request.setCode("SAME_CODE");

        Role existingRole = new Role();
        existingRole.setId(1);
        existingRole.setCode("SAME_CODE");

        when(roleMapper.selectById(1)).thenReturn(existingRole);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        R result = roleService.updateRole(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("更新角色成功", result.getMessage());
        verify(roleMapper, never()).selectCount(any(QueryWrapper.class));
        verify(roleMapper, times(1)).updateById(any(Role.class));
    }

    // ==================== deleteRole 方法测试 ====================

    @Test
    @DisplayName("删除角色 - 成功")
    void deleteRole_Success() {
        Integer roleId = 1;

        Role role = new Role();
        role.setId(roleId);
        role.setCode("NORMAL_ROLE");

        when(roleMapper.selectById(roleId)).thenReturn(role);
        when(roleMapper.deleteById(roleId)).thenReturn(1);

        R result = roleService.deleteRole(roleId);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("删除角色成功", result.getMessage());
        verify(roleMapper, times(1)).deleteById(roleId);
    }

    @Test
    @DisplayName("删除角色 - ID 为空")
    void deleteRole_IdIsNull() {
        R result = roleService.deleteRole(null);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("角色ID不能为空", result.getMessage());
        assertEquals("error", result.getType());
        verify(roleMapper, never()).selectById(any());
        verify(roleMapper, never()).deleteById((Integer) any());
    }

    @Test
    @DisplayName("删除角色 - 角色不存在")
    void deleteRole_RoleNotFound() {
        Integer roleId = 999;

        when(roleMapper.selectById(roleId)).thenReturn(null);

        R result = roleService.deleteRole(roleId);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("角色不存在", result.getMessage());
        verify(roleMapper, never()).deleteById((Integer) any());
    }

    @Test
    @DisplayName("删除角色 - 不能删除超级管理员")
    void deleteRole_CannotDeleteSuperAdmin() {
        Integer roleId = 1;

        Role role = new Role();
        role.setId(roleId);
        role.setCode("SUPER_ADMIN");

        when(roleMapper.selectById(roleId)).thenReturn(role);

        R result = roleService.deleteRole(roleId);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("不能删除超级管理员角色", result.getMessage());
        verify(roleMapper, never()).deleteById((Integer) any());
    }

    @Test
    @DisplayName("删除角色 - 删除失败")
    void deleteRole_DeleteFailed() {
        Integer roleId = 1;

        Role role = new Role();
        role.setId(roleId);
        role.setCode("NORMAL_ROLE");

        when(roleMapper.selectById(roleId)).thenReturn(role);
        when(roleMapper.deleteById(roleId)).thenReturn(0);

        R result = roleService.deleteRole(roleId);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("删除角色失败", result.getMessage());
    }

    // ==================== getRoleById 方法测试 ====================

    @Test
    @DisplayName("根据 ID 查询角色 - 成功")
    void getRoleById_Success() {
        Integer roleId = 1;

        Role role = new Role();
        role.setId(roleId);
        role.setName("测试角色");
        role.setCode("TEST_ROLE");
        role.setDescription("测试描述");
        role.setStatus(1);

        when(roleMapper.selectById(roleId)).thenReturn(role);

        R result = roleService.getRoleById(roleId);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());
        assertNotNull(result.getData());
        Role resultRole = (Role) result.getData();
        assertEquals(roleId, resultRole.getId());
        assertEquals("测试角色", resultRole.getName());
    }

    @Test
    @DisplayName("根据 ID 查询角色 - ID 为空")
    void getRoleById_IdIsNull() {
        R result = roleService.getRoleById(null);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("角色ID不能为空", result.getMessage());
        assertEquals("error", result.getType());
        verify(roleMapper, never()).selectById(any());
    }

    // ==================== 其他测试方法 ====================

    @Test
    @DisplayName("根据 ID 查询角色 - 角色不存在")
    void getRoleById_RoleNotFound() {
        Integer roleId = 999;

        when(roleMapper.selectById(roleId)).thenReturn(null);

        R result = roleService.getRoleById(roleId);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("角色不存在", result.getMessage());
    }

    // ==================== getAllRoles 方法测试 ====================

    @Test
    @DisplayName("查询所有角色 - 成功")
    void getAllRoles_Success() {
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("超级管理员");
        role1.setCode("SUPER_ADMIN");

        Role role2 = new Role();
        role2.setId(2);
        role2.setName("普通用户");
        role2.setCode("NORMAL_USER");

        Role role3 = new Role();
        role3.setId(3);
        role3.setName("访客");
        role3.setCode("GUEST");

        when(roleMapper.selectList(null)).thenReturn(Arrays.asList(role1, role2, role3));

        R result = roleService.getAllRoles();

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());
        assertNotNull(result.getData());
        List<Role> roles = (List<Role>) result.getData();
        assertEquals(3, roles.size());
    }

    @Test
    @DisplayName("查询所有角色 - 空数据")
    void getAllRoles_Empty() {
        when(roleMapper.selectList(null)).thenReturn(Collections.emptyList());

        R result = roleService.getAllRoles();

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());
        assertNotNull(result.getData());
        List<Role> roles = (List<Role>) result.getData();
        assertEquals(0, roles.size());
    }

    // ==================== getRolesByStatus 方法测试 ====================

    @Test
    @DisplayName("按状态查询角色 - 查询启用状态")
    void getRolesByStatus_Active() {
        Integer status = 1;

        Role role1 = new Role();
        role1.setId(1);
        role1.setName("超级管理员");
        role1.setCode("SUPER_ADMIN");
        role1.setStatus(1);

        Role role2 = new Role();
        role2.setId(2);
        role2.setName("普通用户");
        role2.setCode("NORMAL_USER");
        role2.setStatus(1);

        when(roleMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(role1, role2));

        R result = roleService.getRolesByStatus(status);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());
        assertNotNull(result.getData());
        List<Role> roles = (List<Role>) result.getData();
        assertEquals(2, roles.size());
    }

    @Test
    @DisplayName("按状态查询角色 - 查询禁用状态")
    void getRolesByStatus_Disabled() {
        Integer status = 0;

        Role role = new Role();
        role.setId(3);
        role.setName("禁用角色");
        role.setCode("DISABLED_ROLE");
        role.setStatus(0);

        when(roleMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.singletonList(role));

        R result = roleService.getRolesByStatus(status);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());
        assertNotNull(result.getData());
        List<Role> roles = (List<Role>) result.getData();
        assertEquals(1, roles.size());
    }

    @Test
    @DisplayName("按状态查询角色 - 查询所有状态")
    void getRolesByStatus_AllStatus() {
        when(roleMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        R result = roleService.getRolesByStatus(null);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());
        verify(roleMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    // ==================== 边界条件测试 ====================

    @Test
    @DisplayName("添加角色 - 角色代码为空")
    void addRole_NullCode() {
        RoleRequest request = new RoleRequest();
        request.setName("测试角色");
        request.setCode(null);

        when(roleMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenReturn(1);

        R result = roleService.addRole(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("添加角色成功", result.getMessage());
    }

    @Test
    @DisplayName("添加角色 - 角色名称为空")
    void addRole_NullName() {
        RoleRequest request = new RoleRequest();
        request.setName(null);
        request.setCode("TEST_ROLE");

        when(roleMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenReturn(1);

        R result = roleService.addRole(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("添加角色成功", result.getMessage());
    }

    @Test
    @DisplayName("更新角色 - 只更新名称")
    void updateRole_OnlyName() {
        RoleRequest request = new RoleRequest();
        request.setId(1);
        request.setName("新名称");

        Role existingRole = new Role();
        existingRole.setId(1);
        existingRole.setName("旧名称");
        existingRole.setCode("TEST_ROLE");

        when(roleMapper.selectById(1)).thenReturn(existingRole);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        R result = roleService.updateRole(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("更新角色成功", result.getMessage());
    }

    @Test
    @DisplayName("更新角色 - 只更新描述")
    void updateRole_OnlyDescription() {
        RoleRequest request = new RoleRequest();
        request.setId(1);
        request.setDescription("新的描述信息");

        Role existingRole = new Role();
        existingRole.setId(1);
        existingRole.setName("角色名称");
        existingRole.setCode("TEST_ROLE");

        when(roleMapper.selectById(1)).thenReturn(existingRole);
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        R result = roleService.updateRole(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("更新角色成功", result.getMessage());
    }

    // ==================== 返回类型验证测试 ====================

    @Test
    @DisplayName("添加角色成功返回类型验证")
    void addRole_SuccessTypeVerification() {
        RoleRequest request = new RoleRequest();
        request.setCode("TEST_ROLE");

        when(roleMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(roleMapper.insert(any(Role.class))).thenReturn(1);

        R result = roleService.addRole(request);

        assertEquals(200, result.getCode());
        assertTrue(result.getSuccess());
        assertEquals("success", result.getType());
    }

    @Test
    @DisplayName("添加角色失败返回类型验证")
    void addRole_FailedTypeVerification() {
        RoleRequest request = new RoleRequest();
        request.setCode("EXISTING_ROLE");

        when(roleMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        R result = roleService.addRole(request);

        assertEquals(200, result.getCode());
        assertFalse(result.getSuccess());
        assertEquals("warning", result.getType());
    }

    @Test
    @DisplayName("删除超级管理员返回类型验证")
    void deleteRole_SuperAdminTypeVerification() {
        Integer roleId = 1;
        Role role = new Role();
        role.setId(roleId);
        role.setCode("SUPER_ADMIN");

        when(roleMapper.selectById(roleId)).thenReturn(role);

        R result = roleService.deleteRole(roleId);

        assertEquals(200, result.getCode());
        assertFalse(result.getSuccess());
        assertEquals("error", result.getType());
    }
}
