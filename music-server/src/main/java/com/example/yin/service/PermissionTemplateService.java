package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.Permission;

import java.util.List;

public interface PermissionTemplateService {

    R getTemplateByRole(String roleCode);

    R getAllTemplates();

    R applyTemplateToRole(String templateName, Integer roleId);
}