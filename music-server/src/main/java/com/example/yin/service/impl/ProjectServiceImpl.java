package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.OrgMapper;
import com.example.yin.mapper.ProjectArtistMapper;
import com.example.yin.mapper.ProjectMapper;
import com.example.yin.mapper.SongMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Org;
import com.example.yin.model.domain.Project;
import com.example.yin.model.domain.ProjectArtist;
import com.example.yin.model.domain.Song;
import com.example.yin.model.request.ProjectCreateRequest;
import com.example.yin.model.request.ProjectUpdateRequest;
import com.example.yin.model.response.ProjectAssociatedArtistRow;
import com.example.yin.service.ProjectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectArtistMapper projectArtistMapper;

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private ConsumerMapper consumerMapper;

    @Autowired
    private OrgMapper orgMapper;

    @Override
    public R pageProjects(String status,
                          Integer orgId,
                          String keyword,
                          String sortBy,
                          String sortOrder,
                          Integer page,
                          Integer size) {
        int current = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1) ? 10 : size;

        QueryWrapper<Project> qw = new QueryWrapper<>();
        qw.eq("deleted", 0);

        boolean isAdmin = isSuperAdmin();
        if (!isAdmin) {
            Consumer me = requireCurrentConsumer();
            if (me.getOrgId() == null) {
                return R.error("用户未绑定组织");
            }
            qw.eq("org_id", me.getOrgId());
        } else {
            if (orgId != null && orgId > 0) {
                qw.eq("org_id", orgId);
            }
        }

        if (StringUtils.isNotBlank(status)) {
            qw.eq("status", status);
        }
        if (StringUtils.isNotBlank(keyword)) {
            qw.and(w -> w.like("name", keyword).or().like("description", keyword));
        }

        applyOrderBy(qw, sortBy, sortOrder);

        Page<Project> p = new Page<>(current, pageSize);
        Page<Project> result = projectMapper.selectPage(p, qw);

        for (Project record : result.getRecords()) {
            if (record.getOrgId() != null) {
                Org org = orgMapper.selectById(record.getOrgId());
                if (org != null) {
                    record.setOrgName(org.getName());
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());
        data.put("pages", result.getPages());
        return R.success(null, data);
    }

    @Override
    public R getProjectDetail(Integer id) {
        if (id == null) {
            return R.error("id 不能为空");
        }
        Project project = projectMapper.selectById(id);
        if (project == null || project.getDeleted() != null && project.getDeleted() == 1) {
            return R.error("项目不存在");
        }
        assertCanView(project);

        List<ProjectAssociatedArtistRow> artists = projectMapper.selectAssociatedArtists(id);
        QueryWrapper<Song> songQw = new QueryWrapper<>();
        songQw.eq("project_id", id);
        songQw.orderByDesc("id");
        List<Song> songs = songMapper.selectList(songQw);

        Map<String, Object> projectMap = new HashMap<>();
        projectMap.put("id", project.getId());
        projectMap.put("orgId", project.getOrgId());
        if (project.getOrgId() != null) {
            Org org = orgMapper.selectById(project.getOrgId());
            if (org != null) {
                projectMap.put("orgName", org.getName());
            }
        }
        projectMap.put("name", project.getName());
        projectMap.put("description", project.getDescription());
        projectMap.put("status", project.getStatus());
        projectMap.put("createdBy", project.getCreatedBy());
        if (project.getCreatedBy() != null) {
            Consumer creator = consumerMapper.selectById(project.getCreatedBy());
            if (creator != null) {
                projectMap.put("createdByName", creator.getUsername());
            }
        }
        projectMap.put("createTime", project.getCreateTime());
        projectMap.put("updateTime", project.getUpdateTime());

        Map<String, Object> detail = new HashMap<>();
        detail.put("project", projectMap);
        detail.put("artists", artists);
        detail.put("songs", songs);
        return R.success(null, detail);
    }

    @Override
    public R createProject(ProjectCreateRequest request) {
        Consumer me = requireCurrentConsumer();
        Integer org = me.getOrgId();
        if (isSuperAdmin() && request.getOrgId() != null && request.getOrgId() > 0) {
            org = request.getOrgId();
        }
        if (org == null) {
            return R.error("orgId 不能为空");
        }

        Project project = new Project();
        BeanUtils.copyProperties(request, project);
        project.setOrgId(org);
        project.setCreatedBy(me.getId());
        project.setStatus("DRAFT");
        project.setDeleted(0);

        if (projectMapper.insert(project) > 0) {
            return R.success("添加成功", project);
        }
        return R.error("添加失败");
    }

    @Override
    public R updateProject(Integer id, ProjectUpdateRequest request) {
        if (id == null) {
            return R.error("id 不能为空");
        }
        Project exists = projectMapper.selectById(id);
        if (exists == null || exists.getDeleted() != null && exists.getDeleted() == 1) {
            return R.error("项目不存在");
        }
        assertCanModify(exists);

        if (request != null && request.getName() != null && request.getName().trim().isEmpty()) {
            return R.error("name 不能为空");
        }

        Project project = new Project();
        BeanUtils.copyProperties(request, project);
        project.setId(id);

        if (projectMapper.updateById(project) > 0) {
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deleteProject(Integer id) {
        if (id == null) {
            return R.error("id 不能为空");
        }
        Project exists = projectMapper.selectById(id);
        if (exists == null || exists.getDeleted() != null && exists.getDeleted() == 1) {
            return R.error("项目不存在");
        }
        assertCanModify(exists);

        Project p = new Project();
        p.setId(id);
        p.setDeleted(1);
        int updated = projectMapper.updateById(p);
        if (updated <= 0) {
            return R.error("删除失败");
        }

        // 清理项目-艺术家关联
        QueryWrapper<ProjectArtist> paQw = new QueryWrapper<>();
        paQw.eq("project_id", id);
        projectArtistMapper.delete(paQw);

        // 清理歌曲 project_id 关联（避免“已删除项目仍挂载歌曲”）
        UpdateWrapper<Song> songUw = new UpdateWrapper<>();
        songUw.eq("project_id", id).set("project_id", null);
        songMapper.update(null, songUw);

        return R.success("删除成功");
    }

    private void applyOrderBy(QueryWrapper<Project> qw, String sortBy, String sortOrder) {
        String order = StringUtils.defaultIfBlank(sortOrder, "desc").toLowerCase();
        boolean asc = "asc".equals(order);

        String column;
        if ("createTime".equals(sortBy) || "create_time".equals(sortBy)) {
            column = "create_time";
        } else if ("updateTime".equals(sortBy) || "update_time".equals(sortBy)) {
            column = "update_time";
        } else if ("name".equals(sortBy)) {
            column = "name";
        } else if ("id".equals(sortBy) || StringUtils.isBlank(sortBy)) {
            column = "id";
        } else {
            // 非白名单字段拒绝排序，避免 SQL 注入；回退默认
            column = "id";
        }

        if (asc) {
            qw.orderByAsc(column);
        } else {
            qw.orderByDesc(column);
        }
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
