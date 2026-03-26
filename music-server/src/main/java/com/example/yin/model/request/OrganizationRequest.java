package com.example.yin.model.request;

import lombok.Data;

@Data
public class OrganizationRequest {
    private Integer id;
    private String name;
    private Integer parentId;
    private Integer level;
    private String path;
    private Integer sort;
    private Integer status;
}