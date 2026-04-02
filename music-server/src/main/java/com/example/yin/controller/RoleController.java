package com.example.yin.controller;

import com.example.yin.annotation.RequirePermission;
import com.example.yin.common.R;
import com.example.yin.model.request.RoleRequest;
import com.example.yin.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/add")
    @RequirePermission({"system:role:add", "role:add"})
    public R addRole(@RequestBody RoleRequest roleRequest) {
        return roleService.addRole(roleRequest);
    }

    @PutMapping("/update")
    @RequirePermission({"system:role:update", "role:edit"})
    public R updateRole(@RequestBody RoleRequest roleRequest) {
        return roleService.updateRole(roleRequest);
    }

    @DeleteMapping("/delete")
    @RequirePermission({"system:role:delete", "role:delete"})
    public R deleteRole(@RequestParam Integer id) {
        return roleService.deleteRole(id);
    }

    @GetMapping("/detail")
    @RequirePermission({"system:role:query", "role:detail"})
    public R getRoleById(@RequestParam Integer id) {
        return roleService.getRoleById(id);
    }

    @GetMapping("/list")
    @RequirePermission({"system:role:query", "role:list"})
    public R getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/listByStatus")
    public R getRolesByStatus(@RequestParam(required = false) Integer status) {
        return roleService.getRolesByStatus(status);
    }
}
