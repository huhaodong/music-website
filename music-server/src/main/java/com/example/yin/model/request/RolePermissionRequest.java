package com.example.yin.model.request;

import lombok.Data;

import java.util.List;

@Data
public class RolePermissionRequest {
    private Integer roleId;
    private List<Integer> permissionIds;
}