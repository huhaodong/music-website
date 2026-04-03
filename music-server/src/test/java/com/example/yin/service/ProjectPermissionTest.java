package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.ProjectMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectPermissionTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ConsumerMapper consumerMapper;

    private Consumer org1User;
    private Consumer org2User;
    private Project org1Project;

    @BeforeEach
    void setUp() {
        projectMapper.delete(new QueryWrapper<>());
        consumerMapper.delete(new QueryWrapper<>());

        org1User = new Consumer();
        org1User.setUsername("org1");
        org1User.setPassword("p");
        org1User.setOrgId(1);
        consumerMapper.insert(org1User);

        org2User = new Consumer();
        org2User.setUsername("org2");
        org2User.setPassword("p");
        org2User.setOrgId(2);
        consumerMapper.insert(org2User);

        org1Project = new Project();
        org1Project.setOrgId(1);
        org1Project.setName("P1");
        org1Project.setStatus("DRAFT");
        org1Project.setDeleted(0);
        org1Project.setCreatedBy(org1User.getId());
        projectMapper.insert(org1Project);
    }

    @Test
    @WithMockUser(username = "org2", roles = {"USER"})
    void otherOrgUser_shouldNotSeeProjectInListAndDetail() {
        Object data = projectService.pageProjects(null, null, null, "id", "desc", 1, 10).getData();
        Map<String, Object> page = (Map<String, Object>) data;
        List<Project> records = (List<Project>) page.get("records");
        assertTrue(records.isEmpty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> projectService.getProjectDetail(org1Project.getId()));
        assertEquals("权限不足", ex.getMessage());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void superAdmin_shouldSeeAnyOrgProject() {
        // list 看到 org1 的项目
        Object data = projectService.pageProjects(null, 1, null, "id", "desc", 1, 10).getData();
        Map<String, Object> page = (Map<String, Object>) data;
        List<Project> records = (List<Project>) page.get("records");
        assertEquals(1, records.size());

        assertTrue(projectService.getProjectDetail(org1Project.getId()).getSuccess());
    }
}

