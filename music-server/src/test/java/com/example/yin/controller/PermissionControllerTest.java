package com.example.yin.controller;

import com.example.yin.model.request.PermissionRequest;
import com.example.yin.model.request.RolePermissionRequest;
import com.example.yin.security.JwtTokenProvider;
import com.example.yin.security.TokenBlacklistService;
import com.example.yin.service.PermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PermissionController 集成测试
 */
@WebMvcTest(PermissionController.class)
class PermissionControllerTest {

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private TokenBlacklistService blacklistService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试获取权限树
     */
    @Test
    void getPermissionTree_shouldReturnTreeStructure() throws Exception {
        mockMvc.perform(get("/system/permission/tree")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").exists());
    }

    /**
     * 测试获取权限树返回数据结构
     */
    @Test
    void getPermissionTree_shouldReturnDataStructure() throws Exception {
        mockMvc.perform(get("/system/permission/tree")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试获取角色权限（根据角色 ID）
     */
    @Test
    void getPermissionsByRoleId_shouldReturnPermissionList() throws Exception {
        mockMvc.perform(get("/system/permission/listByRole")
                .param("roleId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").exists());
    }

    /**
     * 测试获取角色权限 - 返回权限数组
     */
    @Test
    void getPermissionsByRoleId_shouldReturnPermissionArray() throws Exception {
        mockMvc.perform(get("/system/permission/listByRole")
                .param("roleId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * 测试获取不存在的角色权限
     */
    @Test
    void getPermissionsByRoleId_withNonExistentRoleId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(get("/system/permission/listByRole")
                .param("roleId", "999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试获取角色权限 - 缺少 roleId 参数
     */
    @Test
    void getPermissionsByRoleId_withoutRoleId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/system/permission/listByRole")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试获取所有权限列表
     */
    @Test
    void getAllPermissions_shouldReturnPermissionList() throws Exception {
        mockMvc.perform(get("/system/permission/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").exists());
    }

    /**
     * 测试获取所有权限列表 - 返回数组
     */
    @Test
    void getAllPermissions_shouldReturnArray() throws Exception {
        mockMvc.perform(get("/system/permission/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * 测试根据 ID 获取权限详情
     */
    @Test
    void getPermissionById_shouldReturnPermissionDetail() throws Exception {
        mockMvc.perform(get("/system/permission/detail")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试获取不存在的权限 ID
     */
    @Test
    void getPermissionById_withNonExistentId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(get("/system/permission/detail")
                .param("id", "999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试获取权限详情 - 缺少 id 参数
     */
    @Test
    void getPermissionById_withoutId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/system/permission/detail")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试添加权限
     */
    @Test
    void addPermission_shouldReturnSuccessResult() throws Exception {
        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setName("测试权限");
        permissionRequest.setCode("TEST_PERMISSION");
        permissionRequest.setType("menu");
        permissionRequest.setUrl("/test");
        permissionRequest.setMethod("GET");
        permissionRequest.setParentId(0);
        permissionRequest.setSort(1);
        permissionRequest.setStatus(1);

        mockMvc.perform(post("/system/permission/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试添加权限 - 缺少必填字段
     */
    @Test
    void addPermission_withMissingFields_shouldReturnSuccess() throws Exception {
        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setName("仅名称权限");

        mockMvc.perform(post("/system/permission/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionRequest)))
                .andExpect(status().isOk());
    }

    /**
     * 测试更新权限
     */
    @Test
    void updatePermission_shouldReturnSuccessResult() throws Exception {
        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setId(1);
        permissionRequest.setName("更新后的权限名");
        permissionRequest.setCode("UPDATED_PERMISSION");
        permissionRequest.setType("button");
        permissionRequest.setUrl("/updated");
        permissionRequest.setMethod("POST");
        permissionRequest.setParentId(1);
        permissionRequest.setSort(2);
        permissionRequest.setStatus(1);

        mockMvc.perform(put("/system/permission/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试更新不存在的权限
     */
    @Test
    void updatePermission_withNonExistentId_shouldReturnAppropriateResponse() throws Exception {
        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.setId(999999);
        permissionRequest.setName("不存在的权限");

        mockMvc.perform(put("/system/permission/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionRequest)))
                .andExpect(status().isOk());
    }

    /**
     * 测试删除权限
     */
    @Test
    void deletePermission_shouldReturnSuccessResult() throws Exception {
        mockMvc.perform(delete("/system/permission/delete")
                .param("id", "999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试删除不存在的权限
     */
    @Test
    void deletePermission_withNonExistentId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(delete("/system/permission/delete")
                .param("id", "999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试删除权限 - 缺少 id 参数
     */
    @Test
    void deletePermission_withoutId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/system/permission/delete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试为角色分配权限
     */
    @Test
    void assignPermissionsToRole_shouldReturnSuccessResult() throws Exception {
        RolePermissionRequest request = new RolePermissionRequest();
        request.setRoleId(1);
        List<Integer> permissionIds = Arrays.asList(1, 2, 3, 4, 5);
        request.setPermissionIds(permissionIds);

        mockMvc.perform(post("/system/permission/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试为角色分配权限 - 空权限列表
     */
    @Test
    void assignPermissionsToRole_withEmptyPermissionList_shouldReturnSuccess() throws Exception {
        RolePermissionRequest request = new RolePermissionRequest();
        request.setRoleId(1);
        request.setPermissionIds(Arrays.asList());

        mockMvc.perform(post("/system/permission/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * 测试为角色分配权限 - 缺少 roleId
     */
    @Test
    void assignPermissionsToRole_withoutRoleId_shouldReturnSuccess() throws Exception {
        RolePermissionRequest request = new RolePermissionRequest();
        request.setPermissionIds(Arrays.asList(1, 2, 3));

        mockMvc.perform(post("/system/permission/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * 测试为不存在的角色分配权限
     */
    @Test
    void assignPermissionsToRole_withNonExistentRoleId_shouldReturnAppropriateResponse() throws Exception {
        RolePermissionRequest request = new RolePermissionRequest();
        request.setRoleId(999999);
        request.setPermissionIds(Arrays.asList(1, 2, 3));

        mockMvc.perform(post("/system/permission/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
