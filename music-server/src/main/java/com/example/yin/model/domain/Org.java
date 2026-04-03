package com.example.yin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName(value = "organization")
@Data
public class Org {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String code;

    private Integer parentId;

    private String path;

    private Integer level;

    private Integer sort;

    private Integer status;

    private String description;

    private Date createTime;

    private Date updateTime;
}