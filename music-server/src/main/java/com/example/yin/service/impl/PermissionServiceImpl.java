package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.mapper.PermissionMapper;
import com.example.yin.mapper.RolePermissionMapper;
import com.example.yin.model.domain.Permission;
import com.example.yin.model.domain.RolePermission;
import com.example.yin.model.request.PermissionRequest;
import com.example.yin.service.PermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final RolePermissionMapper rolePermissionMapper;

    public PermissionServiceImpl(RolePermissionMapper rolePermissionMapper) {
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public R addPermission(PermissionRequest permissionRequest) {
        if (permissionRequest.getName() == null || permissionRequest.getName().trim().isEmpty()) {
            return R.error("权限名称不能为空");
        }

        String code = generateNextCode();

        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionRequest, permission);
        permission.setCode(code);
        permission.setStatus(1);

        if (baseMapper.insert(permission) > 0) {
            return R.success("添加权限成功", permission);
        }
        return R.error("添加权限失败");
    }

    private String generateNextCode() {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("code", "PERMISSION-");
        queryWrapper.orderByDesc("id");
        queryWrapper.last("LIMIT 1");
        Permission lastPermission = baseMapper.selectOne(queryWrapper);

        int nextNum = 1;
        if (lastPermission != null && lastPermission.getCode() != null) {
            try {
                String numStr = lastPermission.getCode().replace("PERMISSION-", "");
                nextNum = Integer.parseInt(numStr) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("PERMISSION-%03d", nextNum);
    }

    @Override
    public R updatePermission(PermissionRequest permissionRequest) {
        if (permissionRequest.getId() == null) {
            return R.error("权限ID不能为空");
        }

        Permission permission = baseMapper.selectById(permissionRequest.getId());
        if (permission == null) {
            return R.error("权限不存在");
        }

        if (StringUtils.hasText(permissionRequest.getName())) {
            permission.setName(permissionRequest.getName());
        }
        if (StringUtils.hasText(permissionRequest.getUrl())) {
            permission.setUrl(permissionRequest.getUrl());
        }
        if (StringUtils.hasText(permissionRequest.getMethod())) {
            permission.setMethod(permissionRequest.getMethod());
        }
        if (permissionRequest.getSort() != null) {
            permission.setSort(permissionRequest.getSort());
        }
        if (permissionRequest.getStatus() != null) {
            permission.setStatus(permissionRequest.getStatus());
        }

        if (baseMapper.updateById(permission) > 0) {
            return R.success("更新权限成功", permission);
        }
        return R.error("更新权限失败");
    }

    @Override
    @Transactional
    public R deletePermission(Integer id) {
        if (id == null) {
            return R.error("权限ID不能为空");
        }

        Permission permission = baseMapper.selectById(id);
        if (permission == null) {
            return R.error("权限不存在");
        }

        QueryWrapper<RolePermission> rolePermissionQueryWrapper = new QueryWrapper<>();
        rolePermissionQueryWrapper.eq("permission_id", id);
        rolePermissionMapper.delete(rolePermissionQueryWrapper);

        if (baseMapper.deleteById(id) > 0) {
            return R.success("删除权限成功");
        }
        return R.error("删除权限失败");
    }

    @Override
    public R getPermissionById(Integer id) {
        if (id == null) {
            return R.error("权限ID不能为空");
        }

        Permission permission = baseMapper.selectById(id);
        if (permission == null) {
            return R.error("权限不存在");
        }
        return R.success("查询成功", permission);
    }

    @Override
    public R getAllPermissions() {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        return R.success("查询成功", baseMapper.selectList(queryWrapper));
    }

    @Override
    public R getPermissionTree() {
        return getAllPermissions();
    }

    @Override
    public R getPermissionsByRoleId(Integer roleId) {
        if (roleId == null) {
            return R.error("角色ID不能为空");
        }

        QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(queryWrapper);

        List<Integer> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());

        if (permissionIds.isEmpty()) {
            return R.success("查询成功", new ArrayList<>());
        }

        List<Permission> permissions = baseMapper.selectBatchIds(permissionIds);
        return R.success("查询成功", permissions);
    }

    @Override
    @Transactional
    public R assignPermissionsToRole(Integer roleId, List<Integer> permissionIds) {
        if (roleId == null) {
            return R.error("角色ID不能为空");
        }

        QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        rolePermissionMapper.delete(queryWrapper);

        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<RolePermission> rolePermissions = permissionIds.stream()
                    .map(permissionId -> {
                        RolePermission rp = new RolePermission();
                        rp.setRoleId(roleId);
                        rp.setPermissionId(permissionId);
                        return rp;
                    })
                    .collect(Collectors.toList());

            for (RolePermission rp : rolePermissions) {
                rolePermissionMapper.insert(rp);
            }
        }

        return R.success("分配权限成功");
    }
}
