package com.example.yin.migration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DatabaseMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void consumer表应包含新字段() {
        // H2 下没有 DATABASE()，且 INFORMATION_SCHEMA 中表/列名通常为大写
        // 这里不限定 schema，统一用 UPPER 兼容 MySQL/H2
        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                     "WHERE UPPER(TABLE_NAME) = 'CONSUMER'";
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql);
        List<String> columnNames = columns.stream()
                .map(col -> (String) col.get("COLUMN_NAME"))
                .collect(Collectors.toList());

        List<String> upperNames = columnNames.stream().map(String::toUpperCase).collect(Collectors.toList());
        assertTrue(upperNames.contains("ORG_ID"), "consumer表应包含org_id字段");
        assertTrue(upperNames.contains("STATUS"), "consumer表应包含status字段");
        assertTrue(upperNames.contains("LAST_LOGIN_TIME"), "consumer表应包含last_login_time字段");
    }

    @Test
    void organization表应存在() {
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                     "WHERE UPPER(TABLE_NAME) = 'ORGANIZATION'";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        assertFalse(result.isEmpty(), "organization表应该存在");
    }

    @Test
    void organization表应包含code字段() {
        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                     "WHERE UPPER(TABLE_NAME) = 'ORGANIZATION'";
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql);
        List<String> columnNames = columns.stream()
                .map(col -> (String) col.get("COLUMN_NAME"))
                .collect(Collectors.toList());

        List<String> upperNames = columnNames.stream().map(String::toUpperCase).collect(Collectors.toList());
        assertTrue(upperNames.contains("CODE"), "organization表应包含code字段");
    }

    @Test
    void role表应存在() {
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                     "WHERE UPPER(TABLE_NAME) = 'ROLE'";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        assertFalse(result.isEmpty(), "role表应该存在");
    }

    @Test
    void permission表应存在() {
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                     "WHERE UPPER(TABLE_NAME) = 'PERMISSION'";
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
