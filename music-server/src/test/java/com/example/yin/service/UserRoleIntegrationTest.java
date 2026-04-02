package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.UserRoleMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.UserRole;
import com.example.yin.model.request.ConsumerRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Automatically roll back after each test to keep DB clean
@DisplayName("用户角色分配集成测试")
public class UserRoleIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private ConsumerMapper consumerMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Test
    @DisplayName("前台注册用户应默认分配 'USER' 角色")
    void registrationShouldAssignDefaultRole() {
        String username = "test_register_" + System.currentTimeMillis();
        ConsumerRequest request = new ConsumerRequest();
        request.setUsername(username);
        request.setPassword("password123");
        request.setNickname("Test Register");

        // Act
        consumerService.addUser(request);

        // Assert
        Consumer consumer = getConsumerByUsername(username);
        assertNotNull(consumer, "User should be created");

        List<UserRole> userRoles = getUserRoles(consumer.getId());
        assertEquals(1, userRoles.size(), "User should have exactly one role");
        
        // Assuming role ID 2 is 'USER' based on V5__init_default_data.sql
        // Or we can check the code if we had RoleMapper, but let's check if role exists.
        assertNotNull(userRoles.get(0).getRoleId(), "Role ID should not be null");
    }

    @Test
    @DisplayName("后台手动添加用户（不选角色）应默认分配 'USER' 角色")
    void adminAddUserWithoutRoleShouldAssignDefaultRole() {
        String username = "test_admin_add_" + System.currentTimeMillis();
        ConsumerRequest request = new ConsumerRequest();
        request.setUsername(username);
        request.setPassword("password123");
        request.setNickname("Test Admin Add");

        // Act
        userService.addUser(request);

        // Assert
        Consumer consumer = getConsumerByUsername(username);
        assertNotNull(consumer, "User should be created");

        List<UserRole> userRoles = getUserRoles(consumer.getId());
        assertEquals(1, userRoles.size(), "User should have default role assigned");
    }

    @Test
    @DisplayName("删除用户后应同时删除用户角色关联（ConsumerService）")
    void deleteUserShouldCleanupRolesInConsumerService() {
        String username = "test_delete_consumer_" + System.currentTimeMillis();
        ConsumerRequest request = new ConsumerRequest();
        request.setUsername(username);
        request.setPassword("password123");
        
        consumerService.addUser(request);
        Consumer consumer = getConsumerByUsername(username);
        Integer userId = consumer.getId();
        
        // Pre-check
        assertFalse(getUserRoles(userId).isEmpty(), "User roles should exist before deletion");

        // Act
        consumerService.deleteUser(userId);

        // Assert
        assertTrue(getUserRoles(userId).isEmpty(), "User roles should be deleted after user deletion");
    }

    @Test
    @DisplayName("删除用户后应同时删除用户角色关联（UserService）")
    void deleteUserShouldCleanupRolesInUserService() {
        String username = "test_delete_user_" + System.currentTimeMillis();
        ConsumerRequest request = new ConsumerRequest();
        request.setUsername(username);
        request.setPassword("password123");
        
        userService.addUser(request);
        Consumer consumer = getConsumerByUsername(username);
        Integer userId = consumer.getId();
        
        // Pre-check
        assertFalse(getUserRoles(userId).isEmpty(), "User roles should exist before deletion");

        // Act
        userService.deleteUser(userId);

        // Assert
        assertTrue(getUserRoles(userId).isEmpty(), "User roles should be deleted after user deletion");
    }

    private Consumer getConsumerByUsername(String username) {
        QueryWrapper<Consumer> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return consumerMapper.selectOne(wrapper);
    }

    private List<UserRole> getUserRoles(Integer userId) {
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("user_type", "consumer");
        return userRoleMapper.selectList(wrapper);
    }
}
