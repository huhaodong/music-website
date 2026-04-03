package com.example.yin.model.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ProjectArtistUnbindRequest {

    @NotNull(message = "artistId 不能为空")
    @Min(value = 1, message = "artistId 必须大于 0")
    private Integer artistId;

    @NotBlank(message = "role 不能为空")
    @Size(max = 32, message = "role 长度不能超过 32")
    private String role;
}

