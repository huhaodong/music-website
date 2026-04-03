package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.mapper.ArtistMapper;
import com.example.yin.mapper.ProjectArtistMapper;
import com.example.yin.model.domain.Artist;
import com.example.yin.model.domain.ProjectArtist;
import com.example.yin.model.request.ArtistCreateRequest;
import com.example.yin.model.request.ArtistUpdateRequest;
import com.example.yin.service.ArtistService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArtistServiceImpl extends ServiceImpl<ArtistMapper, Artist> implements ArtistService {

    @Autowired
    private ArtistMapper artistMapper;

    @Autowired
    private ProjectArtistMapper projectArtistMapper;

    @Override
    public R createArtist(ArtistCreateRequest request) {
        if (request == null) {
            return R.error("请求参数不能为空");
        }
        Artist artist = new Artist();
        BeanUtils.copyProperties(request, artist);
        if (StringUtils.isBlank(artist.getPic())) {
            artist.setPic("/img/avatorImages/user.jpg");
        }
        if (artistMapper.insert(artist) > 0) {
            return R.success("添加成功", artist);
        }
        return R.error("添加失败");
    }

    @Override
    public R updateArtist(Integer id, ArtistUpdateRequest request) {
        if (id == null) {
            return R.error("id 不能为空");
        }
        if (request == null) {
            return R.error("请求参数不能为空");
        }
        Artist existingArtist = artistMapper.selectById(id);
        if (existingArtist == null) {
            return R.error("未找到该艺术家");
        }
        Artist artist = new Artist();
        BeanUtils.copyProperties(request, artist);
        artist.setId(id);
        if (artistMapper.updateById(artist) > 0) {
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }

    @Override
    public R deleteArtist(Integer id) {
        if (id == null) {
            return R.error("id 不能为空");
        }
        QueryWrapper<ProjectArtist> association = new QueryWrapper<>();
        association.eq("artist_id", id);
        Long count = projectArtistMapper.selectCount(association);
        if (count != null && count > 0) {
            return R.error("删除失败：该艺术家已关联项目，无法删除");
        }
        if (artistMapper.deleteById(id) > 0) {
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    @Override
    public R getArtist(Integer id) {
        if (id == null) {
            return R.error("id 不能为空");
        }
        Artist artist = artistMapper.selectById(id);
        if (artist == null) {
            return R.error("未找到该艺术家");
        }
        return R.success(null, artist);
    }

    @Override
    public R pageArtists(String type, String keyword, Integer page, Integer size) {
        int current = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1) ? 10 : size;

        QueryWrapper<Artist> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(type)) {
            queryWrapper.like("types_json", type);
        }
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(w -> w.like("name", keyword)
                    .or()
                    .like("introduction", keyword)
                    .or()
                    .like("location", keyword));
        }
        queryWrapper.orderByDesc("id");

        Page<Artist> p = new Page<>(current, pageSize);
        Page<Artist> result = artistMapper.selectPage(p, queryWrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("current", result.getCurrent());
        data.put("size", result.getSize());
        data.put("pages", result.getPages());
        return R.success(null, data);
    }

    @Override
    public R listAssociatedProjects(Integer artistId) {
        if (artistId == null) {
            return R.error("artistId 不能为空");
        }
        List<Map<String, Object>> projects = artistMapper.selectAssociatedProjects(artistId);
        return R.success(null, projects);
    }
}
