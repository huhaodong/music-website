package com.example.yin.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ProjectStatusUpdateRequest {

    @NotBlank(message = "status 不能为空")
    @Size(max = 32, message = "status 长度不能超过 32")
    private String status;

    @Size(max = 500, message = "remark 长度不能超过 500")
    private String remark;
}

