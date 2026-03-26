package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.common.R;
import com.example.yin.mapper.PermissionMapper;
import com.example.yin.mapper.RoleMapper;
import com.example.yin.mapper.RolePermissionMapper;
import com.example.yin.model.domain.Permission;
import com.example.yin.model.domain.Role;
import com.example.yin.model.domain.RolePermission;
import com.example.yin.service.PermissionTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PermissionTemplateServiceImpl implements PermissionTemplateService {

    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    public PermissionTemplateServiceImpl(PermissionMapper permissionMapper,
                                         RoleMapper roleMapper,
                                         RolePermissionMapper rolePermissionMapper) {
        this.permissionMapper = permissionMapper;
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    private static final Map<String, List<String>> PERMISSION_TEMPLATES = new HashMap<>();

    static {
        PERMISSION_TEMPLATES.put("SUPER_ADMIN", new ArrayList<>());
        PERMISSION_TEMPLATES.put("USER", new ArrayList<String>() {{
            add("song:list");
            add("singer:list");
            add("songlist:list");
            add("comment:list");
        }});
        PERMISSION_TEMPLATES.put("SINGER", new ArrayList<String>() {{
            add("song:list");
            add("song:add");
            add("song:update");
            add("singer:list");
        }});
        PERMISSION_TEMPLATES.put("ADMIN_BASIC", new ArrayList<String>() {{
            add("system:user");
            add("system:role");
            add("song:list");
            add("singer:list");
            add("songlist:list");
        }});
    }

    @Override
    public R getTemplateByRole(String roleCode) {
        List<String> templatePermissions = PERMISSION_TEMPLATES.get(roleCode);
        if (templatePermissions == null) {
            return R.error("模板不存在");
        }

        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("code", templatePermissions);
        List<Permission> permissions = permissionMapper.selectList(queryWrapper);

        return R.success("查询成功", permissions);
    }

    @Override
    public R getAllTemplates() {
        List<Map<String, Object>> templates = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : PERMISSION_TEMPLATES.entrySet()) {
            Map<String, Object> template = new HashMap<>();
            template.put("name", entry.getKey());
            template.put("permissions", entry.getValue());
            template.put("count", entry.getValue().size());
            templates.add(template);
        }
        return R.success("查询成功", templates);
    }

    @Override
    @Transactional
    public R applyTemplateToRole(String templateName, Integer roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            return R.error("角色不存在");
        }

        List<String> templatePermissions = PERMISSION_TEMPLATES.get(templateName);
        if (templatePermissions == null) {
            return R.error("模板不存在");
        }

        QueryWrapper<RolePermission> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("role_id", roleId);
        rolePermissionMapper.delete(deleteWrapper);

        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("code", templatePermissions);
        List<Permission> permissions = permissionMapper.selectList(queryWrapper);

        for (Permission permission : permissions) {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permission.getId());
            rolePermissionMapper.insert(rp);
        }

        return R.success("应用模板成功");
    }
}