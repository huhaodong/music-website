package com.example.yin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "role_permission")
public class RolePermission {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer roleId;

    private Integer permissionId;

    private LocalDateTime createTime;
}