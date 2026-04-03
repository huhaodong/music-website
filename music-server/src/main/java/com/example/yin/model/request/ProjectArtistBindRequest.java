package com.example.yin.model.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ProjectArtistBindRequest {

    @NotNull(message = "artistId 不能为空")
    @Min(value = 1, message = "artistId 必须大于 0")
    private Integer artistId;

    /**
     * 允许一次绑定多个角色（同一 artistId 可多角色）
     */
    @NotEmpty(message = "roles 不能为空")
    private List<@Size(max = 32, message = "role 长度不能超过 32") String> roles;
}

