package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.mapper.UserRoleMapper;
import com.example.yin.model.domain.UserRole;
import com.example.yin.model.request.UserRoleRequest;
import com.example.yin.service.UserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    @Transactional
    public R assignRolesToUser(UserRoleRequest userRoleRequest) {
        if (userRoleRequest.getUserId() == null || userRoleRequest.getUserType() == null) {
            return R.error("用户ID和用户类型不能为空");
        }

        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userRoleRequest.getUserId());
        queryWrapper.eq("user_type", userRoleRequest.getUserType());
        baseMapper.delete(queryWrapper);

        if (userRoleRequest.getRoleIds() != null && !userRoleRequest.getRoleIds().isEmpty()) {
            List<UserRole> userRoles = userRoleRequest.getRoleIds().stream()
                    .map(roleId -> {
                        UserRole ur = new UserRole();
                        ur.setUserId(userRoleRequest.getUserId());
                        ur.setUserType(userRoleRequest.getUserType());
                        ur.setRoleId(roleId);
                        return ur;
                    })
                    .collect(Collectors.toList());

            for (UserRole ur : userRoles) {
                baseMapper.insert(ur);
            }
        }

        return R.success("分配角色成功");
    }

    @Override
    @Transactional
    public R removeRolesFromUser(Integer userId, String userType) {
        if (userId == null || userType == null) {
            return R.error("用户ID和用户类型不能为空");
        }

        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("user_type", userType);

        int deleted = baseMapper.delete(queryWrapper);
        return R.success("移除角色成功", deleted);
    }

    @Override
    public R getUserRoles(Integer userId, String userType) {
        if (userId == null || userType == null) {
            return R.error("用户ID和用户类型不能为空");
        }

        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("user_type", userType);
        List<UserRole> userRoles = baseMapper.selectList(queryWrapper);

        List<Integer> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        return R.success("查询成功", roleIds);
    }

    @Override
    public R getUsersByRoleId(Integer roleId) {
        if (roleId == null) {
            return R.error("角色ID不能为空");
        }

        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        List<UserRole> userRoles = baseMapper.selectList(queryWrapper);

        return R.success("查询成功", userRoles);
    }
}