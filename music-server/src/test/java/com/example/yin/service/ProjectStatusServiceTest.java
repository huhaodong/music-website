package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.ProjectMapper;
import com.example.yin.mapper.ProjectStatusLogMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Project;
import com.example.yin.model.domain.ProjectStatusLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectStatusServiceTest {

    @Autowired
    private ProjectStatusService projectStatusService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectStatusLogMapper projectStatusLogMapper;

    @Autowired
    private ConsumerMapper consumerMapper;

    private Consumer u1;

    @BeforeEach
    void setUp() {
        projectStatusLogMapper.delete(new QueryWrapper<>());
        projectMapper.delete(new QueryWrapper<>());
        consumerMapper.delete(new QueryWrapper<>());

        u1 = new Consumer();
        u1.setUsername("u1");
        u1.setPassword("p");
        u1.setOrgId(1);
        consumerMapper.insert(u1);
    }

    private Project newProject(String status, Integer createdBy, Integer orgId) {
        Project p = new Project();
        p.setName("P1");
        p.setDescription("D");
        p.setOrgId(orgId);
        p.setCreatedBy(createdBy);
        p.setStatus(status);
        p.setDeleted(0);
        projectMapper.insert(p);
        return p;
    }

    @Test
    @WithMockUser(username = "u1", roles = {"USER"})
    void draftToReleased_shouldSucceedAndLog() {
        Project p = newProject("DRAFT", u1.getId(), 1);

        projectStatusService.changeStatus(p.getId(), "RELEASED", "go");

        Project updated = projectMapper.selectById(p.getId());
        assertEquals("RELEASED", updated.getStatus());

        List<ProjectStatusLog> logs = projectStatusLogMapper.selectList(
                new QueryWrapper<ProjectStatusLog>().eq("project_id", p.getId())
        );
        assertEquals(1, logs.size());
        assertEquals("DRAFT", logs.get(0).getFromStatus());
        assertEquals("RELEASED", logs.get(0).getToStatus());
    }

    @Test
    @WithMockUser(username = "u1", roles = {"USER"})
    void releasedToDraft_shouldReject() {
        Project p = newProject("RELEASED", u1.getId(), 1);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> projectStatusService.changeStatus(p.getId(), "DRAFT", null));
        assertEquals("非法状态转换", ex.getMessage());
    }

    @Test
    @WithMockUser(username = "u1", roles = {"USER"})
    void holdToDraft_shouldSucceed() {
        Project p = newProject("HOLD", u1.getId(), 1);

        projectStatusService.changeStatus(p.getId(), "DRAFT", null);

        Project updated = projectMapper.selectById(p.getId());
        assertEquals("DRAFT", updated.getStatus());
    }

    @Test
    @WithMockUser(username = "u2", roles = {"USER"})
    void nonOwner_shouldRejectStatusChange() {
        Consumer u2 = new Consumer();
        u2.setUsername("u2");
        u2.setPassword("p");
        u2.setOrgId(1);
        consumerMapper.insert(u2);

        Project p = newProject("DRAFT", u1.getId(), 1);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> projectStatusService.changeStatus(p.getId(), "HOLD", null));
        assertEquals("权限不足", ex.getMessage());
    }
}

