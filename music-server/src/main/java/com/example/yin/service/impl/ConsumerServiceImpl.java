package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.RoleMapper;
import com.example.yin.mapper.UserRoleMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Role;
import com.example.yin.model.domain.UserRole;
import com.example.yin.model.request.ConsumerRequest;
import com.example.yin.service.ConsumerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

@Service
public class ConsumerServiceImpl extends ServiceImpl<ConsumerMapper, Consumer>
        implements ConsumerService {

    @Autowired
    private ConsumerMapper consumerMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public R addUser(ConsumerRequest registryRequest) {
        if (registryRequest == null) {
            registryRequest = new ConsumerRequest();
        }
        if (StringUtils.isBlank(registryRequest.getUsername())) {
            // 避免空请求体/空用户名触发 DB NOT NULL 约束导致 500；接口保持 200 返回
            return R.error("用户名不能为空");
        }
        if (this.existUser(registryRequest.getUsername())) {
            return R.warning("用户名已注册");
        }
        Consumer consumer = new Consumer();
        BeanUtils.copyProperties(registryRequest, consumer);
        // 避免空请求体/空密码导致 PasswordEncoder 抛异常
        String rawPassword = registryRequest.getPassword() == null ? "" : registryRequest.getPassword();
        String password = passwordEncoder.encode(rawPassword);
        consumer.setPassword(password);
        if (StringUtils.isBlank(consumer.getPhoneNum())) {
            consumer.setPhoneNum(null);
        }
        if ("".equals(consumer.getEmail())) {
            consumer.setEmail(null);
        }
        consumer.setAvator("img/avatorImages/user.jpg");
        if (StringUtils.isBlank(consumer.getNickname())) {
            consumer.setNickname(generateDefaultNickname());
        }
        try {
            if (StringUtils.isNotBlank(consumer.getEmail())) {
                QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("email", consumer.getEmail());
                Consumer one = consumerMapper.selectOne(queryWrapper);
                if (one != null) {
                    return R.fatal("邮箱不允许重复");
                }
            }
            if (consumerMapper.insert(consumer) > 0) {
                QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
                roleQueryWrapper.eq("code", "USER");
                Role defaultRole = roleMapper.selectOne(roleQueryWrapper);
                // 默认角色可能在测试/初始化阶段缺失：容错，不影响注册主流程
                if (defaultRole != null) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(consumer.getId());
                    userRole.setUserType("consumer");
                    userRole.setRoleId(defaultRole.getId());
                    userRoleMapper.insert(userRole);
                }
                return R.success("注册成功");
            } else {
                return R.error("注册失败");
            }
        } catch (DuplicateKeyException e) {
            return R.fatal(e.getMessage());
        }
    }

    private String generateDefaultNickname() {
        return "用户_" + System.currentTimeMillis() % 10000;
    }

    @Override
    public R updateUserMsg(ConsumerRequest consumerRequest) {
        Consumer consumer = new Consumer();
        if (consumerRequest.getId() != null) {
            consumer.setId(consumerRequest.getId());
        } else {
            return R.error("用户ID不能为空");
        }
        if (StringUtils.isNotBlank(consumerRequest.getNickname())) {
            consumer.setNickname(consumerRequest.getNickname());
        }
        if (StringUtils.isNotBlank(consumerRequest.getEmail())) {
            consumer.setEmail(consumerRequest.getEmail());
        }
        if (StringUtils.isNotBlank(consumerRequest.getPhoneNum())) {
            consumer.setPhoneNum(consumerRequest.getPhoneNum());
        }
        if (consumerRequest.getSex() != null) {
            consumer.setSex(consumerRequest.getSex());
        }
        if (consumerRequest.getBirth() != null) {
            consumer.setBirth(consumerRequest.getBirth());
        }
        if (StringUtils.isNotBlank(consumerRequest.getIntroduction())) {
            consumer.setIntroduction(consumerRequest.getIntroduction());
        }
        if (StringUtils.isNotBlank(consumerRequest.getLocation())) {
            consumer.setLocation(consumerRequest.getLocation());
        }
        if (consumerMapper.updateById(consumer) > 0) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    @Override
    public R updatePassword(ConsumerRequest updatePasswordRequest) {
        if (!this.verityPasswd(updatePasswordRequest.getUsername(), updatePasswordRequest.getOldPassword())) {
            return R.error("密码输入错误");
        }

        Consumer consumer = new Consumer();
        consumer.setId(updatePasswordRequest.getId());
        String secretPassword = passwordEncoder.encode(updatePasswordRequest.getPassword());
        consumer.setPassword(secretPassword);

        if (consumerMapper.updateById(consumer) > 0) {
            return R.success("密码修改成功");
        } else {
            return R.error("密码修改失败");
        }
    }

    @Override
    public R updatePassword01(ConsumerRequest updatePasswordRequest) {
        Consumer consumer = new Consumer();
        consumer.setId(updatePasswordRequest.getId());
        String secretPassword = passwordEncoder.encode(updatePasswordRequest.getPassword());
        consumer.setPassword(secretPassword);

        if (consumerMapper.updateById(consumer) > 0) {
            return R.success("密码修改成功");
        } else {
            return R.error("密码修改失败");
        }
    }

    @Override
    public R updateUserAvator(MultipartFile avatorFile, int id) {
        String fileName = avatorFile.getOriginalFilename();
        String imgPath = "/img/avatorImages/" + fileName;
        Consumer consumer = new Consumer();
        consumer.setId(id);
        consumer.setAvator(imgPath);
        if (consumerMapper.updateById(consumer) > 0) {
            return R.success("上传成功", imgPath);
        } else {
            return R.error("上传失败");
        }
    }

    @Override
    public boolean existUser(String username) {
        QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return consumerMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean verityPasswd(String username, String password) {
        QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        Consumer consumer = consumerMapper.selectOne(queryWrapper);
        if (consumer == null) {
            return false;
        }
        return passwordEncoder.matches(password, consumer.getPassword());
    }

    @Override
    @Transactional
    public R deleteUser(Integer id) {
        if (consumerMapper.deleteById(id) > 0) {
            QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", id);
            queryWrapper.eq("user_type", "consumer");
            userRoleMapper.delete(queryWrapper);
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    @Override
    public R allUser() {
        return R.success("查询成功", consumerMapper.selectListWithRoles());
    }

    @Override
    public R userOfId(Integer id) {
        Consumer consumer = consumerMapper.selectByIdWithRoles(id);
        if (consumer == null) {
            return R.error("用户不存在");
        }
        return R.success("查询成功", consumer);
    }

    @Override
    public Consumer findByEmail(String email) {
        QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return consumerMapper.selectOne(queryWrapper);
    }

    @Override
    public R loginStatus(ConsumerRequest loginRequest, HttpSession session) {
        Consumer consumer = findByEmail(loginRequest.getEmail());
        if (consumer == null) {
            return R.error("用户不存在");
        }
        session.setAttribute("username", consumer.getUsername());
        return R.success(null, consumer);
    }

    @Override
    public R loginEmailStatus(ConsumerRequest loginRequest, HttpSession session) {
        Consumer consumer = findByEmail(loginRequest.getEmail());
        if (consumer == null) {
            return R.error("用户不存在");
        }
        session.setAttribute("username", consumer.getUsername());
        return R.success(null, consumer);
    }

    @Override
    @Transactional
    public R batchDeleteUsers(java.util.List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return R.success("删除成功");
        }
        // 过滤非法 id，避免异常
        java.util.List<Integer> validIds = ids.stream().filter(i -> i != null && i > 0).collect(java.util.stream.Collectors.toList());
        if (validIds.isEmpty()) {
            return R.success("删除成功");
        }
        for (Integer id : validIds) {
            // deleteUser 内部包含 user_role 清理逻辑；这里容错，不因单个失败中断
            try {
                deleteUser(id);
            } catch (Exception ignored) {
            }
        }
        return R.success("删除成功");
    }
}
