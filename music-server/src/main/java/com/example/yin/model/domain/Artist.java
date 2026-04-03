package com.example.yin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableName(value = "artist")
@Data
public class Artist {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String typesJson;

    private Byte sex;

    private String pic;

    private Date birth;

    private String location;

    private String introduction;

    private Date createTime;

    private Date updateTime;

    public List<String> getTypes() {
        if (typesJson == null || typesJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(typesJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    public void setTypes(List<String> types) {
        if (types == null || types.isEmpty()) {
            this.typesJson = "[]";
            return;
        }
        try {
            this.typesJson = objectMapper.writeValueAsString(types);
        } catch (JsonProcessingException e) {
            this.typesJson = "[]";
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
