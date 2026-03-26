package com.example.yin.controller;

import com.example.yin.model.request.RoleRequest;
import com.example.yin.config.TestMinioConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RoleController 集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestMinioConfig.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试获取角色列表
     */
    @Test
    void allRole_shouldReturnRoleList() throws Exception {
        mockMvc.perform(get("/system/role/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").exists());
    }

    /**
     * 测试根据 ID 获取角色详情
     */
    @Test
    void roleOfId_shouldReturnRoleDetail() throws Exception {
        mockMvc.perform(get("/system/role/detail")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试添加角色
     */
    @Test
    void addRole_shouldReturnSuccessResult() throws Exception {
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("测试角色");
        roleRequest.setCode("TEST_ROLE");
        roleRequest.setDescription("测试角色描述");
        roleRequest.setStatus(1);

        mockMvc.perform(post("/system/role/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试更新角色
     */
    @Test
    void updateRole_shouldReturnSuccessResult() throws Exception {
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setId(1);
        roleRequest.setName("更新后的角色名");
        roleRequest.setCode("UPDATED_ROLE");
        roleRequest.setDescription("更新后的描述");
        roleRequest.setStatus(1);

        mockMvc.perform(put("/system/role/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试删除角色
     */
    @Test
    void deleteRole_shouldReturnSuccessResult() throws Exception {
        mockMvc.perform(delete("/system/role/delete")
                .param("id", "999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试根据状态查询角色列表
     */
    @Test
    void rolesOfStatus_shouldReturnRoleListByStatus() throws Exception {
        mockMvc.perform(get("/system/role/listByStatus")
                .param("status", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试获取所有状态的角色（不传 status 参数）
     */
    @Test
    void rolesOfStatus_withoutStatus_shouldReturnAllRoles() throws Exception {
        mockMvc.perform(get("/system/role/listByStatus")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试获取不存在的角色 ID
     */
    @Test
    void roleOfId_withNonExistentId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(get("/system/role/detail")
                .param("id", "999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试缺少必需参数
     */
    @Test
    void roleOfId_withoutId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/system/role/detail")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试获取角色列表返回数组
     */
    @Test
    void allRole_shouldReturnArray() throws Exception {
        mockMvc.perform(get("/system/role/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * 测试添加角色 - 缺少必填字段
     */
    @Test
    void addRole_withMissingFields_shouldReturnSuccess() throws Exception {
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("仅名称角色");

        mockMvc.perform(post("/system/role/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(status().isOk());
    }

    /**
     * 测试更新不存在的角色
     */
    @Test
    void updateRole_withNonExistentId_shouldReturnAppropriateResponse() throws Exception {
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setId(999999);
        roleRequest.setName("不存在的角色");

        mockMvc.perform(put("/system/role/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(status().isOk());
    }

    /**
     * 测试删除不存在的角色
     */
    @Test
    void deleteRole_withNonExistentId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(delete("/system/role/delete")
                .param("id", "999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试按状态查询 - 状态为 0
     */
    @Test
    void rolesOfStatus_withZeroStatus_shouldReturnRoleList() throws Exception {
        mockMvc.perform(get("/system/role/listByStatus")
                .param("status", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
