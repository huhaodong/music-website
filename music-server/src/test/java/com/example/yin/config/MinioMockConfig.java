package com.example.yin.config;

import io.minio.MinioClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * 测试环境默认注入 MinioClient Mock，避免任何测试用例发起真实网络请求。
 * - 单测/集成测试可按需通过 @Import(TestMinioConfig.class) 或 @MockBean 覆盖该 Bean
 */
@Configuration
@Profile("test")
public class MinioMockConfig {

    @Bean
    @Primary
    public MinioClient minioClient() {
        return Mockito.mock(MinioClient.class);
    }
}

