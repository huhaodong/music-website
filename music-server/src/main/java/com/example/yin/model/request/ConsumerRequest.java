package com.example.yin.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @Author 祝英台炸油条
 * @Time : 2022/6/5 19:35
 * 这块 现在尝试把所有有关用户的属性都放入
 **/
@Data
public class ConsumerRequest {
    private Integer id;

    private String username;

    private String oldPassword;

    private String password;

    private Byte sex;

    private String phoneNum;

    private String email;

    private Date birth;

    private String introduction;

    private String location;

    private String avator;

    private String nickname;

    private Date createTime;

    private MultipartFile avatarFile;

    private String code;

    private Integer roleId;

    private Integer status;
}