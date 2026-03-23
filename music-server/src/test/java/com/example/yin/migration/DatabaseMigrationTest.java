package com.example.yin.migration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DatabaseMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void consumer表应包含新字段() {
        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                     "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'consumer'";
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql);
        List<String> columnNames = columns.stream()
                .map(col -> (String) col.get("COLUMN_NAME"))
                .toList();

        assertTrue(columnNames.contains("org_id"), "consumer表应包含org_id字段");
        assertTrue(columnNames.contains("status"), "consumer表应包含status字段");
        assertTrue(columnNames.contains("last_login_time"), "consumer表应包含last_login_time字段");
    }

    @Test
    void organization表应存在() {
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                     "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'organization'";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        assertFalse(result.isEmpty(), "organization表应该存在");
    }

    @Test
    void role表应存在() {
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                     "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'role'";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        assertFalse(result.isEmpty(), "role表应该存在");
    }

    @Test
    void permission表应存在() {
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                     "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'permission'";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        assertFalse(result.isEmpty(), "permission表应该存在");
    }

    @Test
    void 默认管理员角色应存在() {
        String sql = "SELECT COUNT(*) FROM role WHERE code = 'SUPER_ADMIN'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        assertTrue(count != null && count > 0, "默认超级管理员角色应该存在");
    }
}