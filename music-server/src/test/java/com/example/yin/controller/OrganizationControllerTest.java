package com.example.yin.controller;

import com.example.yin.model.request.OrganizationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * OrganizationController 集成测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试获取组织树
     */
    @Test
    void getOrganizationTree_shouldReturnTreeStructure() throws Exception {
        mockMvc.perform(get("/system/organization/tree")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").exists());
    }

    /**
     * 测试获取组织树返回数据结构
     */
    @Test
    void getOrganizationTree_shouldReturnDataStructure() throws Exception {
        mockMvc.perform(get("/system/organization/tree")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试添加组织
     */
    @Test
    void addOrganization_shouldReturnSuccessResult() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setName("测试组织");
        organizationRequest.setParentId(0);
        organizationRequest.setLevel(1);
        organizationRequest.setPath("/1");
        organizationRequest.setSort(1);
        organizationRequest.setStatus(1);

        mockMvc.perform(post("/system/organization/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试添加组织 - 缺少必填字段
     */
    @Test
    void addOrganization_withMissingFields_shouldReturnSuccess() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setName("仅名称组织");

        mockMvc.perform(post("/system/organization/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk());
    }

    /**
     * 测试添加组织 - 根组织
     */
    @Test
    void addOrganization_asRootOrganization_shouldReturnSuccess() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setName("根组织");
        organizationRequest.setParentId(0);
        organizationRequest.setLevel(1);
        organizationRequest.setSort(1);
        organizationRequest.setStatus(1);

        mockMvc.perform(post("/system/organization/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试添加组织 - 子组织
     */
    @Test
    void addOrganization_asChildOrganization_shouldReturnSuccess() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setName("子组织");
        organizationRequest.setParentId(1);
        organizationRequest.setLevel(2);
        organizationRequest.setPath("/1/2");
        organizationRequest.setSort(1);
        organizationRequest.setStatus(1);

        mockMvc.perform(post("/system/organization/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试更新组织
     */
    @Test
    void updateOrganization_shouldReturnSuccessResult() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setId(1);
        organizationRequest.setName("更新后的组织名");
        organizationRequest.setParentId(0);
        organizationRequest.setLevel(1);
        organizationRequest.setPath("/1");
        organizationRequest.setSort(2);
        organizationRequest.setStatus(1);

        mockMvc.perform(put("/system/organization/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试更新不存在的组织
     */
    @Test
    void updateOrganization_withNonExistentId_shouldReturnAppropriateResponse() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setId(999999);
        organizationRequest.setName("不存在的组织");
        organizationRequest.setParentId(0);
        organizationRequest.setLevel(1);
        organizationRequest.setSort(1);
        organizationRequest.setStatus(1);

        mockMvc.perform(put("/system/organization/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk());
    }

    /**
     * 测试更新组织 - 缺少 ID
     */
    @Test
    void updateOrganization_withoutId_shouldReturnSuccess() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setName("没有 ID 的组织");
        organizationRequest.setParentId(0);
        organizationRequest.setLevel(1);
        organizationRequest.setSort(1);
        organizationRequest.setStatus(1);

        mockMvc.perform(put("/system/organization/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk());
    }

    /**
     * 测试删除组织
     */
    @Test
    void deleteOrganization_shouldReturnSuccessResult() throws Exception {
        mockMvc.perform(delete("/system/organization/delete")
                .param("id", "999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试删除不存在的组织
     */
    @Test
    void deleteOrganization_withNonExistentId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(delete("/system/organization/delete")
                .param("id", "999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试删除组织 - 缺少 id 参数
     */
    @Test
    void deleteOrganization_withoutId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/system/organization/delete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试删除组织 - id 为 0
     */
    @Test
    void deleteOrganization_withZeroId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(delete("/system/organization/delete")
                .param("id", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试删除组织 - 负数 ID
     */
    @Test
    void deleteOrganization_withNegativeId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(delete("/system/organization/delete")
                .param("id", "-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试获取组织下用户 - 正常情况
     */
    @Test
    void getOrganizationUsers_shouldReturnUserList() throws Exception {
        mockMvc.perform(get("/system/organization/1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").exists());
    }

    /**
     * 测试获取组织下用户 - 返回数据结构
     */
    @Test
    void getOrganizationUsers_shouldReturnDataStructure() throws Exception {
        mockMvc.perform(get("/system/organization/1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试获取组织下用户 - 不存在的组织 ID
     */
    @Test
    void getOrganizationUsers_withNonExistentId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(get("/system/organization/999999/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试获取组织下用户 - 组织 ID 为 0
     */
    @Test
    void getOrganizationUsers_withZeroId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(get("/system/organization/0/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试获取组织下用户 - 负数组织 ID
     */
    @Test
    void getOrganizationUsers_withNegativeId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(get("/system/organization/-1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试获取组织详情 - 正常情况
     */
    @Test
    void getOrganizationById_shouldReturnOrganizationDetail() throws Exception {
        mockMvc.perform(get("/system/organization/detail")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试获取组织详情 - 不存在的 ID
     */
    @Test
    void getOrganizationById_withNonExistentId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(get("/system/organization/detail")
                .param("id", "999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试获取组织详情 - 缺少 id 参数
     */
    @Test
    void getOrganizationById_withoutId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/system/organization/detail")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试获取所有组织列表
     */
    @Test
    void getAllOrganizations_shouldReturnOrganizationList() throws Exception {
        mockMvc.perform(get("/system/organization/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").exists());
    }

    /**
     * 测试获取所有组织列表 - 返回数组
     */
    @Test
    void getAllOrganizations_shouldReturnArray() throws Exception {
        mockMvc.perform(get("/system/organization/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * 测试获取子组织列表 - 带 parentId 参数
     */
    @Test
    void getChildrenOrganizations_withParentId_shouldReturnSubOrganizationList() throws Exception {
        mockMvc.perform(get("/system/organization/children")
                .param("parentId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试获取子组织列表 - 不带 parentId 参数（获取根组织）
     */
    @Test
    void getChildrenOrganizations_withoutParentId_shouldReturnRootOrganizations() throws Exception {
        mockMvc.perform(get("/system/organization/children")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试获取子组织列表 - parentId 为 0
     */
    @Test
    void getChildrenOrganizations_withZeroParentId_shouldReturnRootOrganizations() throws Exception {
        mockMvc.perform(get("/system/organization/children")
                .param("parentId", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试添加组织 - 状态为 0（禁用）
     */
    @Test
    void addOrganization_withDisabledStatus_shouldReturnSuccess() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setName("禁用状态组织");
        organizationRequest.setParentId(0);
        organizationRequest.setLevel(1);
        organizationRequest.setSort(1);
        organizationRequest.setStatus(0);

        mockMvc.perform(post("/system/organization/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试添加组织 - 无效状态值
     */
    @Test
    void addOrganization_withInvalidStatus_shouldReturnSuccess() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setName("无效状态组织");
        organizationRequest.setParentId(0);
        organizationRequest.setLevel(1);
        organizationRequest.setSort(1);
        organizationRequest.setStatus(-1);

        mockMvc.perform(post("/system/organization/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk());
    }

    /**
     * 测试添加组织 - 空名称
     */
    @Test
    void addOrganization_withEmptyName_shouldReturnSuccess() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setName("");
        organizationRequest.setParentId(0);
        organizationRequest.setLevel(1);
        organizationRequest.setSort(1);
        organizationRequest.setStatus(1);

        mockMvc.perform(post("/system/organization/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk());
    }

    /**
     * 测试更新组织 - 只更新名称
     */
    @Test
    void updateOrganization_onlyName_shouldReturnSuccess() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setId(1);
        organizationRequest.setName("仅更新名称");

        mockMvc.perform(put("/system/organization/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk());
    }

    /**
     * 测试更新组织 - 只更新状态
     */
    @Test
    void updateOrganization_onlyStatus_shouldReturnSuccess() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setId(1);
        organizationRequest.setStatus(0);

        mockMvc.perform(put("/system/organization/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk());
    }

    /**
     * 测试更新组织 - 只更新排序
     */
    @Test
    void updateOrganization_onlySort_shouldReturnSuccess() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setId(1);
        organizationRequest.setSort(10);

        mockMvc.perform(put("/system/organization/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk());
    }

    /**
     * 测试获取组织树返回 JSON 结构完整性
     */
    @Test
    void getOrganizationTree_shouldReturnCompleteJsonStructure() throws Exception {
        mockMvc.perform(get("/system/organization/tree")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.success").exists());
    }

    /**
     * 测试添加组织返回消息字段
     */
    @Test
    void addOrganization_shouldReturnMessageField() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setName("测试消息字段");
        organizationRequest.setParentId(0);
        organizationRequest.setStatus(1);

        mockMvc.perform(post("/system/organization/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * 测试更新组织返回消息字段
     */
    @Test
    void updateOrganization_shouldReturnMessageField() throws Exception {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        organizationRequest.setId(1);
        organizationRequest.setName("测试消息字段");

        mockMvc.perform(put("/system/organization/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * 测试删除组织返回消息字段
     */
    @Test
    void deleteOrganization_shouldReturnMessageField() throws Exception {
        mockMvc.perform(delete("/system/organization/delete")
                .param("id", "999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * 测试获取组织下用户返回数组
     */
    @Test
    void getOrganizationUsers_shouldReturnArray() throws Exception {
        mockMvc.perform(get("/system/organization/1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}
