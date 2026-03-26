package com.example.yin.model.request;

import lombok.Data;

@Data
public class PermissionRequest {
    private Integer id;
    private String name;
    private String code;
    private String type;
    private String url;
    private String method;
    private Integer parentId;
    private Integer sort;
    private Integer status;
}