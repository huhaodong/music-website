package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.yin.common.R;
import com.example.yin.mapper.ArtistMapper;
import com.example.yin.mapper.ProjectArtistMapper;
import com.example.yin.model.domain.Artist;
import com.example.yin.model.domain.ProjectArtist;
import com.example.yin.model.request.ArtistCreateRequest;
import com.example.yin.model.request.ArtistUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ArtistServiceTest {

    @MockBean
    private ArtistMapper artistMapper;

    @MockBean
    private ProjectArtistMapper projectArtistMapper;

    @Autowired
    private ArtistService artistService;

    private ArtistCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        createRequest = new ArtistCreateRequest();
        createRequest.setName("周杰伦");
        createRequest.setTypes(Arrays.asList("singer"));
    }

    @Test
    void createArtist_shouldInsertAndReturnArtist() {
        when(artistMapper.insert(any(Artist.class))).thenReturn(1);

        R result = artistService.createArtist(createRequest);

        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("添加成功", result.getMessage());
        assertNotNull(result.getData());
        verify(artistMapper, times(1)).insert(any(Artist.class));
    }

    @Test
    void createArtist_shouldReturnErrorWhenInsertFail() {
        when(artistMapper.insert(any(Artist.class))).thenReturn(0);

        R result = artistService.createArtist(createRequest);

        assertFalse(result.getSuccess());
        assertEquals("添加失败", result.getMessage());
    }

    @Test
    void updateArtist_shouldUpdateSuccessfully() {
        ArtistUpdateRequest req = new ArtistUpdateRequest();
        req.setName("更新名");
        when(artistMapper.updateById(any(Artist.class))).thenReturn(1);

        R result = artistService.updateArtist(1, req);

        assertTrue(result.getSuccess());
        assertEquals("修改成功", result.getMessage());
        verify(artistMapper, times(1)).updateById(any(Artist.class));
    }

    @Test
    void deleteArtist_shouldRejectWhenHasAssociation() {
        when(projectArtistMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        R result = artistService.deleteArtist(1);

        assertFalse(result.getSuccess());
        assertTrue(result.getMessage().contains("已关联项目"));
        verify(artistMapper, never()).deleteById(anyInt());
    }

    @Test
    void deleteArtist_shouldDeleteWhenNoAssociation() {
        when(projectArtistMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(artistMapper.deleteById(1)).thenReturn(1);

        R result = artistService.deleteArtist(1);

        assertTrue(result.getSuccess());
        assertEquals("删除成功", result.getMessage());
        verify(artistMapper, times(1)).deleteById(1);
    }

    @Test
    void pageArtists_shouldReturnPageStructure() {
        Page<Artist> page = new Page<>(1, 10);
        Artist a1 = new Artist();
        a1.setId(1);
        a1.setName("A1");
        page.setRecords(Collections.singletonList(a1));
        page.setTotal(1);

        when(artistMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

        R result = artistService.pageArtists("singer", "A", 1, 10);

        assertTrue(result.getSuccess());
        assertNotNull(result.getData());
        HashMap<String, Object> data = (HashMap<String, Object>) result.getData();
        assertTrue(data.containsKey("records"));
        assertEquals(1L, data.get("total"));
        verify(artistMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    void listAssociatedProjects_shouldReturnList() {
        List<HashMap<String, Object>> rows = Arrays.asList(new HashMap<>(), new HashMap<>());
        when(artistMapper.selectAssociatedProjects(1)).thenReturn((List) rows);

        R result = artistService.listAssociatedProjects(1);

        assertTrue(result.getSuccess());
        assertTrue(result.getData() instanceof List);
        verify(artistMapper, times(1)).selectAssociatedProjects(1);
    }
}
