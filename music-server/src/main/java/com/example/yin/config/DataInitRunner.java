package com.example.yin.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.RoleMapper;
import com.example.yin.mapper.UserRoleMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Role;
import com.example.yin.model.domain.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataInitRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitRunner.class);

    private final ConsumerMapper consumerMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    public DataInitRunner(ConsumerMapper consumerMapper, UserRoleMapper userRoleMapper, RoleMapper roleMapper) {
        this.consumerMapper = consumerMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public void run(String... args) {
        log.info("========== Starting data initialization: assigning default roles to users without roles ==========");

        Role defaultRole = getDefaultUserRole();
        if (defaultRole == null) {
            log.error("Default role 'USER' not found in database. Cannot initialize user roles. Please ensure role table has a role with code='USER'");
            return;
        }
        log.info("Found default role: id={}, name={}, code={}", defaultRole.getId(), defaultRole.getName(), defaultRole.getCode());

        List<Consumer> allUsers = consumerMapper.selectList(null);
        log.info("Total users found in database: {}", allUsers.size());

        int initializedCount = 0;
        int alreadyHasRoleCount = 0;

        for (Consumer user : allUsers) {
            try {
                QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", user.getId());
                queryWrapper.eq("user_type", "consumer");
                Long roleCount = userRoleMapper.selectCount(queryWrapper);

                if (roleCount == null || roleCount == 0) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(user.getId());
                    userRole.setUserType("consumer");
                    userRole.setRoleId(defaultRole.getId());
                    userRoleMapper.insert(userRole);
                    initializedCount++;
                    log.info("Assigned default role to user: id={}, username={}", user.getId(), user.getUsername());
                } else {
                    alreadyHasRoleCount++;
                    log.debug("User already has roles: id={}, username={}, roleCount={}", user.getId(), user.getUsername(), roleCount);
                }
            } catch (Exception e) {
                log.error("Failed to assign role to user: id={}, username={}, error={}", user.getId(), user.getUsername(), e.getMessage());
            }
        }

        log.info("========== Data initialization completed: {} users were assigned the default role, {} users already had roles ==========", initializedCount, alreadyHasRoleCount);
    }

    private Role getDefaultUserRole() {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", "USER");
        Role role = roleMapper.selectOne(queryWrapper);

        if (role == null) {
            log.warn("No role found with code='USER', checking all roles...");
            List<Role> allRoles = roleMapper.selectList(null);
            for (Role r : allRoles) {
                log.warn("Available role: id={}, name={}, code={}", r.getId(), r.getName(), r.getCode());
            }
        }
        return role;
    }
}