package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.Admin;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.request.ConsumerRequest;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface UserService extends IService<Consumer> {

    R addUser(ConsumerRequest registryRequest);

    R updateUserMsg(ConsumerRequest updateRequest);

    R updateUserAvatar(ConsumerRequest request);

    R updatePassword(ConsumerRequest updatePasswordRequest);

    boolean existUser(String username);

    boolean verifyPassword(String username, String password);

    R deleteUser(Integer id);

    R getAllUsers();

    R getUserById(Integer id);

    R login(ConsumerRequest loginRequest, HttpSession session);

    R emailLogin(ConsumerRequest loginRequest, HttpSession session);

    Consumer findByEmail(String email);

    R resetPassword(ConsumerRequest updatePasswordRequest);

    R sendVerificationCode(String email);

    R batchDeleteUsers(List<Integer> ids);

    R batchAssignRoles(Integer[] userIds, String userType, List<Integer> roleIds);
}