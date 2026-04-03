package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.controller.MinioUploadController;
import com.example.yin.mapper.ArtistMapper;
import com.example.yin.mapper.ProjectArtistMapper;
import com.example.yin.mapper.SingerMapper;
import com.example.yin.model.domain.Artist;
import com.example.yin.model.domain.ProjectArtist;
import com.example.yin.model.domain.Singer;
import com.example.yin.model.request.SingerRequest;
import com.example.yin.service.SingerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SingerServiceImpl extends ServiceImpl<SingerMapper, Singer> implements SingerService {

    @Autowired
    private SingerMapper singerMapper;

    @Autowired
    private ArtistMapper artistMapper;

    @Autowired
    private ProjectArtistMapper projectArtistMapper;

    @Override
    public R updateSingerMsg(SingerRequest updateSingerRequest) {
        // 优先基于 artist(type='singer') 进行更新（兼容迁移后数据）
        if (updateSingerRequest == null || updateSingerRequest.getId() == null) {
            return R.error("修改失败");
        }

        Artist existingArtist = getSingerArtistById(updateSingerRequest.getId());
        if (existingArtist != null) {
            Artist artist = new Artist();
            BeanUtils.copyProperties(updateSingerRequest, artist);
            artist.setId(updateSingerRequest.getId());
            artist.setTypes(java.util.Arrays.asList("singer"));
            if (artistMapper.updateById(artist) > 0) {
                return R.success("修改成功");
            }
            return R.error("修改失败");
        }

        // fallback：旧 singer 表
        Singer singer = new Singer();
        BeanUtils.copyProperties(updateSingerRequest, singer);
        if (singerMapper.updateById(singer) > 0) {
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }

    @Override
    public R updateSingerPic(MultipartFile avatorFile, int id) {
        String fileName =  avatorFile.getOriginalFilename();
        MinioUploadController.uploadImgFile(avatorFile);
        String imgPath = "/user01/singer/img/" + fileName;

        Artist existingArtist = getSingerArtistById(id);
        if (existingArtist != null) {
            Artist artist = new Artist();
            artist.setId(id);
            artist.setPic(imgPath);
            artist.setTypes(java.util.Arrays.asList("singer"));
            if (artistMapper.updateById(artist) > 0) {
                return R.success("上传成功", imgPath);
            }
            return R.error("上传失败");
        }

        // fallback：旧 singer 表
        Singer singer = new Singer();
        singer.setId(id);
        singer.setPic(imgPath);
        if (singerMapper.updateById(singer) > 0) {
            return R.success("上传成功", imgPath);
        }
        return R.error("上传失败");
    }

    @Override
    public R deleteSinger(Integer id) {
        Artist existingArtist = getSingerArtistById(id);
        if (existingArtist != null) {
            QueryWrapper<ProjectArtist> association = new QueryWrapper<>();
            association.eq("artist_id", id);
            Long count = projectArtistMapper.selectCount(association);
            if (count != null && count > 0) {
                return R.error("删除失败：该歌手已关联项目，无法删除");
            }
            if (artistMapper.deleteById(id) > 0) {
                return R.success("删除成功");
            }
            return R.error("删除失败");
        }

        // fallback：旧 singer 表
        if (singerMapper.deleteById(id) > 0) {
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    @Override
    public R allSinger() {
        QueryWrapper<Artist> q = new QueryWrapper<>();
        q.like("types_json", "singer").orderByDesc("id");
        if (artistMapper.selectCount(q) > 0) {
            return R.success(null, artistMapper.selectList(q));
        }
        return R.success(null, singerMapper.selectList(null));
    }

    @Override
    public R addSinger(SingerRequest addSingerRequest) {
        Artist artist = new Artist();
        BeanUtils.copyProperties(addSingerRequest, artist);
        artist.setTypes(java.util.Arrays.asList("singer"));
        String pic = "/img/avatorImages/user.jpg";
        if (artist.getPic() == null || artist.getPic().trim().isEmpty()) {
            artist.setPic(pic);
        }
        if (artistMapper.insert(artist) > 0) {
            return R.success("添加成功");
        }
        return R.error("添加失败");
    }

    @Override
    public R singerOfName(String name) {
        QueryWrapper<Artist> q = new QueryWrapper<>();
        q.like("types_json", "singer").like("name", name).orderByDesc("id");
        if (artistMapper.selectCount(q) > 0) {
            return R.success(null, artistMapper.selectList(q));
        }
        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", name);
        return R.success(null, singerMapper.selectList(queryWrapper));
    }

    @Override
    public R singerOfSex(Integer sex) {
        QueryWrapper<Artist> q = new QueryWrapper<>();
        q.like("types_json", "singer").eq("sex", sex).orderByDesc("id");
        if (artistMapper.selectCount(q) > 0) {
            return R.success(null, artistMapper.selectList(q));
        }
        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("sex", sex);
        return R.success(null, singerMapper.selectList(queryWrapper));
    }

    private Artist getSingerArtistById(Integer id) {
        if (id == null) {
            return null;
        }
        Artist artist = artistMapper.selectById(id);
        if (artist == null) {
            return null;
        }
        return artist.getTypes().contains("singer") ? artist : null;
    }
}
