package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.mapper.RoleMapper;
import com.example.yin.model.domain.Role;
import com.example.yin.model.request.RoleRequest;
import com.example.yin.service.RoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public R addRole(RoleRequest roleRequest) {
        if (!StringUtils.hasText(roleRequest.getName())) {
            return R.error("角色名称不能为空");
        }

        String code = generateNextCode();

        Role role = new Role();
        BeanUtils.copyProperties(roleRequest, role);
        role.setCode(code);
        role.setStatus(1);

        if (baseMapper.insert(role) > 0) {
            return R.success("添加角色成功", role);
        }
        return R.error("添加角色失败");
    }

    private String generateNextCode() {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("code", "ROLE-");
        queryWrapper.orderByDesc("id");
        queryWrapper.last("LIMIT 1");
        Role lastRole = baseMapper.selectOne(queryWrapper);

        int nextNum = 1;
        if (lastRole != null && lastRole.getCode() != null) {
            try {
                String numStr = lastRole.getCode().replace("ROLE-", "");
                nextNum = Integer.parseInt(numStr) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("ROLE-%03d", nextNum);
    }

    @Override
    public R updateRole(RoleRequest roleRequest) {
        if (roleRequest.getId() == null) {
            return R.error("角色ID不能为空");
        }

        Role role = baseMapper.selectById(roleRequest.getId());
        if (role == null) {
            return R.error("角色不存在");
        }

        if (StringUtils.hasText(roleRequest.getName())) {
            role.setName(roleRequest.getName());
        }
        if (roleRequest.getStatus() != null) {
            role.setStatus(roleRequest.getStatus());
        }
        if (StringUtils.hasText(roleRequest.getDescription())) {
            role.setDescription(roleRequest.getDescription());
        }

        if (baseMapper.updateById(role) > 0) {
            return R.success("更新角色成功", role);
        }
        return R.error("更新角色失败");
    }

    @Override
    @Transactional
    public R deleteRole(Integer id) {
        if (id == null) {
            return R.error("角色ID不能为空");
        }

        Role role = baseMapper.selectById(id);
        if (role == null) {
            return R.error("角色不存在");
        }

        if ("SUPER_ADMIN".equals(role.getCode())) {
            return R.error("不能删除超级管理员角色");
        }

        if (baseMapper.deleteById(id) > 0) {
            return R.success("删除角色成功");
        }
        return R.error("删除角色失败");
    }

    @Override
    public R getRoleById(Integer id) {
        if (id == null) {
            return R.error("角色ID不能为空");
        }

        Role role = baseMapper.selectById(id);
        if (role == null) {
            return R.error("角色不存在");
        }
        return R.success("查询成功", role);
    }

    @Override
    public R getAllRoles() {
        return R.success("查询成功", baseMapper.selectList(null));
    }

    @Override
    public R getRolesByStatus(Integer status) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        return R.success("查询成功", baseMapper.selectList(queryWrapper));
    }
}