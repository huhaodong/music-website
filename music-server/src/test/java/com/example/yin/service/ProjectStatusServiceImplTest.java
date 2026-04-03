package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.common.R;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.ProjectMapper;
import com.example.yin.mapper.ProjectStatusLogMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Project;
import com.example.yin.model.domain.ProjectStatusLog;
import com.example.yin.service.impl.ProjectStatusServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectStatusServiceImpl 单元测试（覆盖率补齐）")
class ProjectStatusServiceImplTest {

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectStatusLogMapper projectStatusLogMapper;

    @Mock
    private ConsumerMapper consumerMapper;

    @InjectMocks
    private ProjectStatusServiceImpl projectStatusService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("projectId 为空 - 返回错误")
    void changeStatus_projectIdNull() {
        R r = projectStatusService.changeStatus(null, "DRAFT", null);
        assertFalse(r.getSuccess());
        assertEquals("projectId 不能为空", r.getMessage());
    }

    @Test
    @DisplayName("toStatus 非法 - 返回错误")
    void changeStatus_invalidToStatus() {
        R r = projectStatusService.changeStatus(1, "NOT_A_STATUS", null);
        assertFalse(r.getSuccess());
        assertEquals("状态不合法", r.getMessage());
    }

    @Test
    @DisplayName("项目不存在/已删除 - 返回错误")
    void changeStatus_projectNotFoundOrDeleted() {
        when(projectMapper.selectById(1)).thenReturn(null);
        R r1 = projectStatusService.changeStatus(1, "DRAFT", null);
        assertFalse(r1.getSuccess());
        assertEquals("项目不存在", r1.getMessage());

        Project deleted = new Project();
        deleted.setId(2);
        deleted.setDeleted(1);
        when(projectMapper.selectById(2)).thenReturn(deleted);
        R r2 = projectStatusService.changeStatus(2, "DRAFT", null);
        assertFalse(r2.getSuccess());
        assertEquals("项目不存在", r2.getMessage());
    }

    @Test
    @DisplayName("状态未变化 - 返回成功且不更新/不写日志")
    void changeStatus_sameStatus_noUpdateNoLog() {
        mockAuth("u1", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Consumer me = new Consumer();
        me.setId(10);
        me.setUsername("u1");
        when(consumerMapper.selectOne(any(QueryWrapper.class))).thenReturn(me);

        Project p = new Project();
        p.setId(1);
        p.setDeleted(0);
        p.setCreatedBy(10);
        p.setStatus("DRAFT");
        when(projectMapper.selectById(1)).thenReturn(p);

        R r = projectStatusService.changeStatus(1, "DRAFT", "same");
        assertTrue(r.getSuccess());
        assertEquals("状态未变化", r.getMessage());

        verify(projectMapper, never()).updateById(any());
        verify(projectStatusLogMapper, never()).insert(any());
    }

    @Test
    @DisplayName("非法状态转换 - 抛出异常")
    void changeStatus_invalidTransition_shouldThrow() {
        mockAuth("u1", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Consumer me = new Consumer();
        me.setId(10);
        me.setUsername("u1");
        when(consumerMapper.selectOne(any(QueryWrapper.class))).thenReturn(me);

        Project p = new Project();
        p.setId(1);
        p.setDeleted(0);
        p.setCreatedBy(10);
        p.setStatus("RELEASED");
        when(projectMapper.selectById(1)).thenReturn(p);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> projectStatusService.changeStatus(1, "DRAFT", null));
        assertEquals("非法状态转换", ex.getMessage());
    }

    @Test
    @DisplayName("更新失败 - 返回错误")
    void changeStatus_updateFail() {
        mockAuth("u1", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Consumer me = new Consumer();
        me.setId(10);
        me.setUsername("u1");
        when(consumerMapper.selectOne(any(QueryWrapper.class))).thenReturn(me);

        Project p = new Project();
        p.setId(1);
        p.setDeleted(0);
        p.setCreatedBy(10);
        p.setStatus("DRAFT");
        when(projectMapper.selectById(1)).thenReturn(p);
        when(projectMapper.updateById(any(Project.class))).thenReturn(0);

        R r = projectStatusService.changeStatus(1, "HOLD", null);
        assertFalse(r.getSuccess());
        assertEquals("状态更新失败", r.getMessage());
        verify(projectStatusLogMapper, never()).insert(any());
    }

    @Test
    @DisplayName("非登录用户且非管理员 - 权限校验拒绝")
    void changeStatus_notLoggedIn_shouldDeny() {
        SecurityContextHolder.clearContext();

        Project p = new Project();
        p.setId(1);
        p.setDeleted(0);
        p.setCreatedBy(10);
        p.setStatus("DRAFT");
        when(projectMapper.selectById(1)).thenReturn(p);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> projectStatusService.changeStatus(1, "HOLD", null));
        assertEquals("用户未登录", ex.getMessage());
    }

    @Test
    @DisplayName("非管理员且不是创建者 - 权限不足")
    void changeStatus_notOwner_shouldDeny() {
        mockAuth("u2", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Consumer me = new Consumer();
        me.setId(20);
        me.setUsername("u2");
        when(consumerMapper.selectOne(any(QueryWrapper.class))).thenReturn(me);

        Project p = new Project();
        p.setId(1);
        p.setDeleted(0);
        p.setCreatedBy(10);
        p.setStatus("DRAFT");
        when(projectMapper.selectById(1)).thenReturn(p);

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> projectStatusService.changeStatus(1, "HOLD", null));
        assertEquals("权限不足", ex.getMessage());
    }

    @Test
    @DisplayName("管理员可变更状态；用户查不到时 operatorName 回退到 auth.name，且写日志")
    void changeStatus_admin_shouldSucceed_andLogWithFallbackOperator() {
        mockAuth("admin", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        // consumer 查不到：tryGetCurrentConsumer 会吞掉异常并返回 null，operatorName 用 auth.getName()
        when(consumerMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        Project p = new Project();
        p.setId(1);
        p.setDeleted(0);
        p.setCreatedBy(999);
        p.setStatus(null); // 覆盖 defaultIfBlank -> DRAFT
        when(projectMapper.selectById(1)).thenReturn(p);
        when(projectMapper.updateById(any(Project.class))).thenReturn(1);
        when(projectStatusLogMapper.insert(any(ProjectStatusLog.class))).thenReturn(1);

        R r = projectStatusService.changeStatus(1, "RELEASED", "ok");
        assertTrue(r.getSuccess());
        assertEquals("状态更新成功", r.getMessage());

        ArgumentCaptor<ProjectStatusLog> logCaptor = ArgumentCaptor.forClass(ProjectStatusLog.class);
        verify(projectStatusLogMapper, times(1)).insert(logCaptor.capture());
        ProjectStatusLog log = logCaptor.getValue();
        assertEquals("DRAFT", log.getFromStatus());
        assertEquals("RELEASED", log.getToStatus());
        assertNull(log.getOperatorId());
        assertEquals("admin", log.getOperatorName());
        assertEquals("ok", log.getRemark());
    }

    private void mockAuth(String username, List<SimpleGrantedAuthority> authorities) {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
