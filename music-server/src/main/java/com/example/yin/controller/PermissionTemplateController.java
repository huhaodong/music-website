package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.service.PermissionTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/permissionTemplate")
public class PermissionTemplateController {

    @Autowired
    private PermissionTemplateService permissionTemplateService;

    @GetMapping("/list")
    public R getAllTemplates() {
        return permissionTemplateService.getAllTemplates();
    }

    @GetMapping("/getByRole")
    public R getTemplateByRole(@RequestParam String roleCode) {
        return permissionTemplateService.getTemplateByRole(roleCode);
    }

    @PostMapping("/apply")
    public R applyTemplateToRole(@RequestParam String templateName, @RequestParam Integer roleId) {
        return permissionTemplateService.applyTemplateToRole(templateName, roleId);
    }
}