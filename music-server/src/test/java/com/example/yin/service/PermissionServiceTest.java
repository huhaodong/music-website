package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.common.R;
import com.example.yin.mapper.PermissionMapper;
import com.example.yin.mapper.RolePermissionMapper;
import com.example.yin.model.domain.Permission;
import com.example.yin.model.domain.RolePermission;
import com.example.yin.model.request.PermissionRequest;
import com.example.yin.service.impl.PermissionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * PermissionService 单元测试
 * 覆盖权限管理的核心功能测试
 */
@SpringBootTest(classes = {PermissionServiceImpl.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@DisplayName("PermissionService 测试")
class PermissionServiceTest {

    @MockBean
    private PermissionMapper permissionMapper;

    @MockBean
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private PermissionService permissionService;

    private Permission testPermission;
    private PermissionRequest testPermissionRequest;
    private RolePermission testRolePermission;

    @BeforeEach
    void setUp() {
        // 初始化测试权限
        testPermission = new Permission();
        testPermission.setId(1);
        testPermission.setName("测试权限");
        testPermission.setCode("TEST_PERMISSION");
        testPermission.setType("MENU");
        testPermission.setUrl("/test");
        testPermission.setMethod("GET");
        testPermission.setParentId(null);
        testPermission.setSort(1);
        testPermission.setStatus(1);
        testPermission.setCreateTime(LocalDateTime.now());
        testPermission.setUpdateTime(LocalDateTime.now());

        // 初始化测试请求
        testPermissionRequest = new PermissionRequest();
        testPermissionRequest.setName("测试权限");
        testPermissionRequest.setCode("TEST_PERMISSION");
        testPermissionRequest.setType("MENU");
        testPermissionRequest.setUrl("/test");
        testPermissionRequest.setMethod("GET");

        // 初始化测试角色权限关联
        testRolePermission = new RolePermission();
        testRolePermission.setId(1);
        testRolePermission.setRoleId(1);
        testRolePermission.setPermissionId(1);
        testRolePermission.setCreateTime(LocalDateTime.now());
    }

    // ==================== addPermission 测试 ====================

    @Test
    @DisplayName("创建权限成功")
    void addPermission_Success() {
        // Given: 权限代码不存在，可以成功创建
        when(permissionMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(permissionMapper.insert(any(Permission.class))).thenReturn(1);

        // When
        PermissionRequest request = new PermissionRequest();
        request.setName("新权限");
        request.setCode("NEW_PERMISSION");
        request.setType("MENU");
        R result = permissionService.addPermission(request);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("添加权限成功", result.getMessage());
        assertNotNull(result.getData());

        verify(permissionMapper, times(1)).selectCount(any(QueryWrapper.class));
        verify(permissionMapper, times(1)).insert(any(Permission.class));
    }

    @Test
    @DisplayName("创建权限失败 - 权限代码已存在")
    void addPermission_DuplicateCode_Fail() {
        // Given: 权限代码已存在
        when(permissionMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // When
        PermissionRequest request = new PermissionRequest();
        request.setName("新权限");
        request.setCode("EXISTING_PERMISSION");
        R result = permissionService.addPermission(request);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("权限代码已存在", result.getMessage());

        verify(permissionMapper, times(1)).selectCount(any(QueryWrapper.class));
        verify(permissionMapper, never()).insert(any(Permission.class));
    }

    @Test
    @DisplayName("创建权限失败 - 权限代码为空")
    void addPermission_CodeIsNull_Fail() {
        // Given: 权限代码为空

        // When
        PermissionRequest request = new PermissionRequest();
        request.setName("新权限");
        R result = permissionService.addPermission(request);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("权限代码和名称不能为空", result.getMessage());
    }

    @Test
    @DisplayName("创建权限失败 - 权限名称为空")
    void addPermission_NameIsNull_Fail() {
        // Given: 权限名称为空

        // When
        PermissionRequest request = new PermissionRequest();
        request.setCode("NEW_PERMISSION");
        R result = permissionService.addPermission(request);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("权限代码和名称不能为空", result.getMessage());
    }

    @Test
    @DisplayName("创建权限失败 - 数据库插入失败")
    void addPermission_InsertFail() {
        // Given: 权限代码不存在，但插入失败
        when(permissionMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(permissionMapper.insert(any(Permission.class))).thenReturn(0);

        // When
        PermissionRequest request = new PermissionRequest();
        request.setName("新权限");
        request.setCode("NEW_PERMISSION");
        R result = permissionService.addPermission(request);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("添加权限失败", result.getMessage());
    }

    // ==================== updatePermission 测试 ====================

    @Test
    @DisplayName("更新权限成功")
    void updatePermission_Success() {
        // Given: 权限存在，可以更新
        when(permissionMapper.selectById(1)).thenReturn(testPermission);
        when(permissionMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(permissionMapper.updateById(any(Permission.class))).thenReturn(1);

        // When
        PermissionRequest request = new PermissionRequest();
        request.setId(1);
        request.setName("更新后的权限名");
        request.setCode("UPDATED_CODE");
        R result = permissionService.updatePermission(request);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("更新权限成功", result.getMessage());

        verify(permissionMapper, times(1)).selectById(1);
        verify(permissionMapper, times(1)).updateById(any(Permission.class));
    }

    @Test
    @DisplayName("更新权限失败 - 权限 ID 为空")
    void updatePermission_IdIsNull_Fail() {
        // Given: 权限 ID 为空

        // When
        PermissionRequest request = new PermissionRequest();
        request.setName("更新后的权限名");
        R result = permissionService.updatePermission(request);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("权限ID不能为空", result.getMessage());
    }

    @Test
    @DisplayName("更新权限失败 - 权限不存在")
    void updatePermission_PermissionNotExist_Fail() {
        // Given: 权限不存在
        when(permissionMapper.selectById(999)).thenReturn(null);

        // When
        PermissionRequest request = new PermissionRequest();
        request.setId(999);
        request.setName("更新后的权限名");
        R result = permissionService.updatePermission(request);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("权限不存在", result.getMessage());
    }

    @Test
    @DisplayName("更新权限失败 - 权限代码已存在")
    void updatePermission_DuplicateCode_Fail() {
        // Given: 权限存在，但新代码已被其他权限使用
        Permission existingPermission = new Permission();
        existingPermission.setId(2);
        existingPermission.setCode("EXISTING_CODE");

        when(permissionMapper.selectById(1)).thenReturn(testPermission);
        when(permissionMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // When
        PermissionRequest request = new PermissionRequest();
        request.setId(1);
        request.setCode("EXISTING_CODE");
        R result = permissionService.updatePermission(request);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("权限代码已存在", result.getMessage());
    }

    @Test
    @DisplayName("更新权限失败 - 数据库更新失败")
    void updatePermission_UpdateFail() {
        // Given: 权限存在，但更新失败
        when(permissionMapper.selectById(1)).thenReturn(testPermission);
        when(permissionMapper.updateById(any(Permission.class))).thenReturn(0);

        // When
        PermissionRequest request = new PermissionRequest();
        request.setId(1);
        request.setName("更新后的权限名");
        R result = permissionService.updatePermission(request);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("更新权限失败", result.getMessage());
    }

    @Test
    @DisplayName("更新权限时不改变 code 不检查重复")
    void updatePermission_KeepOriginalCode_NoDuplicateCheck() {
        // Given: 权限存在，更新时不改变 code
        when(permissionMapper.selectById(1)).thenReturn(testPermission);
        when(permissionMapper.updateById(any(Permission.class))).thenReturn(1);

        // When: 只更新 name，不更新 code
        PermissionRequest request = new PermissionRequest();
        request.setId(1);
        request.setName("新名称");
        R result = permissionService.updatePermission(request);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        verify(permissionMapper, never()).selectCount(any(QueryWrapper.class));
        verify(permissionMapper, times(1)).updateById(any(Permission.class));
    }

    // ==================== deletePermission 测试 ====================

    @Test
    @DisplayName("删除权限成功")
    void deletePermission_Success() {
        // Given: 权限存在且没有子权限，可以删除
        when(permissionMapper.selectById(1)).thenReturn(testPermission);
        when(permissionMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(rolePermissionMapper.delete(any(QueryWrapper.class))).thenReturn(1);
        when(permissionMapper.deleteById(1)).thenReturn(1);

        // When
        R result = permissionService.deletePermission(1);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("删除权限成功", result.getMessage());

        verify(permissionMapper, times(1)).selectById(1);
        verify(permissionMapper, times(1)).deleteById(1);
        verify(rolePermissionMapper, times(1)).delete(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("删除权限失败 - 权限 ID 为空")
    void deletePermission_IdIsNull_Fail() {
        // Given: 权限 ID 为空

        // When
        R result = permissionService.deletePermission(null);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("权限ID不能为空", result.getMessage());
    }

    @Test
    @DisplayName("删除权限失败 - 权限不存在")
    void deletePermission_PermissionNotExist_Fail() {
        // Given: 权限不存在
        when(permissionMapper.selectById(999)).thenReturn(null);

        // When
        R result = permissionService.deletePermission(999);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("权限不存在", result.getMessage());
    }

    @Test
    @DisplayName("删除权限失败 - 存在子权限")
    void deletePermission_HasChildren_Fail() {
        // Given: 权限存在但有子权限
        when(permissionMapper.selectById(1)).thenReturn(testPermission);
        when(permissionMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // When
        R result = permissionService.deletePermission(1);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("该权限存在子权限，不能删除", result.getMessage());

        verify(permissionMapper, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("删除权限失败 - 数据库删除失败")
    void deletePermission_DeleteFail() {
        // Given: 权限存在，但删除失败
        when(permissionMapper.selectById(1)).thenReturn(testPermission);
        when(permissionMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(rolePermissionMapper.delete(any(QueryWrapper.class))).thenReturn(1);
        when(permissionMapper.deleteById(1)).thenReturn(0);

        // When
        R result = permissionService.deletePermission(1);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("删除权限失败", result.getMessage());
    }

    // ==================== getPermissionById 测试 ====================

    @Test
    @DisplayName("查询单个权限成功")
    void getPermissionById_Success() {
        // Given: 权限存在
        when(permissionMapper.selectById(1)).thenReturn(testPermission);

        // When
        R result = permissionService.getPermissionById(1);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("查询成功", result.getMessage());

        Permission resultPermission = (Permission) result.getData();
        assertNotNull(resultPermission);
        assertEquals(1, resultPermission.getId());
        assertEquals("TEST_PERMISSION", resultPermission.getCode());

        verify(permissionMapper, times(1)).selectById(1);
    }

    @Test
    @DisplayName("查询单个权限失败 - ID 为空")
    void getPermissionById_IdIsNull_Fail() {
        // Given: ID 为空

        // When
        R result = permissionService.getPermissionById(null);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("权限ID不能为空", result.getMessage());
    }

    @Test
    @DisplayName("查询单个权限失败 - 权限不存在")
    void getPermissionById_NotExist_Fail() {
        // Given: 权限不存在
        when(permissionMapper.selectById(999)).thenReturn(null);

        // When
        R result = permissionService.getPermissionById(999);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("权限不存在", result.getMessage());
    }

    // ==================== getAllPermissions 测试 ====================

    @Test
    @DisplayName("查询所有权限成功 - 有权限数据")
    void getAllPermissions_Success_WithData() {
        // Given: 有权限数据
        Permission perm1 = new Permission();
        perm1.setId(1);
        perm1.setName("权限 1");
        perm1.setCode("PERMISSION_1");
        perm1.setSort(1);

        Permission perm2 = new Permission();
        perm2.setId(2);
        perm2.setName("权限 2");
        perm2.setCode("PERMISSION_2");
        perm2.setSort(2);

        when(permissionMapper.selectList(any(QueryWrapper.class))).thenReturn(Arrays.asList(perm1, perm2));

        // When
        R result = permissionService.getAllPermissions();

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("查询成功", result.getMessage());

        List<Permission> permissions = (List<Permission>) result.getData();
        assertNotNull(permissions);
        assertEquals(2, permissions.size());

        verify(permissionMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("查询所有权限成功 - 空数据")
    void getAllPermissions_Success_EmptyData() {
        // Given: 没有权限数据
        when(permissionMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        // When
        R result = permissionService.getAllPermissions();

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());

        List<Permission> permissions = (List<Permission>) result.getData();
        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());
    }

    // ==================== getPermissionTree 测试 ====================

    @Test
    @DisplayName("获取权限树成功 - 有根节点和子节点")
    void getPermissionTree_Success_WithHierarchy() {
        // Given: 有层级结构的权限数据
        Permission rootPerm = new Permission();
        rootPerm.setId(1);
        rootPerm.setName("根权限");
        rootPerm.setCode("ROOT");
        rootPerm.setParentId(null);
        rootPerm.setSort(1);

        Permission childPerm1 = new Permission();
        childPerm1.setId(2);
        childPerm1.setName("子权限 1");
        childPerm1.setCode("CHILD_1");
        childPerm1.setParentId(1);
        childPerm1.setSort(1);

        Permission childPerm2 = new Permission();
        childPerm2.setId(3);
        childPerm2.setName("子权限 2");
        childPerm2.setCode("CHILD_2");
        childPerm2.setParentId(1);
        childPerm2.setSort(2);

        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(rootPerm, childPerm1, childPerm2));

        // When
        R result = permissionService.getPermissionTree();

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());

        List<Permission> tree = (List<Permission>) result.getData();
        assertNotNull(tree);
        assertEquals(1, tree.size());
        assertEquals(1, tree.get(0).getId());
        assertNotNull(tree.get(0).getChildren());
        assertEquals(2, tree.get(0).getChildren().size());

        verify(permissionMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取权限树成功 - 只有根节点")
    void getPermissionTree_Success_OnlyRoot() {
        // Given: 只有根节点权限
        Permission rootPerm = new Permission();
        rootPerm.setId(1);
        rootPerm.setName("根权限");
        rootPerm.setCode("ROOT");
        rootPerm.setParentId(null);
        rootPerm.setSort(1);

        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(rootPerm));

        // When
        R result = permissionService.getPermissionTree();

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());

        List<Permission> tree = (List<Permission>) result.getData();
        assertNotNull(tree);
        assertEquals(1, tree.size());
        assertNull(tree.get(0).getChildren());
    }

    @Test
    @DisplayName("获取权限树成功 - 空数据")
    void getPermissionTree_Success_EmptyData() {
        // Given: 没有权限数据
        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // When
        R result = permissionService.getPermissionTree();

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());

        List<Permission> tree = (List<Permission>) result.getData();
        assertNotNull(tree);
        assertTrue(tree.isEmpty());
    }

    @Test
    @DisplayName("获取权限树成功 - 多层级结构")
    void getPermissionTree_Success_MultiLevel() {
        // Given: 三层级权限结构
        Permission level1 = new Permission();
        level1.setId(1);
        level1.setName("一级权限");
        level1.setCode("LEVEL_1");
        level1.setParentId(null);
        level1.setSort(1);

        Permission level2 = new Permission();
        level2.setId(2);
        level2.setName("二级权限");
        level2.setCode("LEVEL_2");
        level2.setParentId(1);
        level2.setSort(1);

        Permission level3 = new Permission();
        level3.setId(3);
        level3.setName("三级权限");
        level3.setCode("LEVEL_3");
        level3.setParentId(2);
        level3.setSort(1);

        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(level1, level2, level3));

        // When
        R result = permissionService.getPermissionTree();

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());

        List<Permission> tree = (List<Permission>) result.getData();
        assertNotNull(tree);
        assertEquals(1, tree.size());
        assertEquals(1, tree.get(0).getChildren().size());
        assertEquals(1, tree.get(0).getChildren().get(0).getChildren().size());
    }

    // ==================== getPermissionsByRoleId 测试 ====================

    @Test
    @DisplayName("获取角色权限成功 - 有权限数据")
    void getPermissionsByRoleId_Success_WithData() {
        // Given: 角色有权限
        RolePermission rp1 = new RolePermission();
        rp1.setId(1);
        rp1.setRoleId(1);
        rp1.setPermissionId(1);

        RolePermission rp2 = new RolePermission();
        rp2.setId(2);
        rp2.setRoleId(1);
        rp2.setPermissionId(2);

        Permission perm1 = new Permission();
        perm1.setId(1);
        perm1.setName("权限 1");
        perm1.setCode("PERMISSION_1");

        Permission perm2 = new Permission();
        perm2.setId(2);
        perm2.setName("权限 2");
        perm2.setCode("PERMISSION_2");

        when(rolePermissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(rp1, rp2));
        when(permissionMapper.selectBatchIds(Arrays.asList(1, 2)))
                .thenReturn(Arrays.asList(perm1, perm2));

        // When
        R result = permissionService.getPermissionsByRoleId(1);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());

        List<Permission> permissions = (List<Permission>) result.getData();
        assertNotNull(permissions);
        assertEquals(2, permissions.size());

        verify(rolePermissionMapper, times(1)).selectList(any(QueryWrapper.class));
        verify(permissionMapper, times(1)).selectBatchIds(any());
    }

    @Test
    @DisplayName("获取角色权限成功 - 无权限数据")
    void getPermissionsByRoleId_Success_NoPermissions() {
        // Given: 角色没有权限
        when(rolePermissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // When
        R result = permissionService.getPermissionsByRoleId(1);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());

        List<Permission> permissions = (List<Permission>) result.getData();
        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());
    }

    @Test
    @DisplayName("获取角色权限失败 - 角色 ID 为空")
    void getPermissionsByRoleId_RoleIdIsNull_Fail() {
        // Given: 角色 ID 为空

        // When
        R result = permissionService.getPermissionsByRoleId(null);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("角色ID不能为空", result.getMessage());
    }

    // ==================== assignPermissionsToRole 测试 ====================

    @Test
    @DisplayName("分配权限给角色成功 - 有多个权限")
    void assignPermissionsToRole_Success_MultiplePermissions() {
        // Given: 分配多个权限给角色
        List<Integer> permissionIds = Arrays.asList(1, 2, 3);
        when(rolePermissionMapper.delete(any(QueryWrapper.class))).thenReturn(1);
        when(rolePermissionMapper.insert(any(RolePermission.class))).thenReturn(1);

        // When
        R result = permissionService.assignPermissionsToRole(1, permissionIds);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("分配权限成功", result.getMessage());

        verify(rolePermissionMapper, times(1)).delete(any(QueryWrapper.class));
        verify(rolePermissionMapper, times(3)).insert(any(RolePermission.class));
    }

    @Test
    @DisplayName("分配权限给角色成功 - 清空所有权限")
    void assignPermissionsToRole_Success_EmptyPermissions() {
        // Given: 清空角色的所有权限
        List<Integer> permissionIds = new ArrayList<>();
        when(rolePermissionMapper.delete(any(QueryWrapper.class))).thenReturn(1);

        // When
        R result = permissionService.assignPermissionsToRole(1, permissionIds);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("分配权限成功", result.getMessage());

        verify(rolePermissionMapper, times(1)).delete(any(QueryWrapper.class));
        verify(rolePermissionMapper, never()).insert(any(RolePermission.class));
    }

    @Test
    @DisplayName("分配权限给角色成功 - 权限列表为 null")
    void assignPermissionsToRole_Success_NullPermissions() {
        // Given: 权限列表为 null
        when(rolePermissionMapper.delete(any(QueryWrapper.class))).thenReturn(1);

        // When
        R result = permissionService.assignPermissionsToRole(1, null);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("分配权限成功", result.getMessage());

        verify(rolePermissionMapper, times(1)).delete(any(QueryWrapper.class));
        verify(rolePermissionMapper, never()).insert(any(RolePermission.class));
    }

    @Test
    @DisplayName("分配权限给角色失败 - 角色 ID 为空")
    void assignPermissionsToRole_RoleIdIsNull_Fail() {
        // Given: 角色 ID 为空

        // When
        R result = permissionService.assignPermissionsToRole(null, Arrays.asList(1, 2));

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("角色ID不能为空", result.getMessage());

        verify(rolePermissionMapper, never()).delete(any(QueryWrapper.class));
        verify(rolePermissionMapper, never()).insert(any(RolePermission.class));
    }

    // ==================== 边界条件测试 ====================

    @Test
    @DisplayName("创建权限时默认设置状态为启用")
    void addPermission_DefaultStatusEnabled() {
        // Given
        when(permissionMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(permissionMapper.insert(any(Permission.class))).thenReturn(1);

        // When
        PermissionRequest request = new PermissionRequest();
        request.setName("新权限");
        request.setCode("NEW_PERMISSION");
        R result = permissionService.addPermission(request);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());

        // 验证插入的 Permission 对象 status 为 1
        verify(permissionMapper, times(1)).insert(argThat(permission ->
            permission != null && permission.getStatus() != null && permission.getStatus() == 1
        ));
    }

    @Test
    @DisplayName("权限树构建 - 所有权限都是根节点")
    void getPermissionTree_AllRootNodes() {
        // Given: 所有权限都是根节点（parentId 为 null）
        Permission perm1 = new Permission();
        perm1.setId(1);
        perm1.setName("权限 1");
        perm1.setParentId(null);
        perm1.setSort(1);

        Permission perm2 = new Permission();
        perm2.setId(2);
        perm2.setName("权限 2");
        perm2.setParentId(null);
        perm2.setSort(2);

        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(perm1, perm2));

        // When
        R result = permissionService.getPermissionTree();

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());

        List<Permission> tree = (List<Permission>) result.getData();
        assertNotNull(tree);
        assertEquals(2, tree.size());
        assertNull(tree.get(0).getChildren());
        assertNull(tree.get(1).getChildren());
    }

    @Test
    @DisplayName("权限树构建 - 跳过已删除的子节点")
    void getPermissionTree_WithMissingChildren() {
        // Given: 子权限的父 ID 指向不存在的权限
        Permission childPerm = new Permission();
        childPerm.setId(2);
        childPerm.setName("子权限");
        childPerm.setCode("CHILD");
        childPerm.setParentId(999); // 父权限不存在
        childPerm.setSort(1);

        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(childPerm));

        // When
        R result = permissionService.getPermissionTree();

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());

        List<Permission> tree = (List<Permission>) result.getData();
        assertNotNull(tree);
        assertTrue(tree.isEmpty()); // 因为没有根节点，所以树为空
    }

    @Test
    @DisplayName("获取角色权限 - 权限 ID 列表中有不存在的权限")
    void getPermissionsByRoleId_WithMissingPermissions() {
        // Given: 角色权限关联存在，但部分权限已被删除
        RolePermission rp = new RolePermission();
        rp.setId(1);
        rp.setRoleId(1);
        rp.setPermissionId(999);

        when(rolePermissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(rp));
        when(permissionMapper.selectBatchIds(Collections.singletonList(999)))
                .thenReturn(Collections.emptyList());

        // When
        R result = permissionService.getPermissionsByRoleId(1);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());

        List<Permission> permissions = (List<Permission>) result.getData();
        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());
    }

    @Test
    @DisplayName("分配权限 - 删除原有权限失败不影响分配")
    void assignPermissionsToRole_DeleteFail_StillAssign() {
        // Given: 删除原有权限失败，但仍尝试分配新权限
        List<Integer> permissionIds = Arrays.asList(1, 2);
        when(rolePermissionMapper.delete(any(QueryWrapper.class))).thenReturn(0);
        when(rolePermissionMapper.insert(any(RolePermission.class))).thenReturn(1);

        // When
        R result = permissionService.assignPermissionsToRole(1, permissionIds);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("分配权限成功", result.getMessage());

        verify(rolePermissionMapper, times(1)).delete(any(QueryWrapper.class));
        verify(rolePermissionMapper, times(2)).insert(any(RolePermission.class));
    }
}
