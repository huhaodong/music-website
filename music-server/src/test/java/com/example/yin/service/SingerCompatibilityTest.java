package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.common.R;
import com.example.yin.mapper.ArtistMapper;
import com.example.yin.mapper.ProjectArtistMapper;
import com.example.yin.mapper.SingerMapper;
import com.example.yin.model.domain.Artist;
import com.example.yin.model.domain.Singer;
import com.example.yin.model.request.SingerRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 旧 Singer API 兼容：优先走 artist(type='singer')
 */
@SpringBootTest
class SingerCompatibilityTest {

    @MockBean
    private SingerMapper singerMapper;

    @MockBean
    private ArtistMapper artistMapper;

    @MockBean
    private ProjectArtistMapper projectArtistMapper;

    @Autowired
    private SingerService singerService;

    @Test
    void addSinger_shouldInsertIntoArtistTable() {
        when(artistMapper.insert(any(Artist.class))).thenReturn(1);

        SingerRequest req = new SingerRequest();
        req.setName("测试歌手");

        R result = singerService.addSinger(req);

        assertTrue(result.getSuccess());
        verify(artistMapper, times(1)).insert(any(Artist.class));
        verify(singerMapper, never()).insert(any(Singer.class));
    }

    @Test
    void allSinger_shouldPreferArtistTableWhenExists() {
        when(artistMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);
        when(artistMapper.selectList(any(QueryWrapper.class))).thenReturn(Collections.emptyList());

        R result = singerService.allSinger();

        assertTrue(result.getSuccess());
        verify(artistMapper, times(1)).selectList(any(QueryWrapper.class));
        verify(singerMapper, never()).selectList(any());
    }
}

