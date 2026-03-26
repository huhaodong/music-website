package com.example.yin.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 * 用于标注需要特定权限才能访问的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限代码，多个权限用逗号分隔
     * 用户需要拥有其中任一权限即可访问
     */
    String[] value() default {};

    /**
     * 权限代码数组
     */
    String[] codes() default {};

    /**
     * 是否需要所有权限
     * 默认为false，表示拥有任一权限即可
     * 当为true时，用户需要拥有所有指定的权限
     */
    boolean requireAll() default false;
}