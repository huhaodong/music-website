package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.model.request.UserRoleRequest;
import com.example.yin.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/userRole")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @PostMapping("/assign")
    public R assignRolesToUser(@RequestBody UserRoleRequest userRoleRequest) {
        return userRoleService.assignRolesToUser(userRoleRequest);
    }

    @DeleteMapping("/remove")
    public R removeRolesFromUser(@RequestParam Integer userId, @RequestParam String userType) {
        return userRoleService.removeRolesFromUser(userId, userType);
    }

    @GetMapping("/list")
    public R getUserRoles(@RequestParam Integer userId, @RequestParam String userType) {
        return userRoleService.getUserRoles(userId, userType);
    }

    @GetMapping("/usersByRole")
    public R getUsersByRoleId(@RequestParam Integer roleId) {
        return userRoleService.getUsersByRoleId(roleId);
    }
}