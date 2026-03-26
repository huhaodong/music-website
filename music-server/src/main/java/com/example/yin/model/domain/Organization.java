package com.example.yin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "organization")
public class Organization {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private Integer parentId;

    private Integer level;

    private String path;

    private Integer sort;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private transient List<Organization> children;
}