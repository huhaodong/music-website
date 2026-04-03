package com.example.yin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * 仅在存在 DataSource 时启用 Mapper 扫描。
 * - 避免 WebMvcTest 等 slice 测试场景下因缺少 SqlSessionFactory 导致启动失败
 */
@Configuration
@ConditionalOnProperty(name = "spring.datasource.url")
@MapperScan("com.example.yin.mapper")
public class MybatisMapperScanConfig {
}
