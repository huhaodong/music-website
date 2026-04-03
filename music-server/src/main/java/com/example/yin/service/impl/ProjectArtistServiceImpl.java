package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.common.R;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.ProjectArtistMapper;
import com.example.yin.mapper.ProjectMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Project;
import com.example.yin.model.domain.ProjectArtist;
import com.example.yin.model.response.ProjectAssociatedArtistRow;
import com.example.yin.service.ProjectArtistService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProjectArtistServiceImpl implements ProjectArtistService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectArtistMapper projectArtistMapper;

    @Autowired
    private ConsumerMapper consumerMapper;

    @Override
    public R listProjectArtists(Integer projectId) {
        if (projectId == null) {
            return R.error("projectId 不能为空");
        }
        Project project = projectMapper.selectById(projectId);
        if (project == null || (project.getDeleted() != null && project.getDeleted() == 1)) {
            return R.error("项目不存在");
        }
        assertCanView(project);

        List<ProjectAssociatedArtistRow> rows = projectMapper.selectAssociatedArtists(projectId);
        return R.success(null, rows);
    }

    @Override
    public R bindArtistRoles(Integer projectId, Integer artistId, List<String> roles) {
        if (projectId == null) {
            return R.error("projectId 不能为空");
        }
        if (artistId == null) {
            return R.error("artistId 不能为空");
        }
        if (roles == null || roles.isEmpty()) {
            return R.error("roles 不能为空");
        }

        Project project = projectMapper.selectById(projectId);
        if (project == null || (project.getDeleted() != null && project.getDeleted() == 1)) {
            return R.error("项目不存在");
        }
        assertCanModify(project);

        List<ProjectArtist> inserted = new ArrayList<>();
        for (String role : roles) {
            if (StringUtils.isBlank(role)) {
                continue;
            }
            ProjectArtist pa = new ProjectArtist();
            pa.setProjectId(projectId);
            pa.setArtistId(artistId);
            pa.setRole(role.trim());
            pa.setCreateTime(new Date());
            pa.setUpdateTime(new Date());
            try {
                projectArtistMapper.insert(pa);
                inserted.add(pa);
            } catch (DuplicateKeyException ex) {
                return R.error("关联已存在：" + role);
            }
        }
        return R.success("关联成功", inserted);
    }

    @Override
    public R unbindArtistRole(Integer projectId, Integer artistId, String role) {
        if (projectId == null) {
            return R.error("projectId 不能为空");
        }
        if (artistId == null) {
            return R.error("artistId 不能为空");
        }
        if (StringUtils.isBlank(role)) {
            return R.error("role 不能为空");
        }

        Project project = projectMapper.selectById(projectId);
        if (project == null || (project.getDeleted() != null && project.getDeleted() == 1)) {
            return R.error("项目不存在");
        }
        assertCanModify(project);

        QueryWrapper<ProjectArtist> qw = new QueryWrapper<>();
        qw.eq("project_id", projectId).eq("artist_id", artistId).eq("role", role);
        int deleted = projectArtistMapper.delete(qw);
        if (deleted > 0) {
            return R.success("取消关联成功");
        }
        return R.error("未找到关联关系");
    }

    private void assertCanView(Project project) {
        if (project == null) {
            throw new RuntimeException("权限不足");
        }
        if (isSuperAdmin()) {
            return;
        }
        Consumer me = requireCurrentConsumer();
        if (project.getOrgId() != null && project.getOrgId().equals(me.getOrgId())) {
            return;
        }
        if (project.getCreatedBy() != null && project.getCreatedBy().equals(me.getId())) {
            return;
        }
        throw new RuntimeException("权限不足");
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
}
