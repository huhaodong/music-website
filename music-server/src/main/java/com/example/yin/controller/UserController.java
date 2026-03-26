package com.example.yin.controller;

import com.example.yin.annotation.RequirePermission;
import com.example.yin.common.R;
import com.example.yin.model.domain.ResetPasswordRequest;
import com.example.yin.model.request.ConsumerRequest;
import com.example.yin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    @RequirePermission(codes = {"system:user", "user:add"})
    public R addUser(@RequestBody ConsumerRequest registryRequest) {
        return userService.addUser(registryRequest);
    }

    @GetMapping("/list")
    @RequirePermission(codes = {"system:user", "user:list"})
    public R allUser() {
        return userService.getAllUsers();
    }

    @GetMapping("/detail")
    @RequirePermission(codes = {"system:user", "user:detail"})
    public R userOfId(@RequestParam int id) {
        return userService.getUserById(id);
    }

    @GetMapping("/delete")
    @RequirePermission(codes = {"system:user", "user:delete"})
    public R deleteUser(@RequestParam int id) {
        return userService.deleteUser(id);
    }

    @PostMapping("/update")
    @RequirePermission(codes = {"system:user", "user:update"})
    public R updateUserMsg(@RequestBody ConsumerRequest updateRequest) {
        return userService.updateUserMsg(updateRequest);
    }

    @PostMapping("/updatePassword")
    @RequirePermission(codes = {"system:user", "user:updatePassword"})
    public R updatePassword(@RequestBody ConsumerRequest updatePasswordRequest) {
        return userService.updatePassword(updatePasswordRequest);
    }

    @PostMapping("/avatar/update")
    @RequirePermission(codes = {"system:user", "user:updateAvatar"})
    public R updateUserPic(@RequestParam("file") MultipartFile avatorFile, @RequestParam("id") int id) {
        ConsumerRequest request = new ConsumerRequest();
        request.setId(id);
        request.setAvatarFile(avatorFile);
        return userService.updateUserAvatar(request);
    }

    @PostMapping("/batchDelete")
    @RequirePermission(codes = {"system:user", "user:batchDelete"})
    public R batchDeleteUsers(@RequestBody List<Integer> ids) {
        return userService.batchDeleteUsers(ids);
    }

    @PostMapping("/batchAssignRoles")
    @RequirePermission(codes = {"system:user", "user:assignRoles"})
    public R batchAssignRoles(@RequestParam Integer[] userIds,
                               @RequestParam String userType,
                               @RequestBody List<Integer> roleIds) {
        return userService.batchAssignRoles(userIds, userType, roleIds);
    }
}