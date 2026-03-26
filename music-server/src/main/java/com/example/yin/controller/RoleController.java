package com.example.yin.controller;

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
    public R addRole(@RequestBody RoleRequest roleRequest) {
        return roleService.addRole(roleRequest);
    }

    @PutMapping("/update")
    public R updateRole(@RequestBody RoleRequest roleRequest) {
        return roleService.updateRole(roleRequest);
    }

    @DeleteMapping("/delete")
    public R deleteRole(@RequestParam Integer id) {
        return roleService.deleteRole(id);
    }

    @GetMapping("/detail")
    public R getRoleById(@RequestParam Integer id) {
        return roleService.getRoleById(id);
    }

    @GetMapping("/list")
    public R getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/listByStatus")
    public R getRolesByStatus(@RequestParam(required = false) Integer status) {
        return roleService.getRolesByStatus(status);
    }
}