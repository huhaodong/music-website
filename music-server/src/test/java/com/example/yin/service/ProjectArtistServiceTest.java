package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.mapper.ArtistMapper;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.ProjectArtistMapper;
import com.example.yin.mapper.ProjectMapper;
import com.example.yin.model.domain.Artist;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Project;
import com.example.yin.model.domain.ProjectArtist;
import com.example.yin.model.response.ProjectAssociatedArtistRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectArtistServiceTest {

    @Autowired
    private ProjectArtistService projectArtistService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectArtistMapper projectArtistMapper;

    @Autowired
    private ArtistMapper artistMapper;

    @Autowired
    private ConsumerMapper consumerMapper;

    private Consumer u1;
    private Project p1;
    private Artist a1;

    @BeforeEach
    void setUp() {
        projectArtistMapper.delete(new QueryWrapper<>());
        projectMapper.delete(new QueryWrapper<>());
        artistMapper.delete(new QueryWrapper<>());
        consumerMapper.delete(new QueryWrapper<>());

        u1 = new Consumer();
        u1.setUsername("u1");
        u1.setPassword("p");
        u1.setOrgId(1);
        consumerMapper.insert(u1);

        p1 = new Project();
        p1.setOrgId(1);
        p1.setName("P1");
        p1.setStatus("DRAFT");
        p1.setDeleted(0);
        p1.setCreatedBy(u1.getId());
        projectMapper.insert(p1);

        a1 = new Artist();
        a1.setName("A1");
        a1.setTypes(Arrays.asList("singer"));
        artistMapper.insert(a1);
    }

    @Test
    @WithMockUser(username = "u1", roles = {"USER"})
    void bindSameArtistMultiRoles_shouldSucceed() {
        projectArtistService.bindArtistRoles(p1.getId(), a1.getId(), Arrays.asList("singer", "composer"));

        List<ProjectArtist> list = projectArtistMapper.selectList(
                new QueryWrapper<ProjectArtist>().eq("project_id", p1.getId()).eq("artist_id", a1.getId())
        );
        assertEquals(2, list.size());
    }

    @Test
    @WithMockUser(username = "u1", roles = {"USER"})
    void listProjectArtists_shouldReturnRoles() {
        projectArtistService.bindArtistRoles(p1.getId(), a1.getId(), Arrays.asList("singer", "composer"));

        Object data = projectArtistService.listProjectArtists(p1.getId()).getData();
        assertNotNull(data);
        assertTrue(data instanceof List);
        List<ProjectAssociatedArtistRow> rows = (List<ProjectAssociatedArtistRow>) data;
        assertEquals(2, rows.size());
        assertTrue(rows.stream().anyMatch(r -> "singer".equals(r.getRole())));
        assertTrue(rows.stream().anyMatch(r -> "composer".equals(r.getRole())));
    }

    @Test
    @WithMockUser(username = "u1", roles = {"USER"})
    void unbindRole_shouldRemoveOnlyOneRole() {
        projectArtistService.bindArtistRoles(p1.getId(), a1.getId(), Arrays.asList("singer", "composer"));

        projectArtistService.unbindArtistRole(p1.getId(), a1.getId(), "composer");

        List<ProjectArtist> list = projectArtistMapper.selectList(
                new QueryWrapper<ProjectArtist>().eq("project_id", p1.getId()).eq("artist_id", a1.getId())
        );
        assertEquals(1, list.size());
        assertEquals("singer", list.get(0).getRole());
    }
}
