package com.example.yin.controller;

import com.example.yin.annotation.RequirePermission;
import com.example.yin.common.R;
import com.example.yin.model.request.OrganizationRequest;
import com.example.yin.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @PostMapping("/add")
    @RequirePermission("org:add")
    public R addOrganization(@RequestBody OrganizationRequest organizationRequest) {
        return organizationService.addOrganization(organizationRequest);
    }

    @PutMapping("/update")
    @RequirePermission("org:edit")
    public R updateOrganization(@RequestBody OrganizationRequest organizationRequest) {
        return organizationService.updateOrganization(organizationRequest);
    }

    @DeleteMapping("/delete")
    @RequirePermission("org:delete")
    public R deleteOrganization(@RequestParam Integer id) {
        return organizationService.deleteOrganization(id);
    }

    @GetMapping("/detail")
    @RequirePermission("org:detail")
    public R getOrganizationById(@RequestParam Integer id) {
        return organizationService.getOrganizationById(id);
    }

    @GetMapping("/list")
    @RequirePermission("org:list")
    public R getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }

    @GetMapping("/tree")
    public R getOrganizationTree() {
        return organizationService.getOrganizationTree();
    }

    @GetMapping("/children")
    public R getChildrenOrganizations(@RequestParam(required = false) Integer parentId) {
        return organizationService.getChildrenOrganizations(parentId);
    }
}