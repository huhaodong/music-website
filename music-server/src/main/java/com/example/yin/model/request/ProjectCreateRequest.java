package com.example.yin.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ProjectCreateRequest {

    /**
     * 仅超级管理员可指定；普通用户会被强制写入当前用户 orgId
     */
    private Integer orgId;

    @NotBlank(message = "name 不能为空")
    @Size(max = 200, message = "name 长度不能超过 200")
    private String name;

    @Size(max = 1000, message = "description 长度不能超过 1000")
    private String description;
}

