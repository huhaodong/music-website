package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.common.R;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.ProjectMapper;
import com.example.yin.mapper.ProjectStatusLogMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Project;
import com.example.yin.model.domain.ProjectStatusLog;
import com.example.yin.model.enums.ProjectStatus;
import com.example.yin.service.ProjectStatusService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class ProjectStatusServiceImpl implements ProjectStatusService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectStatusLogMapper projectStatusLogMapper;

    @Autowired
    private ConsumerMapper consumerMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R changeStatus(Integer projectId, String toStatus, String remark) {
        if (projectId == null) {
            return R.error("projectId 不能为空");
        }
        if (!ProjectStatus.isValid(toStatus)) {
            return R.error("状态不合法");
        }

        Project project = projectMapper.selectById(projectId);
        if (project == null || (project.getDeleted() != null && project.getDeleted() == 1)) {
            return R.error("项目不存在");
        }
        assertCanModify(project);

        String from = StringUtils.defaultIfBlank(project.getStatus(), ProjectStatus.DRAFT.name());
        if (from.equals(toStatus)) {
            return R.success("状态未变化");
        }

        if (!isAllowedTransition(from, toStatus)) {
            // 交给全局异常处理映射为 400
            throw new RuntimeException("非法状态转换");
        }

        Project update = new Project();
        update.setId(projectId);
        update.setStatus(toStatus);
        int updated = projectMapper.updateById(update);
        if (updated <= 0) {
            return R.error("状态更新失败");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Consumer me = tryGetCurrentConsumer();

        ProjectStatusLog log = new ProjectStatusLog();
        log.setProjectId(projectId);
        log.setFromStatus(from);
        log.setToStatus(toStatus);
        log.setOperatorId(me == null ? null : me.getId());
        log.setOperatorName(me == null ? (auth == null ? null : auth.getName()) : me.getUsername());
        log.setRemark(remark);
        log.setCreateTime(new Date());
        projectStatusLogMapper.insert(log);

        return R.success("状态更新成功");
    }

    private boolean isAllowedTransition(String from, String to) {
        // 合法转换：
        // DRAFT → RELEASED
        // DRAFT → HOLD
        // RELEASED → HOLD
        // HOLD → DRAFT
        // HOLD → RELEASED
        Set<String> allowed = new HashSet<>();
        allowed.add(ProjectStatus.DRAFT.name() + "->" + ProjectStatus.RELEASED.name());
        allowed.add(ProjectStatus.DRAFT.name() + "->" + ProjectStatus.HOLD.name());
        allowed.add(ProjectStatus.RELEASED.name() + "->" + ProjectStatus.HOLD.name());
        allowed.add(ProjectStatus.HOLD.name() + "->" + ProjectStatus.DRAFT.name());
        allowed.add(ProjectStatus.HOLD.name() + "->" + ProjectStatus.RELEASED.name());
        return allowed.contains(from + "->" + to);
    }

    private void assertCanModify(Project project) {
        if (project == null) {
            throw new RuntimeException("权限不足");
        }
        if (isSuperAdmin()) {
            return;
        }
        Consumer me = requireCurrentConsumer();
        if (project.getCreatedBy() != null && project.getCreatedBy().equals(me.getId())) {
            return;
        }
        throw new RuntimeException("权限不足");
    }

    private boolean isSuperAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority != null && "ROLE_ADMIN".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private Consumer requireCurrentConsumer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new RuntimeException("用户未登录");
        }
        QueryWrapper<Consumer> qw = new QueryWrapper<>();
        qw.eq("username", authentication.getName());
        Consumer me = consumerMapper.selectOne(qw);
        if (me == null) {
            throw new RuntimeException("用户未找到");
        }
        return me;
    }

    private Consumer tryGetCurrentConsumer() {
        try {
            return requireCurrentConsumer();
        } catch (RuntimeException ex) {
            return null;
        }
    }
}

