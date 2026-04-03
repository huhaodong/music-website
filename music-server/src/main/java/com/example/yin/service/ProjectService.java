package com.example.yin.service;

import com.example.yin.common.R;
import com.example.yin.model.request.ProjectCreateRequest;
import com.example.yin.model.request.ProjectUpdateRequest;

public interface ProjectService {

    R pageProjects(String status,
                   Integer orgId,
                   String keyword,
                   String sortBy,
                   String sortOrder,
                   Integer page,
                   Integer size);

    R getProjectDetail(Integer id);

    R createProject(ProjectCreateRequest request);

    R updateProject(Integer id, ProjectUpdateRequest request);

    /**
     * 逻辑删除：project.deleted=1，并清理关联（project_artist + song.project_id）
     */
    R deleteProject(Integer id);
}

