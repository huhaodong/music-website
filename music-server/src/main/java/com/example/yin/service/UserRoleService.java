package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.UserRole;
import com.example.yin.model.request.UserRoleRequest;

import java.util.List;

public interface UserRoleService extends IService<UserRole> {

    R assignRolesToUser(UserRoleRequest userRoleRequest);

    R removeRolesFromUser(Integer userId, String userType);

    R getUserRoles(Integer userId, String userType);

    R getUsersByRoleId(Integer roleId);
}