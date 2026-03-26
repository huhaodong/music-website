package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.Permission;
import com.example.yin.model.request.PermissionRequest;

import java.util.List;

public interface PermissionService extends IService<Permission> {

    R addPermission(PermissionRequest permissionRequest);

    R updatePermission(PermissionRequest permissionRequest);

    R deletePermission(Integer id);

    R getPermissionById(Integer id);

    R getAllPermissions();

    R getPermissionTree();

    R getPermissionsByRoleId(Integer roleId);

    R assignPermissionsToRole(Integer roleId, List<Integer> permissionIds);
}