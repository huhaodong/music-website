package com.example.yin.model.request;

import lombok.Data;

import java.util.List;

@Data
public class UserRoleRequest {
    private Integer userId;
    private String userType;
    private List<Integer> roleIds;
}