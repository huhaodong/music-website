package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.Role;
import com.example.yin.model.request.RoleRequest;

import java.util.List;

public interface RoleService extends IService<Role> {

    R addRole(RoleRequest roleRequest);

    R updateRole(RoleRequest roleRequest);

    R deleteRole(Integer id);

    R getRoleById(Integer id);

    R getAllRoles();

    R getRolesByStatus(Integer status);
}