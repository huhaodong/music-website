package com.example.yin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yin.model.domain.Artist;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ArtistMapper extends BaseMapper<Artist> {

    /**
     * 查询艺术家关联的项目（按 project_artist 关联，返回基础字段 + role）
     */
    List<Map<String, Object>> selectAssociatedProjects(Integer artistId);
}
