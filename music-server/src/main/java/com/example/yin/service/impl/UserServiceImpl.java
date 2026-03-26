package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.controller.MinioUploadController;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.RoleMapper;
import com.example.yin.mapper.UserRoleMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Role;
import com.example.yin.model.domain.UserRole;
import com.example.yin.model.request.ConsumerRequest;
import com.example.yin.service.UserService;
import com.example.yin.service.impl.SimpleOrderManager;
import com.example.yin.utils.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<ConsumerMapper, Consumer> implements UserService {

    @Autowired
    private ConsumerMapper consumerMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private SimpleOrderManager simpleOrderManager;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
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
        if (StringUtils.isBlank(consumer.getEmail())) {
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
                if (registryRequest.getRoleId() != null) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(consumer.getId());
                    userRole.setUserType("consumer");
                    userRole.setRoleId(registryRequest.getRoleId());
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

    @Override
    public R updateUserMsg(ConsumerRequest updateRequest) {
        Consumer consumer = new Consumer();
        BeanUtils.copyProperties(updateRequest, consumer);
        if (consumerMapper.updateById(consumer) > 0) {
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }

    @Override
    public R updateUserAvatar(ConsumerRequest request) {
        MultipartFile avatarFile = request.getAvatarFile();
        if (avatarFile == null) {
            return R.error("头像文件不能为空");
        }
        String fileName = avatarFile.getOriginalFilename();
        String imgPath = "/img/avatorImages/" + fileName;
        Consumer consumer = new Consumer();
        consumer.setId(request.getId());
        consumer.setAvator(imgPath);
        String uploadResult = MinioUploadController.uploadAtorImgFile(avatarFile);
        if ("File uploaded successfully!".equals(uploadResult) && consumerMapper.updateById(consumer) > 0) {
            return R.success("上传成功", imgPath);
        }
        return R.error("上传失败");
    }

    @Override
    public R updatePassword(ConsumerRequest updatePasswordRequest) {
        if (!this.verifyPassword(updatePasswordRequest.getUsername(), updatePasswordRequest.getOldPassword())) {
            return R.error("密码输入错误");
        }

        Consumer consumer = new Consumer();
        consumer.setId(updatePasswordRequest.getId());
        String secretPassword = passwordEncoder.encode(updatePasswordRequest.getPassword());
        consumer.setPassword(secretPassword);

        if (consumerMapper.updateById(consumer) > 0) {
            return R.success("密码修改成功");
        }
        return R.error("密码修改失败");
    }

    @Override
    public boolean existUser(String username) {
        QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return consumerMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean verifyPassword(String username, String password) {
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
            QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", id);
            queryWrapper.eq("user_type", "consumer");
            userRoleMapper.delete(queryWrapper);
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    @Override
    public R getAllUsers() {
        List<Consumer> consumers = consumerMapper.selectList(null);
        for (Consumer consumer : consumers) {
            QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", consumer.getId());
            queryWrapper.eq("user_type", "consumer");
            List<UserRole> userRoles = userRoleMapper.selectList(queryWrapper);

            List<Role> roles = new ArrayList<>();
            for (UserRole userRole : userRoles) {
                Role role = roleMapper.selectById(userRole.getRoleId());
                if (role != null) {
                    roles.add(role);
                }
            }
            consumer.setRoles(roles);
        }
        return R.success(null, consumers);
    }

    @Override
    public R getUserById(Integer id) {
        QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return R.success(null, consumerMapper.selectList(queryWrapper));
    }

    @Override
    public R login(ConsumerRequest loginRequest, HttpSession session) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (this.verifyPassword(username, password)) {
            session.setAttribute("username", username);
            Consumer consumer = new Consumer();
            consumer.setUsername(username);
            return R.success("登录成功", consumerMapper.selectList(new QueryWrapper<>(consumer)));
        }
        return R.error("用户名或密码错误");
    }

    @Override
    public R emailLogin(ConsumerRequest loginRequest, HttpSession session) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        Consumer consumer1 = findByEmail(email);
        if (this.verifyPassword(consumer1.getUsername(), password)) {
            session.setAttribute("username", consumer1.getUsername());
            Consumer consumer = new Consumer();
            consumer.setUsername(consumer1.getUsername());
            return R.success("登录成功", consumerMapper.selectList(new QueryWrapper<>(consumer)));
        }
        return R.error("用户名或密码错误");
    }

    @Override
    public Consumer findByEmail(String email) {
        QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return consumerMapper.selectOne(queryWrapper);
    }

    @Override
    public R resetPassword(ConsumerRequest updatePasswordRequest) {
        Consumer user = findByEmail(updatePasswordRequest.getEmail());
        String code = stringRedisTemplate.opsForValue().get("code");
        if (user == null) {
            return R.fatal("用户不存在");
        } else if (!code.equals(updatePasswordRequest.getCode())) {
            return R.fatal("验证码不存在或失效");
        }
        ConsumerRequest consumerRequest = new ConsumerRequest();
        BeanUtils.copyProperties(user, consumerRequest);
        consumerRequest.setPassword(updatePasswordRequest.getPassword());
        updatePasswordWithoutOldPassword(consumerRequest);
        return R.success("密码修改成功");
    }

    private void updatePasswordWithoutOldPassword(ConsumerRequest updatePasswordRequest) {
        Consumer consumer = new Consumer();
        consumer.setId(updatePasswordRequest.getId());
        String secretPassword = passwordEncoder.encode(updatePasswordRequest.getPassword());
        consumer.setPassword(secretPassword);
        consumerMapper.updateById(consumer);
    }

    @Override
    public R sendVerificationCode(String email) {
        Consumer user = findByEmail(email);
        if (user == null) {
            return R.fatal("用户不存在");
        }
        String code = RandomUtils.code();
        simpleOrderManager.sendCode(code, email);
        stringRedisTemplate.opsForValue().set("code", code, 5, TimeUnit.MINUTES);
        return R.success("发送成功");
    }

    @Override
    @Transactional
    public R batchDeleteUsers(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return R.error("用户ID列表不能为空");
        }
        int deletedCount = 0;
        for (Integer id : ids) {
            if (consumerMapper.deleteById(id) > 0) {
                deletedCount++;
                QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", id);
                queryWrapper.eq("user_type", "consumer");
                userRoleMapper.delete(queryWrapper);
            }
        }
        return R.success("批量删除成功", deletedCount);
    }

    @Override
    @Transactional
    public R batchAssignRoles(Integer[] userIds, String userType, List<Integer> roleIds) {
        if (userIds == null || userIds.length == 0) {
            return R.error("用户ID列表不能为空");
        }
        if (roleIds == null || roleIds.isEmpty()) {
            return R.error("角色ID列表不能为空");
        }

        int assignedCount = 0;
        for (Integer userId : userIds) {
            QueryWrapper<UserRole> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("user_id", userId);
            deleteWrapper.eq("user_type", userType);
            userRoleMapper.delete(deleteWrapper);

            for (Integer roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setUserType(userType);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
            assignedCount++;
        }
        return R.success("批量分配角色成功", assignedCount);
    }

    private String generateDefaultNickname() {
        QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("COALESCE(MAX(CAST(SUBSTRING(nickname, 5) AS UNSIGNED)), 0) as maxNum")
                    .likeRight("nickname", "默认用户");
        Consumer maxConsumer = consumerMapper.selectOne(queryWrapper);
        int maxNum = 0;
        if (maxConsumer != null) {
            String maxNickname = maxConsumer.getNickname();
            if (maxNickname != null && maxNickname.length() > 4) {
                try {
                    maxNum = Integer.parseInt(maxNickname.substring(4));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        QueryWrapper<Consumer> countWrapper = new QueryWrapper<>();
        countWrapper.likeRight("nickname", "默认用户");
        long count = consumerMapper.selectCount(countWrapper);

        int newNum = (int) count + 1;
        return String.format("默认用户%03d", newNum);
    }
}