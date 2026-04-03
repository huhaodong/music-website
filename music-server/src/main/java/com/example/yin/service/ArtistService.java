package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.Artist;
import com.example.yin.model.request.ArtistCreateRequest;
import com.example.yin.model.request.ArtistUpdateRequest;

public interface ArtistService extends IService<Artist> {

    R createArtist(ArtistCreateRequest request);

    R updateArtist(Integer id, ArtistUpdateRequest request);

    R deleteArtist(Integer id);

    R getArtist(Integer id);

    /**
     * 分页查询：支持 type 筛选 + keyword 模糊搜索（name/introduction/location）+ 分页
     */
    R pageArtists(String type, String keyword, Integer page, Integer size);

    /**
     * 查询艺术家关联项目（基于 project_artist）
     */
    R listAssociatedProjects(Integer artistId);
}
