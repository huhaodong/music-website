package com.example.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yin.model.domain.Project;
import com.example.yin.model.response.ProjectAssociatedArtistRow;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMapper extends BaseMapper<Project> {

    /**
     * 查询项目关联的艺术家（含角色），支持同一艺术家多角色（project_artist 唯一键包含 role）
     */
    List<ProjectAssociatedArtistRow> selectAssociatedArtists(Integer projectId);
}
