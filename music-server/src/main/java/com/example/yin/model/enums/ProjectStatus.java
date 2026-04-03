package com.example.yin.model.enums;

/**
 * 项目状态
 * - DRAFT: 未发行
 * - RELEASED: 已发行
 * - HOLD: 暂缓发行
 */
public enum ProjectStatus {
    DRAFT,
    RELEASED,
    HOLD;

    public static boolean isValid(String status) {
        if (status == null) {
            return false;
        }
        try {
            ProjectStatus.valueOf(status);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}

