package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.common.R;
import com.example.yin.mapper.PermissionMapper;
import com.example.yin.mapper.RoleMapper;
import com.example.yin.mapper.RolePermissionMapper;
import com.example.yin.model.domain.Permission;
import com.example.yin.model.domain.Role;
import com.example.yin.model.domain.RolePermission;
import com.example.yin.service.impl.PermissionTemplateServiceImpl;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * PermissionTemplateService 单元测试
 * 覆盖权限模板管理的核心功能测试
 */
@SpringBootTest(classes = {PermissionTemplateServiceImpl.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@DisplayName("PermissionTemplateService 测试")
class PermissionTemplateServiceTest {

    @MockBean
    private PermissionMapper permissionMapper;

    @MockBean
    private RoleMapper roleMapper;

    @MockBean
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private PermissionTemplateService permissionTemplateService;

    private Permission testPermission;
    private Role testRole;
    private RolePermission testRolePermission;

    @BeforeEach
    void setUp() {
        // 初始化测试权限
        testPermission = new Permission();
        testPermission.setId(1);
        testPermission.setName("测试权限");
        testPermission.setCode("song:list");
        testPermission.setType("MENU");
        testPermission.setUrl("/song");
        testPermission.setMethod("GET");
        testPermission.setParentId(null);
        testPermission.setSort(1);
        testPermission.setStatus(1);
        testPermission.setCreateTime(LocalDateTime.now());
        testPermission.setUpdateTime(LocalDateTime.now());

        // 初始化测试角色
        testRole = new Role();
        testRole.setId(1);
        testRole.setName("测试角色");
        testRole.setCode("TEST_ROLE");
        testRole.setDescription("测试角色描述");
        testRole.setStatus(1);
        testRole.setCreateTime(LocalDateTime.now());
        testRole.setUpdateTime(LocalDateTime.now());

        // 初始化测试角色权限关联
        testRolePermission = new RolePermission();
        testRolePermission.setId(1);
        testRolePermission.setRoleId(1);
        testRolePermission.setPermissionId(1);
        testRolePermission.setCreateTime(LocalDateTime.now());
    }

    // ==================== getTemplateByRole 测试 ====================

    @Test
    @DisplayName("根据角色获取权限模板成功 - SUPER_ADMIN")
    void getTemplateByRole_Success_SuperAdmin() {
        // Given: SUPER_ADMIN 角色，模板返回空权限列表
        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        // When
        R result = permissionTemplateService.getTemplateByRole("SUPER_ADMIN");

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("查询成功", result.getMessage());

        // SUPER_ADMIN 也会查询数据库，但返回空列表
        verify(permissionMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("根据角色获取权限模板成功 - USER")
    void getTemplateByRole_Success_User() {
        // Given: USER 角色，有 4 个权限
        List<Permission> permissions = new ArrayList<>();
        permissions.add(createPermission(1, "song:list", "歌曲列表"));
        permissions.add(createPermission(2, "singer:list", "歌手列表"));
        permissions.add(createPermission(3, "songlist:list", "歌单列表"));
        permissions.add(createPermission(4, "comment:list", "评论列表"));

        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(permissions);

        // When
        R result = permissionTemplateService.getTemplateByRole("USER");

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("查询成功", result.getMessage());

        List<Permission> resultPermissions = (List<Permission>) result.getData();
        assertNotNull(resultPermissions);
        assertEquals(4, resultPermissions.size());

        verify(permissionMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("根据角色获取权限模板成功 - SINGER")
    void getTemplateByRole_Success_Singer() {
        // Given: SINGER 角色，有 4 个权限
        List<Permission> permissions = new ArrayList<>();
        permissions.add(createPermission(1, "song:list", "歌曲列表"));
        permissions.add(createPermission(2, "song:add", "添加歌曲"));
        permissions.add(createPermission(3, "song:update", "更新歌曲"));
        permissions.add(createPermission(4, "singer:list", "歌手列表"));

        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(permissions);

        // When
        R result = permissionTemplateService.getTemplateByRole("SINGER");

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("查询成功", result.getMessage());

        List<Permission> resultPermissions = (List<Permission>) result.getData();
        assertNotNull(resultPermissions);
        assertEquals(4, resultPermissions.size());

        verify(permissionMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("根据角色获取权限模板成功 - ADMIN_BASIC")
    void getTemplateByRole_Success_AdminBasic() {
        // Given: ADMIN_BASIC 角色，有 5 个权限
        List<Permission> permissions = new ArrayList<>();
        permissions.add(createPermission(1, "system:user", "系统用户"));
        permissions.add(createPermission(2, "system:role", "系统角色"));
        permissions.add(createPermission(3, "song:list", "歌曲列表"));
        permissions.add(createPermission(4, "singer:list", "歌手列表"));
        permissions.add(createPermission(5, "songlist:list", "歌单列表"));

        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(permissions);

        // When
        R result = permissionTemplateService.getTemplateByRole("ADMIN_BASIC");

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("查询成功", result.getMessage());

        List<Permission> resultPermissions = (List<Permission>) result.getData();
        assertNotNull(resultPermissions);
        assertEquals(5, resultPermissions.size());

        verify(permissionMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("根据角色获取权限模板失败 - 模板不存在")
    void getTemplateByRole_TemplateNotExist_Fail() {
        // Given: 不存在的角色模板

        // When
        R result = permissionTemplateService.getTemplateByRole("INVALID_ROLE");

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("模板不存在", result.getMessage());

        verify(permissionMapper, never()).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("根据角色获取权限模板失败 - 角色代码为 null")
    void getTemplateByRole_RoleCodeIsNull_Fail() {
        // Given: 角色代码为 null

        // When
        R result = permissionTemplateService.getTemplateByRole(null);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("模板不存在", result.getMessage());

        verify(permissionMapper, never()).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("根据角色获取权限模板 - 部分权限在数据库中不存在")
    void getTemplateByRole_PartialPermissionsNotExist() {
        // Given: USER 角色，但数据库中只返回部分权限
        List<Permission> permissions = new ArrayList<>();
        permissions.add(createPermission(1, "song:list", "歌曲列表"));
        permissions.add(createPermission(2, "singer:list", "歌手列表"));
        // comment:list 权限不存在

        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(permissions);

        // When
        R result = permissionTemplateService.getTemplateByRole("USER");

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());

        List<Permission> resultPermissions = (List<Permission>) result.getData();
        assertNotNull(resultPermissions);
        assertEquals(2, resultPermissions.size());

        verify(permissionMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    // ==================== getAllTemplates 测试 ====================

    @Test
    @DisplayName("获取所有权限模板成功")
    void getAllTemplates_Success() {
        // When
        R result = permissionTemplateService.getAllTemplates();

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("查询成功", result.getMessage());

        List<Map<String, Object>> templates = (List<Map<String, Object>>) result.getData();
        assertNotNull(templates);
        assertTrue(templates.size() >= 4); // 至少有 SUPER_ADMIN, USER, SINGER, ADMIN_BASIC

        // 验证模板结构
        for (Map<String, Object> template : templates) {
            assertTrue(template.containsKey("name"));
            assertTrue(template.containsKey("permissions"));
            assertTrue(template.containsKey("count"));
        }
    }

    @Test
    @DisplayName("获取所有权限模板 - 验证 USER 模板内容")
    void getAllTemplates_VerifyUserTemplate() {
        // When
        R result = permissionTemplateService.getAllTemplates();

        // Then
        List<Map<String, Object>> templates = (List<Map<String, Object>>) result.getData();
        Map<String, Object> userTemplate = null;

        for (Map<String, Object> template : templates) {
            if ("USER".equals(template.get("name"))) {
                userTemplate = template;
                break;
            }
        }

        assertNotNull(userTemplate);
        assertEquals("USER", userTemplate.get("name"));
        assertEquals(4, userTemplate.get("count"));

        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) userTemplate.get("permissions");
        assertNotNull(permissions);
        assertTrue(permissions.contains("song:list"));
        assertTrue(permissions.contains("singer:list"));
        assertTrue(permissions.contains("songlist:list"));
        assertTrue(permissions.contains("comment:list"));
    }

    @Test
    @DisplayName("获取所有权限模板 - 验证 SINGER 模板内容")
    void getAllTemplates_VerifySingerTemplate() {
        // When
        R result = permissionTemplateService.getAllTemplates();

        // Then
        List<Map<String, Object>> templates = (List<Map<String, Object>>) result.getData();
        Map<String, Object> singerTemplate = null;

        for (Map<String, Object> template : templates) {
            if ("SINGER".equals(template.get("name"))) {
                singerTemplate = template;
                break;
            }
        }

        assertNotNull(singerTemplate);
        assertEquals("SINGER", singerTemplate.get("name"));
        assertEquals(4, singerTemplate.get("count"));

        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) singerTemplate.get("permissions");
        assertNotNull(permissions);
        assertTrue(permissions.contains("song:list"));
        assertTrue(permissions.contains("song:add"));
        assertTrue(permissions.contains("song:update"));
        assertTrue(permissions.contains("singer:list"));
    }

    @Test
    @DisplayName("获取所有权限模板 - 验证 SUPER_ADMIN 模板内容")
    void getAllTemplates_VerifySuperAdminTemplate() {
        // When
        R result = permissionTemplateService.getAllTemplates();

        // Then
        List<Map<String, Object>> templates = (List<Map<String, Object>>) result.getData();
        Map<String, Object> superAdminTemplate = null;

        for (Map<String, Object> template : templates) {
            if ("SUPER_ADMIN".equals(template.get("name"))) {
                superAdminTemplate = template;
                break;
            }
        }

        assertNotNull(superAdminTemplate);
        assertEquals("SUPER_ADMIN", superAdminTemplate.get("name"));
        assertEquals(0, superAdminTemplate.get("count"));

        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) superAdminTemplate.get("permissions");
        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());
    }

    @Test
    @DisplayName("获取所有权限模板 - 验证 ADMIN_BASIC 模板内容")
    void getAllTemplates_VerifyAdminBasicTemplate() {
        // When
        R result = permissionTemplateService.getAllTemplates();

        // Then
        List<Map<String, Object>> templates = (List<Map<String, Object>>) result.getData();
        Map<String, Object> adminBasicTemplate = null;

        for (Map<String, Object> template : templates) {
            if ("ADMIN_BASIC".equals(template.get("name"))) {
                adminBasicTemplate = template;
                break;
            }
        }

        assertNotNull(adminBasicTemplate);
        assertEquals("ADMIN_BASIC", adminBasicTemplate.get("name"));
        assertEquals(5, adminBasicTemplate.get("count"));

        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) adminBasicTemplate.get("permissions");
        assertNotNull(permissions);
        assertTrue(permissions.contains("system:user"));
        assertTrue(permissions.contains("system:role"));
        assertTrue(permissions.contains("song:list"));
        assertTrue(permissions.contains("singer:list"));
        assertTrue(permissions.contains("songlist:list"));
    }

    // ==================== applyTemplateToRole 测试 ====================

    @Test
    @DisplayName("应用权限模板到角色成功 - USER 模板")
    void applyTemplateToRole_Success_UserTemplate() {
        // Given: 角色存在，应用 USER 模板
        when(roleMapper.selectById(1)).thenReturn(testRole);
        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(
                        createPermission(1, "song:list", "歌曲列表"),
                        createPermission(2, "singer:list", "歌手列表"),
                        createPermission(3, "songlist:list", "歌单列表"),
                        createPermission(4, "comment:list", "评论列表")
                ));
        when(rolePermissionMapper.delete(any(QueryWrapper.class))).thenReturn(1);
        when(rolePermissionMapper.insert(any(RolePermission.class))).thenReturn(1);

        // When
        R result = permissionTemplateService.applyTemplateToRole("USER", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("应用模板成功", result.getMessage());

        verify(roleMapper, times(1)).selectById(1);
        verify(rolePermissionMapper, times(1)).delete(any(QueryWrapper.class));
        verify(rolePermissionMapper, times(4)).insert(any(RolePermission.class));
    }

    @Test
    @DisplayName("应用权限模板到角色成功 - SINGER 模板")
    void applyTemplateToRole_Success_SingerTemplate() {
        // Given: 角色存在，应用 SINGER 模板
        when(roleMapper.selectById(1)).thenReturn(testRole);
        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(
                        createPermission(1, "song:list", "歌曲列表"),
                        createPermission(2, "song:add", "添加歌曲"),
                        createPermission(3, "song:update", "更新歌曲"),
                        createPermission(4, "singer:list", "歌手列表")
                ));
        when(rolePermissionMapper.delete(any(QueryWrapper.class))).thenReturn(1);
        when(rolePermissionMapper.insert(any(RolePermission.class))).thenReturn(1);

        // When
        R result = permissionTemplateService.applyTemplateToRole("SINGER", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("应用模板成功", result.getMessage());

        verify(roleMapper, times(1)).selectById(1);
        verify(rolePermissionMapper, times(1)).delete(any(QueryWrapper.class));
        verify(rolePermissionMapper, times(4)).insert(any(RolePermission.class));
    }

    @Test
    @DisplayName("应用权限模板到角色成功 - SUPER_ADMIN 模板")
    void applyTemplateToRole_Success_SuperAdminTemplate() {
        // Given: 角色存在，应用 SUPER_ADMIN 模板（无权限）
        when(roleMapper.selectById(1)).thenReturn(testRole);
        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        when(rolePermissionMapper.delete(any(QueryWrapper.class))).thenReturn(1);

        // When
        R result = permissionTemplateService.applyTemplateToRole("SUPER_ADMIN", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("应用模板成功", result.getMessage());

        verify(roleMapper, times(1)).selectById(1);
        verify(rolePermissionMapper, times(1)).delete(any(QueryWrapper.class));
        verify(rolePermissionMapper, never()).insert(any(RolePermission.class));
    }

    @Test
    @DisplayName("应用权限模板到角色成功 - ADMIN_BASIC 模板")
    void applyTemplateToRole_Success_AdminBasicTemplate() {
        // Given: 角色存在，应用 ADMIN_BASIC 模板
        when(roleMapper.selectById(1)).thenReturn(testRole);
        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(
                        createPermission(1, "system:user", "系统用户"),
                        createPermission(2, "system:role", "系统角色"),
                        createPermission(3, "song:list", "歌曲列表"),
                        createPermission(4, "singer:list", "歌手列表"),
                        createPermission(5, "songlist:list", "歌单列表")
                ));
        when(rolePermissionMapper.delete(any(QueryWrapper.class))).thenReturn(1);
        when(rolePermissionMapper.insert(any(RolePermission.class))).thenReturn(1);

        // When
        R result = permissionTemplateService.applyTemplateToRole("ADMIN_BASIC", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("应用模板成功", result.getMessage());

        verify(roleMapper, times(1)).selectById(1);
        verify(rolePermissionMapper, times(1)).delete(any(QueryWrapper.class));
        verify(rolePermissionMapper, times(5)).insert(any(RolePermission.class));
    }

    @Test
    @DisplayName("应用权限模板到角色失败 - 角色不存在")
    void applyTemplateToRole_RoleNotExist_Fail() {
        // Given: 角色不存在
        when(roleMapper.selectById(999)).thenReturn(null);

        // When
        R result = permissionTemplateService.applyTemplateToRole("USER", 999);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("角色不存在", result.getMessage());

        verify(roleMapper, times(1)).selectById(999);
        verify(rolePermissionMapper, never()).delete(any(QueryWrapper.class));
        verify(permissionMapper, never()).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("应用权限模板到角色失败 - 模板不存在")
    void applyTemplateToRole_TemplateNotExist_Fail() {
        // Given: 模板不存在
        when(roleMapper.selectById(1)).thenReturn(testRole);

        // When
        R result = permissionTemplateService.applyTemplateToRole("INVALID_TEMPLATE", 1);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("模板不存在", result.getMessage());

        verify(roleMapper, times(1)).selectById(1);
        verify(rolePermissionMapper, never()).delete(any(QueryWrapper.class));
        verify(permissionMapper, never()).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("应用权限模板到角色失败 - 模板名称为 null")
    void applyTemplateToRole_TemplateNameIsNull_Fail() {
        // Given: 模板名称为 null，但先检查角色是否存在
        when(roleMapper.selectById(1)).thenReturn(testRole);

        // When
        R result = permissionTemplateService.applyTemplateToRole(null, 1);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("模板不存在", result.getMessage());

        verify(roleMapper, times(1)).selectById(1);
        verify(rolePermissionMapper, never()).delete(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("应用权限模板到角色失败 - 角色 ID 为 null")
    void applyTemplateToRole_RoleIdIsNull_Fail() {
        // Given: 角色 ID 为 null

        // When
        R result = permissionTemplateService.applyTemplateToRole("USER", null);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());

        verify(roleMapper, never()).selectById(anyInt());
        verify(rolePermissionMapper, never()).delete(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("应用权限模板到角色 - 部分权限在数据库中不存在")
    void applyTemplateToRole_PartialPermissionsNotExist() {
        // Given: 角色存在，但部分权限在数据库中不存在
        when(roleMapper.selectById(1)).thenReturn(testRole);
        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(
                        createPermission(1, "song:list", "歌曲列表"),
                        createPermission(2, "singer:list", "歌手列表")
                        // songlist:list 和 comment:list 不存在
                ));
        when(rolePermissionMapper.delete(any(QueryWrapper.class))).thenReturn(1);
        when(rolePermissionMapper.insert(any(RolePermission.class))).thenReturn(1);

        // When
        R result = permissionTemplateService.applyTemplateToRole("USER", 1);

        // Then
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("应用模板成功", result.getMessage());

        verify(roleMapper, times(1)).selectById(1);
        verify(rolePermissionMapper, times(1)).delete(any(QueryWrapper.class));
        verify(rolePermissionMapper, times(2)).insert(any(RolePermission.class));
    }

    // ==================== 边界条件测试 ====================

    @Test
    @DisplayName("应用模板 - 事务回滚测试（删除失败场景）")
    void applyTemplateToRole_TransactionRollback_Scene() {
        // Given: 角色存在，但删除原有权限失败
        when(roleMapper.selectById(1)).thenReturn(testRole);
        when(permissionMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(
                        createPermission(1, "song:list", "歌曲列表")
                ));
        when(rolePermissionMapper.delete(any(QueryWrapper.class))).thenReturn(0);
        when(rolePermissionMapper.insert(any(RolePermission.class))).thenReturn(1);

        // When: 删除失败但仍尝试插入（实际业务中可能会抛出异常）
        R result = permissionTemplateService.applyTemplateToRole("USER", 1);

        // Then: 由于@Transactional，删除失败可能导致整个事务回滚
        // 这里验证即使删除返回 0，方法仍然执行了插入操作
        verify(roleMapper, times(1)).selectById(1);
        verify(rolePermissionMapper, times(1)).delete(any(QueryWrapper.class));
        verify(rolePermissionMapper, times(1)).insert(any(RolePermission.class));
    }

    @Test
    @DisplayName("获取模板 - 空字符串角色代码")
    void getTemplateByRole_EmptyRoleCode_Fail() {
        // Given: 空字符串角色代码

        // When
        R result = permissionTemplateService.getTemplateByRole("");

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("模板不存在", result.getMessage());

        verify(permissionMapper, never()).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("应用模板 - 空字符串模板名称")
    void applyTemplateToRole_EmptyTemplateName_Fail() {
        // Given: 空字符串模板名称，但先检查角色是否存在
        when(roleMapper.selectById(1)).thenReturn(testRole);

        // When
        R result = permissionTemplateService.applyTemplateToRole("", 1);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("模板不存在", result.getMessage());

        verify(roleMapper, times(1)).selectById(1);
        verify(rolePermissionMapper, never()).delete(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("应用模板 - 角色 ID 为负数")
    void applyTemplateToRole_NegativeRoleId() {
        // Given: 角色 ID 为负数，数据库中不存在
        when(roleMapper.selectById(-1)).thenReturn(null);

        // When
        R result = permissionTemplateService.applyTemplateToRole("USER", -1);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("角色不存在", result.getMessage());

        verify(roleMapper, times(1)).selectById(-1);
    }

    @Test
    @DisplayName("应用模板 - 角色 ID 为 0")
    void applyTemplateToRole_ZeroRoleId() {
        // Given: 角色 ID 为 0，数据库中不存在
        when(roleMapper.selectById(0)).thenReturn(null);

        // When
        R result = permissionTemplateService.applyTemplateToRole("USER", 0);

        // Then
        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("角色不存在", result.getMessage());

        verify(roleMapper, times(1)).selectById(0);
    }

    // ==================== 辅助方法 ====================

    private Permission createPermission(Integer id, String code, String name) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setCode(code);
        permission.setName(name);
        permission.setType("MENU");
        permission.setUrl("/" + code.replace(":", "/"));
        permission.setMethod("GET");
        permission.setParentId(null);
        permission.setSort(1);
        permission.setStatus(1);
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateTime(LocalDateTime.now());
        return permission;
    }
}
