package com.example.yin.model.request;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class ProjectUpdateRequest {

    @Size(max = 200, message = "name 长度不能超过 200")
    private String name;

    @Size(max = 1000, message = "description 长度不能超过 1000")
    private String description;
}
