package com.example.yin.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
public class ArtistCreateRequest {

    @NotBlank(message = "name 不能为空")
    @Size(max = 100, message = "name 长度不能超过 100")
    private String name;

    /**
     * 类型列表：singer / lyricist / composer / producer ...
     */
    @NotEmpty(message = "type 不能为空")
    @Size(max = 200, message = "type 长度不能超过 200")
    private List<String> types;

    private Byte sex;

    private String pic;

    private Date birth;

    private String location;

    private String introduction;
}
