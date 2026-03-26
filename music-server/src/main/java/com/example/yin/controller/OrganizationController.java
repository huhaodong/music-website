package com.example.yin.controller;

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
    public R addOrganization(@RequestBody OrganizationRequest organizationRequest) {
        return organizationService.addOrganization(organizationRequest);
    }

    @PutMapping("/update")
    public R updateOrganization(@RequestBody OrganizationRequest organizationRequest) {
        return organizationService.updateOrganization(organizationRequest);
    }

    @DeleteMapping("/delete")
    public R deleteOrganization(@RequestParam Integer id) {
        return organizationService.deleteOrganization(id);
    }

    @GetMapping("/detail")
    public R getOrganizationById(@RequestParam Integer id) {
        return organizationService.getOrganizationById(id);
    }

    @GetMapping("/list")
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