package com.example.yin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "user_role")
public class UserRole {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String userType;

    private Integer roleId;

    private LocalDateTime createTime;
}