package com.example.yin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "project_status_log")
public class ProjectStatusLog {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer projectId;

    @TableField("old_status")
    private String fromStatus;

    @TableField("new_status")
    private String toStatus;

    private Integer operatorId;

    private String operatorName;

    private String remark;

    private Date createTime;
}

