package com.example.yin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "permission")
public class Permission {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String code;

    private String type;

    private String url;

    private String method;

    private Integer parentId;

    private Integer sort;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private transient List<Permission> children;
}