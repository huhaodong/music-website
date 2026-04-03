package com.example.yin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "project_artist")
public class ProjectArtist {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer projectId;

    private Integer artistId;

    private String role;

    private Date createTime;

    private Date updateTime;
}
