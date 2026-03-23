package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.config.TestMinioConfig;
import com.example.yin.mapper.SongMapper;
import com.example.yin.model.domain.Song;
import com.example.yin.model.request.SongRequest;
import com.example.yin.service.impl.SongServiceImpl;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(TestMinioConfig.class)
class SongServiceTest {

    @MockBean
    private SongMapper songMapper;

    @MockBean
    private MinioClient minioClient;

    @Autowired
    private SongService songService;

    private Song testSong;

    @BeforeEach
    void setUp() {
        testSong = new Song();
        testSong.setId(1);
        testSong.setName("Test Song");
        testSong.setSingerId(1);
        testSong.setIntroduction("Test Introduction");
        testSong.setPic("/img/songPic/tubiao.jpg");
        testSong.setLyric("[00:00:00]暂无歌词");
        testSong.setUrl("/bucket/test.mp3");

        ReflectionTestUtils.setField(songService, "bucketName", "test-bucket");
    }

    @Test
    void allSong_shouldReturnAllSongs() {
        Song song1 = new Song();
        song1.setId(1);
        song1.setName("Song 1");
        Song song2 = new Song();
        song2.setId(2);
        song2.setName("Song 2");
        when(songMapper.selectList(any())).thenReturn(Arrays.asList(song1, song2));

        R result = songService.allSong();

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        List<Song> songs = (List<Song>) result.getData();
        assertEquals(2, songs.size());
        verify(songMapper, times(1)).selectList(any());
    }

    @Test
    void allSong_shouldReturnEmptyListWhenNoSongs() {
        when(songMapper.selectList(any())).thenReturn(Collections.emptyList());

        R result = songService.allSong();

        assertNotNull(result);
        assertTrue(result.getSuccess());
        List<Song> songs = (List<Song>) result.getData();
        assertTrue(songs.isEmpty());
    }

    @Test
    void songOfSingerId_shouldReturnSongsOfSinger() {
        when(songMapper.selectList(any())).thenReturn(Collections.singletonList(testSong));

        R result = songService.songOfSingerId(1);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        List<Song> songs = (List<Song>) result.getData();
        assertEquals(1, songs.size());
        assertEquals(1, songs.get(0).getSingerId());
    }

    @Test
    void songOfId_shouldReturnSongById() {
        when(songMapper.selectList(any())).thenReturn(Collections.singletonList(testSong));

        R result = songService.songOfId(1);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        List<Song> songs = (List<Song>) result.getData();
        assertEquals(1, songs.size());
        assertEquals(1, songs.get(0).getId());
    }

    @Test
    void songOfSingerName_shouldReturnSongsByName() {
        when(songMapper.selectList(any())).thenReturn(Collections.singletonList(testSong));

        R result = songService.songOfSingerName("Test");

        assertNotNull(result);
        assertTrue(result.getSuccess());
        List<Song> songs = (List<Song>) result.getData();
        assertEquals(1, songs.size());
    }

    @Test
    void songOfSingerName_shouldReturnErrorWhenNoSongsFound() {
        when(songMapper.selectList(any())).thenReturn(Collections.emptyList());

        R result = songService.songOfSingerName("NonExistent");

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("添加失败，没有找到该歌,无法加入该歌单", result.getMessage());
    }

    @Test
    void updateSongMsg_shouldUpdateSongSuccessfully() {
        SongRequest request = new SongRequest();
        request.setId(1);
        request.setName("Updated Song");
        request.setIntroduction("Updated Introduction");
        when(songMapper.updateById(any(Song.class))).thenReturn(1);

        R result = songService.updateSongMsg(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("修改成功", result.getMessage());
        verify(songMapper, times(1)).updateById(any(Song.class));
    }

    @Test
    void updateSongMsg_shouldReturnErrorWhenUpdateFails() {
        SongRequest request = new SongRequest();
        request.setId(1);
        request.setName("Updated Song");
        when(songMapper.updateById(any(Song.class))).thenReturn(0);

        R result = songService.updateSongMsg(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("修改失败", result.getMessage());
    }

    @Test
    void deleteSong_shouldDeleteSuccessfully() throws Exception {
        when(songMapper.selectById(1)).thenReturn(testSong);
        when(songMapper.deleteById(1)).thenReturn(1);
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        R result = songService.deleteSong(1);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("删除成功", result.getMessage());
        verify(songMapper, times(1)).deleteById(1);
    }

    @Test
    void deleteSong_shouldReturnErrorWhenSongNotFound() {
        when(songMapper.selectById(999)).thenReturn(null);

        R result = songService.deleteSong(999);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("删除失败", result.getMessage());
    }

    @Test
    void deleteSong_shouldReturnErrorWhenDeleteFails() {
        when(songMapper.selectById(1)).thenReturn(testSong);
        when(songMapper.deleteById(1)).thenReturn(0);

        R result = songService.deleteSong(1);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("删除失败", result.getMessage());
    }

    @Test
    void updateSongLrc_shouldUpdateLrcSuccessfully() {
        when(songMapper.selectById(1)).thenReturn(testSong);
        when(songMapper.updateById(any(Song.class))).thenReturn(1);
        MockMultipartFile lrcFile = new MockMultipartFile(
            "lrcFile", "test.lrc", "text/plain", "[00:00:00]歌词内容".getBytes()
        );

        R result = songService.updateSongLrc(lrcFile, 1);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("更新成功", result.getMessage());
    }

    @Test
    void updateSongLrc_shouldReturnErrorWhenSongNotFound() {
        when(songMapper.selectById(999)).thenReturn(null);

        R result = songService.updateSongLrc(null, 999);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("更新失败", result.getMessage());
    }

    @Test
    void updateSongLrc_shouldReturnErrorWhenUpdateFails() {
        when(songMapper.selectById(1)).thenReturn(testSong);
        when(songMapper.updateById(any(Song.class))).thenReturn(0);

        R result = songService.updateSongLrc(null, 1);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("更新失败", result.getMessage());
    }
}
