package com.example.yin.service;

import com.example.yin.common.R;

public interface ProjectStatusService {

    /**
     * 按状态机规则变更项目状态，并记录状态变更日志
     */
    R changeStatus(Integer projectId, String toStatus, String remark);
}

