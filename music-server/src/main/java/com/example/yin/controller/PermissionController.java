package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.model.request.PermissionRequest;
import com.example.yin.model.request.RolePermissionRequest;
import com.example.yin.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/add")
    public R addPermission(@RequestBody PermissionRequest permissionRequest) {
        return permissionService.addPermission(permissionRequest);
    }

    @PutMapping("/update")
    public R updatePermission(@RequestBody PermissionRequest permissionRequest) {
        return permissionService.updatePermission(permissionRequest);
    }

    @DeleteMapping("/delete")
    public R deletePermission(@RequestParam Integer id) {
        return permissionService.deletePermission(id);
    }

    @GetMapping("/detail")
    public R getPermissionById(@RequestParam Integer id) {
        return permissionService.getPermissionById(id);
    }

    @GetMapping("/list")
    public R getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    @GetMapping("/tree")
    public R getPermissionTree() {
        return permissionService.getPermissionTree();
    }

    @GetMapping("/listByRole")
    public R getPermissionsByRoleId(@RequestParam Integer roleId) {
        return permissionService.getPermissionsByRoleId(roleId);
    }

    @PostMapping("/assign")
    public R assignPermissionsToRole(@RequestBody RolePermissionRequest request) {
        return permissionService.assignPermissionsToRole(request.getRoleId(), request.getPermissionIds());
    }
}