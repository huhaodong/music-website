package com.example.yin.controller;

import com.example.yin.model.request.SongRequest;
import com.example.yin.config.TestMinioConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SongController 集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestMinioConfig.class)
class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试获取所有歌曲列表
     */
    @Test
    void allSong_shouldReturnSongList() throws Exception {
        mockMvc.perform(get("/song")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").exists());
    }

    /**
     * 测试根据ID获取歌曲详情
     */
    @Test
    void songOfId_shouldReturnSongDetail() throws Exception {
        mockMvc.perform(get("/song/detail")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试根据歌手ID获取歌曲列表
     */
    @Test
    void songOfSingerId_shouldReturnSongListBySingerId() throws Exception {
        mockMvc.perform(get("/song/singer/detail")
                .param("singerId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试根据歌手名获取歌曲列表
     */
    @Test
    void songOfSingerName_shouldReturnSongListBySingerName() throws Exception {
        mockMvc.perform(get("/song/singerName/detail")
                .param("name", "周杰伦")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试添加歌曲
     */
    @Test
    void addSong_shouldReturnSuccessResult() throws Exception {
        SongRequest songRequest = new SongRequest();
        songRequest.setName("测试歌曲");
        songRequest.setSingerId(1);
        songRequest.setIntroduction("测试简介");
        songRequest.setCreateTime(new Date());

        MockMultipartFile lrcFile = new MockMultipartFile(
                "lrcfile",
                "test.lrc",
                "text/plain",
                "[00:00:00]测试歌词".getBytes()
        );

        MockMultipartFile mpFile = new MockMultipartFile(
                "file",
                "test.mp3",
                "audio/mpeg",
                "fake audio content".getBytes()
        );

        mockMvc.perform(multipart("/song/add")
                .file(lrcFile)
                .file(mpFile)
                .param("name", songRequest.getName())
                .param("singerId", String.valueOf(songRequest.getSingerId()))
                .param("introduction", songRequest.getIntroduction())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试删除歌曲
     */
    @Test
    void deleteSong_shouldReturnSuccessResult() throws Exception {
        mockMvc.perform(delete("/song/delete")
                .param("id", "999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试更新歌曲信息
     */
    @Test
    void updateSongMsg_shouldReturnSuccessResult() throws Exception {
        SongRequest updateRequest = new SongRequest();
        updateRequest.setId(1);
        updateRequest.setName("更新后的歌曲名");
        updateRequest.setIntroduction("更新后的简介");
        updateRequest.setUpdateTime(new Date());

        mockMvc.perform(post("/song/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试更新歌曲图片
     */
    @Test
    void updateSongPic_shouldReturnSuccessResult() throws Exception {
        MockMultipartFile picFile = new MockMultipartFile(
                "file",
                "cover.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        mockMvc.perform(multipart("/song/img/update")
                .file(picFile)
                .param("id", "1")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    /**
     * 测试更新歌曲URL
     */
    @Test
    void updateSongUrl_shouldReturnSuccessResult() throws Exception {
        MockMultipartFile urlFile = new MockMultipartFile(
                "file",
                "song.mp3",
                "audio/mpeg",
                "fake audio content".getBytes()
        );

        mockMvc.perform(multipart("/song/url/update")
                .file(urlFile)
                .param("id", "1")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    /**
     * 测试更新歌词
     */
    @Test
    void updateSongLrc_shouldReturnSuccessResult() throws Exception {
        MockMultipartFile lrcFile = new MockMultipartFile(
                "file",
                "lyric.lrc",
                "text/plain",
                "[00:00:00]更新后的歌词".getBytes()
        );

        mockMvc.perform(multipart("/song/lrc/update")
                .file(lrcFile)
                .param("id", "1")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    /**
     * 测试获取不存在的歌曲ID
     */
    @Test
    void songOfId_withNonExistentId_shouldReturnAppropriateResponse() throws Exception {
        mockMvc.perform(get("/song/detail")
                .param("id", "999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * 测试缺少必需参数
     */
    @Test
    void songOfId_withoutId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/song/detail")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试获取歌曲列表返回数组
     */
    @Test
    void allSong_shouldReturnArray() throws Exception {
        mockMvc.perform(get("/song")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}
