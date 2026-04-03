package com.example.yin.model.request;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
public class ArtistUpdateRequest {

    @Size(max = 100, message = "name 长度不能超过 100")
    private String name;

    @Size(max = 200, message = "type 长度不能超过 200")
    private List<String> types;

    private Byte sex;

    private String pic;

    private Date birth;

    private String location;

    private String introduction;
}
