package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.common.R;
import com.example.yin.mapper.ArtistMapper;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.ProjectArtistMapper;
import com.example.yin.mapper.ProjectMapper;
import com.example.yin.mapper.SongMapper;
import com.example.yin.model.domain.Artist;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Project;
import com.example.yin.model.domain.ProjectArtist;
import com.example.yin.model.domain.Song;
import com.example.yin.model.request.ProjectCreateRequest;
import com.example.yin.model.request.ProjectUpdateRequest;
import com.example.yin.model.response.ProjectAssociatedArtistRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectArtistMapper projectArtistMapper;

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private ArtistMapper artistMapper;

    @Autowired
    private ConsumerMapper consumerMapper;

    private Consumer u1;
    private Consumer u2;

    @BeforeEach
    void setUp() {
        projectArtistMapper.delete(new QueryWrapper<>());
        songMapper.delete(new QueryWrapper<>());
        projectMapper.delete(new QueryWrapper<>());
        artistMapper.delete(new QueryWrapper<>());
        consumerMapper.delete(new QueryWrapper<>());

        u1 = new Consumer();
        u1.setUsername("u1");
        u1.setPassword("p");
        u1.setOrgId(1);
        consumerMapper.insert(u1);

        u2 = new Consumer();
        u2.setUsername("u2");
        u2.setPassword("p");
        u2.setOrgId(2);
        consumerMapper.insert(u2);
    }

    private Project insertProject(Integer orgId, Integer createdBy, String name, String status, int deleted) {
        Project p = new Project();
        p.setOrgId(orgId);
        p.setCreatedBy(createdBy);
        p.setName(name);
        p.setStatus(status);
        p.setDeleted(deleted);
        projectMapper.insert(p);
        return p;
    }

    @Test
    @WithMockUser(username = "u1", roles = {"USER"})
    void createProject_shouldForceOrgAndDraft() {
        ProjectCreateRequest req = new ProjectCreateRequest();
        req.setName("P");
        req.setDescription("D");
        req.setOrgId(999); // 普通用户应被忽略

        R r = projectService.createProject(req);
        assertTrue(r.getSuccess());

        Project created = (Project) r.getData();
        assertEquals(1, created.getOrgId());
        assertEquals("DRAFT", created.getStatus());
        assertEquals(0, created.getDeleted());
        assertEquals(u1.getId(), created.getCreatedBy());
    }

    @Test
    @WithMockUser(username = "u1", roles = {"USER"})
    void pageProjects_shouldEnforceOrgIsolationAndKeywordSearch() {
        insertProject(1, u1.getId(), "Hello Project", "DRAFT", 0);
        insertProject(2, u2.getId(), "Other Org", "DRAFT", 0);
        insertProject(1, u1.getId(), "Deleted", "DRAFT", 1);

        R r = projectService.pageProjects(null, null, "Hello", "id", "desc", 1, 10);
        assertTrue(r.getSuccess());

        Map<String, Object> data = (Map<String, Object>) r.getData();
        List<Project> records = (List<Project>) data.get("records");
        assertEquals(1, records.size());
        assertEquals("Hello Project", records.get(0).getName());
    }

    @Test
    @WithMockUser(username = "u1", roles = {"USER"})
    void getProjectDetail_shouldAggregateArtistsAndSongs() {
        Project p = insertProject(1, u1.getId(), "P1", "DRAFT", 0);

        Artist a = new Artist();
        a.setName("A1");
        a.setTypes(Arrays.asList("singer"));
        artistMapper.insert(a);

        ProjectArtist pa = new ProjectArtist();
        pa.setProjectId(p.getId());
        pa.setArtistId(a.getId());
        pa.setRole("singer");
        projectArtistMapper.insert(pa);

        Song s = new Song();
        s.setSingerId(1);
        s.setName("S1");
        s.setUrl("/s.mp3");
        songMapper.insert(s);
        // 直接用 SQL 字段更新 project_id（Song 实体未显式声明该字段）
        songMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Song>()
                .eq("id", s.getId()).set("project_id", p.getId()));

        R r = projectService.getProjectDetail(p.getId());
        assertTrue(r.getSuccess());

        Map<String, Object> detail = (Map<String, Object>) r.getData();
        assertNotNull(detail.get("project"));
        assertTrue(detail.get("artists") instanceof List);
        assertTrue(detail.get("songs") instanceof List);

        List<ProjectAssociatedArtistRow> artists = (List<ProjectAssociatedArtistRow>) detail.get("artists");
        assertEquals(1, artists.size());
        assertEquals("singer", artists.get(0).getRole());

        List<Song> songs = (List<Song>) detail.get("songs");
        assertEquals(1, songs.size());
        assertEquals("S1", songs.get(0).getName());
    }

    @Test
    @WithMockUser(username = "u1", roles = {"USER"})
    void deleteProject_shouldLogicalDeleteAndCleanupAssociations() {
        Project p = insertProject(1, u1.getId(), "P1", "DRAFT", 0);

        Artist a = new Artist();
        a.setName("A1");
        a.setTypes(Arrays.asList("singer"));
        artistMapper.insert(a);
        ProjectArtist pa = new ProjectArtist();
        pa.setProjectId(p.getId());
        pa.setArtistId(a.getId());
        pa.setRole("singer");
        projectArtistMapper.insert(pa);

        Song s = new Song();
        s.setSingerId(1);
        s.setName("S1");
        s.setUrl("/s.mp3");
        songMapper.insert(s);
        songMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<Song>()
                .eq("id", s.getId()).set("project_id", p.getId()));

        R r = projectService.deleteProject(p.getId());
        assertTrue(r.getSuccess());

        Project deleted = projectMapper.selectById(p.getId());
        assertEquals(1, deleted.getDeleted());

        Long paCount = projectArtistMapper.selectCount(new QueryWrapper<ProjectArtist>().eq("project_id", p.getId()));
        assertEquals(0L, paCount);

        // song.project_id 应被清空
        Song after = songMapper.selectById(s.getId());
        // Song 实体无 projectId 字段，直接用 SQL 校验
        List<Object> objs = songMapper.selectObjs(new QueryWrapper<Song>().select("project_id").eq("id", s.getId()));
        Integer projectId = null;
        if (objs != null && !objs.isEmpty()) {
            Object v = objs.get(0);
            projectId = v == null ? null : ((Number) v).intValue();
        }
        assertNull(projectId);
        assertNotNull(after);
    }

    @Test
    @WithMockUser(username = "u2", roles = {"USER"})
    void updateProject_notCreator_shouldReject() {
        Project p = insertProject(1, u1.getId(), "P1", "DRAFT", 0);

        ProjectUpdateRequest req = new ProjectUpdateRequest();
        req.setName("P2");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> projectService.updateProject(p.getId(), req));
        assertEquals("权限不足", ex.getMessage());
    }
}
