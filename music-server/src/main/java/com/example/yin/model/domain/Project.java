package com.example.yin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "project")
public class Project {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer orgId;

    @TableField(exist = false)
    private String orgName;

    private String name;

    private String description;

    /**
     * DRAFT / RELEASED / HOLD
     */
    private String status;

    /**
     * 0: 未删除, 1: 已删除
     */
    private Integer deleted;

    private Integer createdBy;

    private Date createTime;

    private Date updateTime;
}

