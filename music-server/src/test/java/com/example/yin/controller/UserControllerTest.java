package com.example.yin.controller;

import com.example.yin.model.request.ConsumerRequest;
import com.example.yin.config.TestMinioConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController 集成测试
 * 测试用户管理相关的 REST API
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestMinioConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== GET /system/user/list - 获取用户列表 ====================

    /**
     * 测试获取用户列表
     */
    @Test
    void getUserList_shouldReturnUserList() throws Exception {
        mockMvc.perform(get("/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").exists());
    }

    /**
     * 测试获取用户列表返回数据结构
     */
    @Test
    void getUserList_shouldReturnDataStructure() throws Exception {
        mockMvc.perform(get("/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试获取用户列表返回 JSON 结构完整性
     */
    @Test
    void getUserList_shouldReturnCompleteJsonStructure() throws Exception {
        mockMvc.perform(get("/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.success").exists());
    }

    // ==================== POST /system/user/add - 添加用户 ====================

    /**
     * 测试添加用户 - 完整信息
     */
    @Test
    void addUser_withCompleteInfo_shouldReturnSuccess() throws Exception {
        ConsumerRequest request = new ConsumerRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setNickname("测试用户");
        request.setSex((byte) 1);
        request.setPhoneNum("13800138000");
        request.setEmail("test@example.com");
        request.setBirth(new Date());
        request.setIntroduction("这是一个测试用户");
        request.setLocation("北京");

        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists());
    }

    /**
     * 测试添加用户 - 仅用户名和密码
     */
    @Test
    void addUser_withOnlyUsernameAndPassword_shouldReturnSuccess() throws Exception {
        ConsumerRequest request = new ConsumerRequest();
        request.setUsername("minimaluser");
        request.setPassword("password123");

        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * 测试添加用户 - 空用户名
     */
    @Test
    void addUser_withEmptyUsername_shouldReturnSuccess() throws Exception {
        ConsumerRequest request = new ConsumerRequest();
        request.setUsername("");
        request.setPassword("password123");

        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * 测试添加用户 - 空密码
     */
    @Test
    void addUser_withEmptyPassword_shouldReturnSuccess() throws Exception {
        ConsumerRequest request = new ConsumerRequest();
        request.setUsername("testuser2");
        request.setPassword("");

        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * 测试添加用户 - 空请求体
     */
    @Test
    void addUser_withEmptyBody_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    /**
     * 测试添加用户 - 邮箱格式
     */
    @Test
    void addUser_withEmail_shouldReturnSuccess() throws Exception {
        ConsumerRequest request = new ConsumerRequest();
        request.setUsername("emailuser");
        request.setPassword("password123");
        request.setEmail("user@domain.com");

        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * 测试添加用户 - 不同性别值
     */
    @Test
    void addUser_withDifferentSexValues_shouldReturnSuccess() throws Exception {
        ConsumerRequest request = new ConsumerRequest();
        request.setUsername("sexuser");
        request.setPassword("password123");
        request.setSex((byte) 0);

        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ==================== PUT /system/user/update - 更新用户 ====================

    /**
     * 测试更新用户 - 完整信息
     */
    @Test
    void updateUser_withCompleteInfo_shouldReturnSuccess() throws Exception {
        ConsumerRequest request = new ConsumerRequest();
        request.setId(1);
        request.setUsername("updateduser");
        request.setNickname("更新后的用户");
        request.setSex((byte) 1);
        request.setPhoneNum("13900139000");
        request.setEmail("updated@example.com");
        request.setIntroduction("更新后的简介");
        request.setLocation("上海");

        mockMvc.perform(post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists());
    }

    /**
     * 测试更新用户 - 仅更新昵称
     */
    @Test
    void updateUser_onlyNickname_shouldReturnSuccess() throws Exception {
        ConsumerRequest request = new ConsumerRequest();
        request.setId(1);
        request.setNickname("仅更新昵称");

        mockMvc.perform(post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * 测试更新用户 - 仅更新邮箱
     */
    @Test
    void updateUser_onlyEmail_shouldReturnSuccess() throws Exception {
        ConsumerRequest request = new ConsumerRequest();
        request.setId(1);
        request.setEmail("newemail@example.com");

        mockMvc.perform(post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * 测试更新用户 - 仅更新简介
     */
    @Test
    void updateUser_onlyIntroduction_shouldReturnSuccess() throws Exception {
        ConsumerRequest request = new ConsumerRequest();
        request.setId(1);
        request.setIntroduction("这是新简介");

        mockMvc.perform(post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * 测试更新用户 - 不存在的用户ID
     */
    @Test
    void updateUser_withNonExistentId_shouldReturnAppropriateResponse() throws Exception {
        ConsumerRequest request = new ConsumerRequest();
        request.setId(999999);
        request.setNickname("不存在的用户");

        mockMvc.perform(post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * 测试更新用户 - 缺少ID
     */
    @Test
    void updateUser_withoutId_shouldReturnSuccess() throws Exception {
        ConsumerRequest request = new ConsumerRequest();
        request.setNickname("没有ID的更新");

        mockMvc.perform(post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * 测试更新用户 - 空请求体
     */
    @Test
    void updateUser_withEmptyBody_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    /**
     * 测试更新用户返回消息字段
     */
    @Test
    void updateUser_shouldReturnMessageField() throws Exception {
        ConsumerRequest request = new ConsumerRequest();
        request.setId(1);
        request.setNickname("测试消息字段");

        mockMvc.perform(post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    // ==================== DELETE /system/user/delete - 删除用户 ====================

    /**
     * 测试删除用户
     */
    @Test
    void deleteUser_shouldReturnSuccessResult() throws Exception {
        mockMvc.perform(get("/user/delete")
                .param("id", "999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试删除不存在的用户
     */
    @Test
    void deleteUser_withNonExistentId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(get("/user/delete")
                .param("id", "999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试删除用户 - 缺少id参数
     */
    @Test
    void deleteUser_withoutId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/user/delete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试删除用户 - id为0
     */
    @Test
    void deleteUser_withZeroId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(get("/user/delete")
                .param("id", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试删除用户 - 负数ID
     */
    @Test
    void deleteUser_withNegativeId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(get("/user/delete")
                .param("id", "-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试删除用户返回消息字段
     */
    @Test
    void deleteUser_shouldReturnMessageField() throws Exception {
        mockMvc.perform(get("/user/delete")
                .param("id", "999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    // ==================== POST /system/user/batch - 批量操作 ====================

    /**
     * 测试批量删除用户
     */
    @Test
    void batchDeleteUsers_shouldReturnSuccessResult() throws Exception {
        mockMvc.perform(post("/user/batchDelete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(1, 2, 3))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试批量删除用户 - 空数组
     */
    @Test
    void batchDeleteUsers_withEmptyArray_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/user/batchDelete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
                .andExpect(status().isOk());
    }

    /**
     * 测试批量删除用户 - 单个ID
     */
    @Test
    void batchDeleteUsers_withSingleId_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/user/batchDelete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(1))))
                .andExpect(status().isOk());
    }

    /**
     * 测试批量删除用户 - 包含不存在的ID
     */
    @Test
    void batchDeleteUsers_withNonExistentIds_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/user/batchDelete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(999999, 999998))))
                .andExpect(status().isOk());
    }

    /**
     * 测试批量删除用户 - 包含负数ID
     */
    @Test
    void batchDeleteUsers_withNegativeIds_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/user/batchDelete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(-1, -2))))
                .andExpect(status().isOk());
    }

    /**
     * 测试批量删除用户返回消息字段
     */
    @Test
    void batchDeleteUsers_shouldReturnMessageField() throws Exception {
        mockMvc.perform(post("/user/batchDelete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(1, 2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * 测试批量删除用户返回 JSON 结构完整性
     */
    @Test
    void batchDeleteUsers_shouldReturnCompleteJsonStructure() throws Exception {
        mockMvc.perform(post("/user/batchDelete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList(1, 2, 3))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.success").exists());
    }

    // ==================== 其他用户相关接口测试 ====================

    /**
     * 测试获取用户详情
     */
    @Test
    void getUserDetail_shouldReturnUserDetail() throws Exception {
        mockMvc.perform(get("/user/detail")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试获取用户详情 - 不存在的ID
     */
    @Test
    void getUserDetail_withNonExistentId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(get("/user/detail")
                .param("id", "999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试获取用户详情 - 缺少id参数
     */
    @Test
    void getUserDetail_withoutId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/user/detail")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试获取用户详情 - id为0
     */
    @Test
    void getUserDetail_withZeroId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(get("/user/detail")
                .param("id", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试获取用户详情 - 负数ID
     */
    @Test
    void getUserDetail_withNegativeId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(get("/user/detail")
                .param("id", "-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
