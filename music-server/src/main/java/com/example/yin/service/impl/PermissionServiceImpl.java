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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final RolePermissionMapper rolePermissionMapper;

    public PermissionServiceImpl(RolePermissionMapper rolePermissionMapper) {
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public R addPermission(PermissionRequest permissionRequest) {
        if (permissionRequest == null
            || !StringUtils.hasText(permissionRequest.getCode())
            || !StringUtils.hasText(permissionRequest.getName())) {
            return R.error("权限代码和名称不能为空");
        }

        QueryWrapper<Permission> dupQw = new QueryWrapper<>();
        dupQw.eq("code", permissionRequest.getCode());
        Long dup = baseMapper.selectCount(dupQw);
        if (dup != null && dup > 0) {
            return R.warning("权限代码已存在");
        }

        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionRequest, permission);
        // 默认启用
        permission.setStatus(1);

        if (baseMapper.insert(permission) > 0) {
            return R.success("添加权限成功", permission);
        }
        return R.error("添加权限失败");
    }

    @Override
    public R updatePermission(PermissionRequest permissionRequest) {
        if (permissionRequest == null || permissionRequest.getId() == null) {
            return R.error("权限ID不能为空");
        }

        Permission permission = baseMapper.selectById(permissionRequest.getId());
        if (permission == null) {
            return R.error("权限不存在");
        }

        // code 变更才做重复校验
        if (StringUtils.hasText(permissionRequest.getCode())
            && !permissionRequest.getCode().equals(permission.getCode())) {
            QueryWrapper<Permission> dupQw = new QueryWrapper<>();
            dupQw.eq("code", permissionRequest.getCode());
            dupQw.ne("id", permissionRequest.getId());
            Long dup = baseMapper.selectCount(dupQw);
            if (dup != null && dup > 0) {
                return R.warning("权限代码已存在");
            }
            permission.setCode(permissionRequest.getCode());
        }

        if (StringUtils.hasText(permissionRequest.getName())) {
            permission.setName(permissionRequest.getName());
        }
        if (StringUtils.hasText(permissionRequest.getType())) {
            permission.setType(permissionRequest.getType());
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

        // 有子权限不允许删除
        QueryWrapper<Permission> childQw = new QueryWrapper<>();
        childQw.eq("parent_id", id);
        Long childCount = baseMapper.selectCount(childQw);
        if (childCount != null && childCount > 0) {
            return R.error("该权限存在子权限，不能删除");
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
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        List<Permission> all = baseMapper.selectList(queryWrapper);
        if (all == null || all.isEmpty()) {
            return R.success("查询成功", new ArrayList<>());
        }

        // 先清理 children，避免脏数据影响构建
        for (Permission p : all) {
            p.setChildren(null);
        }

        Map<Integer, Permission> byId = new HashMap<>();
        for (Permission p : all) {
            if (p != null && p.getId() != null) {
                byId.put(p.getId(), p);
            }
        }

        List<Permission> roots = new ArrayList<>();
        for (Permission p : all) {
            if (p == null) {
                continue;
            }
            Integer parentId = p.getParentId();
            if (parentId == null) {
                roots.add(p);
                continue;
            }
            Permission parent = byId.get(parentId);
            if (parent == null) {
                // 父节点不存在：跳过（测试期望树为空/不挂载到任何节点）
                continue;
            }
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            parent.getChildren().add(p);
        }

        sortAndNormalizeTree(roots);
        return R.success("查询成功", roots);
    }

    private void sortAndNormalizeTree(List<Permission> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        nodes.sort(Comparator.comparingInt(p -> p == null || p.getSort() == null ? 0 : p.getSort()));
        for (Permission p : nodes) {
            if (p == null) {
                continue;
            }
            List<Permission> children = p.getChildren();
            if (children == null || children.isEmpty()) {
                p.setChildren(null);
                continue;
            }
            sortAndNormalizeTree(children);
        }
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
