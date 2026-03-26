package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.request.ConsumerRequest;
import com.example.yin.service.ConsumerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
    private PasswordEncoder passwordEncoder;

    @Override
    public R addUser(ConsumerRequest registryRequest) {
        if (this.existUser(registryRequest.getUsername())) {
            return R.warning("用户名已注册");
        }
        Consumer consumer = new Consumer();
        BeanUtils.copyProperties(registryRequest, consumer);
        String password = passwordEncoder.encode(registryRequest.getPassword());
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
            QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", consumer.getEmail());
            Consumer one = consumerMapper.selectOne(queryWrapper);
            if (one != null) {
                return R.fatal("邮箱不允许重复");
            }
            if (consumerMapper.insert(consumer) > 0) {
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
    public R deleteUser(Integer id) {
        if (consumerMapper.deleteById(id) > 0) {
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    @Override
    public R allUser() {
        return R.success(null, consumerMapper.selectList(null));
    }

    @Override
    public R userOfId(Integer id) {
        Consumer consumer = consumerMapper.selectById(id);
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
}
