package com.example.yin.model.request;

import lombok.Data;

@Data
public class RoleRequest {
    private Integer id;
    private String name;
    private String code;
    private String description;
    private Integer status;
}