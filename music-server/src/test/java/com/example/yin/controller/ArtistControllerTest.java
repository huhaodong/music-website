package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.PermissionMapper;
import com.example.yin.mapper.RolePermissionMapper;
import com.example.yin.mapper.UserRoleMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Permission;
import com.example.yin.model.domain.RolePermission;
import com.example.yin.model.domain.UserRole;
import com.example.yin.service.ArtistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {"app.permission.enabled=true"})
class ArtistControllerTest {

    @MockBean
    private ArtistService artistService;

    // PermissionAspect 依赖的 mapper，在测试里用 mock 控制权限
    @MockBean
    private UserRoleMapper userRoleMapper;
    @MockBean
    private RolePermissionMapper rolePermissionMapper;
    @MockBean
    private PermissionMapper permissionMapper;
    @MockBean
    private ConsumerMapper consumerMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpUser() {
        Consumer c = new Consumer();
        c.setId(1);
        c.setUsername("u1");
        when(consumerMapper.selectOne(any())).thenReturn(c);
    }

    private void stubPermissions(String... codes) {
        UserRole ur = new UserRole();
        ur.setId(1);
        ur.setUserId(1);
        ur.setRoleId(1);
        ur.setUserType("consumer");
        when(userRoleMapper.selectList(any())).thenReturn(Collections.singletonList(ur));

        List<RolePermission> rps = new ArrayList<>();
        List<Integer> permIds = new ArrayList<>();
        for (int i = 0; i < codes.length; i++) {
            RolePermission rp = new RolePermission();
            rp.setId(i + 1);
            rp.setRoleId(1);
            rp.setPermissionId(i + 1);
            rps.add(rp);
            permIds.add(i + 1);
        }
        when(rolePermissionMapper.selectList(any())).thenReturn(rps);

        List<Permission> perms = new ArrayList<>();
        for (int i = 0; i < codes.length; i++) {
            Permission p = new Permission();
            p.setId(i + 1);
            p.setCode(codes[i]);
            perms.add(p);
        }
        when(permissionMapper.selectBatchIds(eq(permIds))).thenReturn(perms);
    }

    @Test
    @WithMockUser(username = "u1", roles = {"USER"})
    void pageArtists_withoutPermission_shouldReturn403() throws Exception {
        when(userRoleMapper.selectList(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/artist"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("权限不足"));
    }

    @Test
    @WithMockUser(username = "u1", roles = {"USER"})
    void pageArtists_withPermission_shouldReturn200() throws Exception {
        stubPermissions("artist:list");
        Map<String, Object> page = new HashMap<>();
        page.put("records", Collections.emptyList());
        page.put("total", 0);
        when(artistService.pageArtists(any(), any(), any(), any()))
                .thenReturn(R.success(null, page));

        mockMvc.perform(get("/api/artist")
                        .param("type", "singer")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @WithMockUser(username = "u1", roles = {"USER"})
    void createArtist_invalidBody_shouldReturn400() throws Exception {
        stubPermissions("artist:create");

        Map<String, Object> body = new HashMap<>();
        body.put("type", "singer");

        mockMvc.perform(post("/api/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "u1", roles = {"USER"})
    void listAssociatedProjects_withPermission_shouldReturn200() throws Exception {
        stubPermissions("artist:projects");
        when(artistService.listAssociatedProjects(eq(1))).thenReturn(R.success(null, Arrays.asList()));

        mockMvc.perform(get("/api/artist/1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
}
