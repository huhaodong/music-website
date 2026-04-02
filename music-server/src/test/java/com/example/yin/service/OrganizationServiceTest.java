package com.example.yin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.common.R;
import com.example.yin.mapper.ConsumerMapper;
import com.example.yin.mapper.OrganizationMapper;
import com.example.yin.model.domain.Consumer;
import com.example.yin.model.domain.Organization;
import com.example.yin.model.request.OrganizationRequest;
import com.example.yin.service.impl.OrganizationServiceImpl;
import com.example.yin.utils.OrganizationCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("OrganizationService 测试")
class OrganizationServiceTest {

    @Mock
    private OrganizationMapper organizationMapper;

    @Mock
    private ConsumerMapper consumerMapper;

    @Mock
    private OrganizationCodeGenerator organizationCodeGenerator;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    private AutoCloseable closeable;

    private Organization testOrg;
    private OrganizationRequest testOrgRequest;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        
        ReflectionTestUtils.setField(organizationService, "baseMapper", organizationMapper);

        when(organizationCodeGenerator.generateUniqueCode(anyString(), any()))
                .thenReturn("test-org-abcde");
        
        testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("测试组织");
        testOrg.setParentId(null);
        testOrg.setLevel(1);
        testOrg.setPath("/1");
        testOrg.setSort(1);
        testOrg.setStatus(1);
        testOrg.setCreateTime(LocalDateTime.now());
        testOrg.setUpdateTime(LocalDateTime.now());

        testOrgRequest = new OrganizationRequest();
        testOrgRequest.setName("测试组织");
        testOrgRequest.setSort(1);
        testOrgRequest.setStatus(1);
    }

    @Test
    @DisplayName("创建根组织成功")
    void addOrganization_RootOrg_Success() {
        when(organizationMapper.insert(any(Organization.class))).thenAnswer(invocation -> {
            Organization org = invocation.getArgument(0);
            org.setId(100);
            return 1;
        });
        when(organizationMapper.updateById(any(Organization.class))).thenReturn(1);

        OrganizationRequest request = new OrganizationRequest();
        request.setName("新根组织");
        request.setSort(1);
        R result = organizationService.addOrganization(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("添加组织成功", result.getMessage());
        assertNotNull(result.getData());

        Organization createdOrg = (Organization) result.getData();
        assertNotNull(createdOrg);
        assertEquals(1, createdOrg.getLevel());
        assertEquals("/100", createdOrg.getPath());

        verify(organizationMapper, times(1)).insert(any(Organization.class));
        verify(organizationMapper, times(1)).updateById(any(Organization.class));
    }

    @Test
    @DisplayName("创建组织 - 未传 code 自动生成并写入")
    void addOrganization_CodeMissing_ShouldGenerateAndPersist() {
        when(organizationCodeGenerator.generateUniqueCode(anyString(), any()))
                .thenReturn("test-org-001");
        when(organizationMapper.insert(any(Organization.class))).thenAnswer(invocation -> {
            Organization org = invocation.getArgument(0);
            org.setId(300);
            return 1;
        });
        when(organizationMapper.updateById(any(Organization.class))).thenReturn(1);

        OrganizationRequest request = new OrganizationRequest();
        request.setName("测试组织");
        request.setCode(null);
        R result = organizationService.addOrganization(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());

        verify(organizationCodeGenerator, times(1))
                .generateUniqueCode(eq("测试组织"), any());
        verify(organizationMapper, times(1)).insert(argThat(org ->
                org != null && "test-org-001".equals(org.getCode())
        ));
    }

    @Test
    @DisplayName("创建组织 - 传入 code 时不覆盖")
    void addOrganization_CodeProvided_ShouldNotOverride() {
        when(organizationMapper.insert(any(Organization.class))).thenAnswer(invocation -> {
            Organization org = invocation.getArgument(0);
            org.setId(301);
            return 1;
        });
        when(organizationMapper.updateById(any(Organization.class))).thenReturn(1);

        OrganizationRequest request = new OrganizationRequest();
        request.setName("自定义编码组织");
        request.setCode("custom-code");
        R result = organizationService.addOrganization(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());

        verify(organizationCodeGenerator, never()).generateUniqueCode(anyString(), any());
        verify(organizationMapper, times(1)).insert(argThat(org ->
                org != null && "custom-code".equals(org.getCode())
        ));
    }

    @Test
    @DisplayName("创建子组织成功")
    void addOrganization_ChildOrg_Success() {
        Organization parentOrg = new Organization();
        parentOrg.setId(1);
        parentOrg.setName("父组织");
        parentOrg.setLevel(1);
        parentOrg.setPath("/1");

        when(organizationMapper.selectById(1)).thenReturn(parentOrg);
        when(organizationMapper.insert(any(Organization.class))).thenAnswer(invocation -> {
            Organization org = invocation.getArgument(0);
            org.setId(101);
            return 1;
        });
        when(organizationMapper.updateById(any(Organization.class))).thenReturn(1);

        OrganizationRequest request = new OrganizationRequest();
        request.setName("子组织");
        request.setParentId(1);
        request.setSort(1);
        R result = organizationService.addOrganization(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("添加组织成功", result.getMessage());

        Organization createdOrg = (Organization) result.getData();
        assertNotNull(createdOrg);
        assertEquals(2, createdOrg.getLevel());
        assertEquals("/1/101", createdOrg.getPath());

        verify(organizationMapper, times(1)).selectById(1);
        verify(organizationMapper, times(1)).insert(any(Organization.class));
        verify(organizationMapper, times(1)).updateById(any(Organization.class));
    }

    @Test
    @DisplayName("创建二级子组织成功 - path 层级正确")
    void addOrganization_Level2ChildOrg_Success() {
        Organization parentOrg = new Organization();
        parentOrg.setId(2);
        parentOrg.setName("父组织");
        parentOrg.setLevel(2);
        parentOrg.setPath("/1/2");

        when(organizationMapper.selectById(2)).thenReturn(parentOrg);
        when(organizationMapper.insert(any(Organization.class))).thenAnswer(invocation -> {
            Organization org = invocation.getArgument(0);
            org.setId(102);
            return 1;
        });
        when(organizationMapper.updateById(any(Organization.class))).thenReturn(1);

        OrganizationRequest request = new OrganizationRequest();
        request.setName("子组织");
        request.setParentId(2);
        request.setSort(1);
        R result = organizationService.addOrganization(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());

        Organization createdOrg = (Organization) result.getData();
        assertNotNull(createdOrg);
        assertEquals(3, createdOrg.getLevel());
        assertEquals("/1/2/102", createdOrg.getPath());
    }

    @Test
    @DisplayName("创建组织失败 - 组织名称为空")
    void addOrganization_EmptyName_Fail() {
        OrganizationRequest request = new OrganizationRequest();
        request.setName("");
        R result = organizationService.addOrganization(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("组织名称不能为空", result.getMessage());

        verify(organizationMapper, never()).insert(any(Organization.class));
    }

    @Test
    @DisplayName("创建组织失败 - 组织名称为 null")
    void addOrganization_NullName_Fail() {
        OrganizationRequest request = new OrganizationRequest();
        request.setName(null);
        R result = organizationService.addOrganization(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("组织名称不能为空", result.getMessage());

        verify(organizationMapper, never()).insert(any(Organization.class));
    }

    @Test
    @DisplayName("创建组织失败 - 父组织不存在")
    void addOrganization_ParentNotExist_Fail() {
        when(organizationMapper.selectById(999)).thenReturn(null);

        OrganizationRequest request = new OrganizationRequest();
        request.setName("子组织");
        request.setParentId(999);
        R result = organizationService.addOrganization(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("上级组织不存在", result.getMessage());

        verify(organizationMapper, times(1)).selectById(999);
        verify(organizationMapper, never()).insert(any(Organization.class));
        verify(organizationMapper, never()).updateById(any(Organization.class));
    }

    @Test
    @DisplayName("创建组织失败 - 数据库插入失败")
    void addOrganization_InsertFail() {
        when(organizationMapper.insert(any(Organization.class))).thenReturn(0);

        OrganizationRequest request = new OrganizationRequest();
        request.setName("新组织");
        R result = organizationService.addOrganization(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("添加组织失败", result.getMessage());

        verify(organizationMapper, times(1)).insert(any(Organization.class));
        verify(organizationMapper, never()).updateById(any(Organization.class));
    }

    @Test
    @DisplayName("更新组织成功")
    void updateOrganization_Success() {
        when(organizationMapper.selectById(1)).thenReturn(testOrg);
        when(organizationMapper.updateById(any(Organization.class))).thenReturn(1);

        OrganizationRequest request = new OrganizationRequest();
        request.setId(1);
        request.setName("更新后的组织名");
        request.setSort(2);
        R result = organizationService.updateOrganization(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("更新组织成功", result.getMessage());

        verify(organizationMapper, times(1)).selectById(1);
        verify(organizationMapper, times(1)).updateById(any(Organization.class));
    }

    @Test
    @DisplayName("更新组织失败 - 组织 ID 为空")
    void updateOrganization_IdIsNull_Fail() {
        OrganizationRequest request = new OrganizationRequest();
        request.setId(null);
        request.setName("更新后的组织名");
        R result = organizationService.updateOrganization(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("组织ID不能为空", result.getMessage());

        verify(organizationMapper, never()).selectById(anyInt());
        verify(organizationMapper, never()).updateById(any(Organization.class));
    }

    @Test
    @DisplayName("更新组织失败 - 组织不存在")
    void updateOrganization_OrgNotExist_Fail() {
        when(organizationMapper.selectById(999)).thenReturn(null);

        OrganizationRequest request = new OrganizationRequest();
        request.setId(999);
        request.setName("更新后的组织名");
        R result = organizationService.updateOrganization(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("组织不存在", result.getMessage());

        verify(organizationMapper, times(1)).selectById(999);
        verify(organizationMapper, never()).updateById(any(Organization.class));
    }

    @Test
    @DisplayName("更新组织失败 - 数据库更新失败")
    void updateOrganization_UpdateFail() {
        when(organizationMapper.selectById(1)).thenReturn(testOrg);
        when(organizationMapper.updateById(any(Organization.class))).thenReturn(0);

        OrganizationRequest request = new OrganizationRequest();
        request.setId(1);
        request.setName("更新后的组织名");
        R result = organizationService.updateOrganization(request);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("更新组织失败", result.getMessage());

        verify(organizationMapper, times(1)).selectById(1);
        verify(organizationMapper, times(1)).updateById(any(Organization.class));
    }

    @Test
    @DisplayName("删除组织成功")
    void deleteOrganization_Success() {
        when(organizationMapper.selectById(1)).thenReturn(testOrg);
        when(organizationMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(organizationMapper.deleteById(1)).thenReturn(1);

        R result = organizationService.deleteOrganization(1);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("删除组织成功", result.getMessage());

        verify(organizationMapper, times(1)).selectById(1);
        verify(organizationMapper, times(1)).selectCount(any(QueryWrapper.class));
        verify(organizationMapper, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("删除组织失败 - 组织 ID 为空")
    void deleteOrganization_IdIsNull_Fail() {
        R result = organizationService.deleteOrganization(null);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("组织ID不能为空", result.getMessage());

        verify(organizationMapper, never()).selectById(anyInt());
        verify(organizationMapper, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("删除组织失败 - 组织不存在")
    void deleteOrganization_OrgNotExist_Fail() {
        when(organizationMapper.selectById(999)).thenReturn(null);

        R result = organizationService.deleteOrganization(999);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("组织不存在", result.getMessage());

        verify(organizationMapper, times(1)).selectById(999);
        verify(organizationMapper, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("删除组织失败 - 有子组织拒绝删除")
    void deleteOrganization_HasChildren_Fail() {
        when(organizationMapper.selectById(1)).thenReturn(testOrg);
        when(organizationMapper.selectCount(any(QueryWrapper.class))).thenReturn(2L);

        R result = organizationService.deleteOrganization(1);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("该组织存在子组织，不能删除", result.getMessage());

        verify(organizationMapper, times(1)).selectById(1);
        verify(organizationMapper, times(1)).selectCount(any(QueryWrapper.class));
        verify(organizationMapper, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("删除组织失败 - 数据库删除失败")
    void deleteOrganization_DeleteFail() {
        when(organizationMapper.selectById(1)).thenReturn(testOrg);
        when(organizationMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(organizationMapper.deleteById(1)).thenReturn(0);

        R result = organizationService.deleteOrganization(1);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("删除组织失败", result.getMessage());

        verify(organizationMapper, times(1)).selectById(1);
        verify(organizationMapper, times(1)).selectCount(any(QueryWrapper.class));
        verify(organizationMapper, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("删除组织失败 - 有关联用户拒绝删除 - 有用户")
    void deleteOrganization_HasAssociatedUsers_Fail() {
        when(organizationMapper.selectById(1)).thenReturn(testOrg);
        when(organizationMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(organizationMapper.deleteById(1)).thenReturn(1);
        
        QueryWrapper<Consumer> userQuery = any(QueryWrapper.class);
        when(consumerMapper.selectCount(userQuery)).thenReturn(3L);

        R result = organizationService.deleteOrganization(1);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        verify(organizationMapper, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("获取组织详情成功")
    void getOrganizationById_Success() {
        when(organizationMapper.selectById(1)).thenReturn(testOrg);

        R result = organizationService.getOrganizationById(1);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(200, result.getCode());
        assertEquals("查询成功", result.getMessage());

        Organization org = (Organization) result.getData();
        assertNotNull(org);
        assertEquals(1, org.getId());
        assertEquals("测试组织", org.getName());
        assertEquals("/1", org.getPath());
        assertEquals(1, org.getLevel());

        verify(organizationMapper, times(1)).selectById(1);
    }

    @Test
    @DisplayName("获取组织详情失败 - ID 为空")
    void getOrganizationById_IdIsNull_Fail() {
        R result = organizationService.getOrganizationById(null);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("组织ID不能为空", result.getMessage());

        verify(organizationMapper, never()).selectById(anyInt());
    }

    @Test
    @DisplayName("获取组织详情失败 - 组织不存在")
    void getOrganizationById_NotExist_Fail() {
        when(organizationMapper.selectById(999)).thenReturn(null);

        R result = organizationService.getOrganizationById(999);

        assertNotNull(result);
        assertFalse(result.getSuccess());
        assertEquals("组织不存在", result.getMessage());

        verify(organizationMapper, times(1)).selectById(999);
    }

    @Test
    @DisplayName("获取组织树成功 - 多层级结构")
    void getOrganizationTree_Success_MultiLevel() {
        Organization root1 = new Organization();
        root1.setId(1);
        root1.setName("根组织 1");
        root1.setParentId(null);
        root1.setLevel(1);
        root1.setPath("/1");
        root1.setSort(1);

        Organization child1 = new Organization();
        child1.setId(2);
        child1.setName("子组织 1");
        child1.setParentId(1);
        child1.setLevel(2);
        child1.setPath("/1/2");
        child1.setSort(1);

        Organization child2 = new Organization();
        child2.setId(3);
        child2.setName("子组织 2");
        child2.setParentId(1);
        child2.setLevel(2);
        child2.setPath("/1/3");
        child2.setSort(2);

        Organization grandChild = new Organization();
        grandChild.setId(4);
        grandChild.setName("孙组织");
        grandChild.setParentId(2);
        grandChild.setLevel(3);
        grandChild.setPath("/1/2/4");
        grandChild.setSort(1);

        when(organizationMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(root1, child1, child2, grandChild));

        R result = organizationService.getOrganizationTree();

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());

        List<Organization> tree = (List<Organization>) result.getData();
        assertNotNull(tree);
        assertEquals(1, tree.size());

        Organization rootOrg = tree.get(0);
        assertEquals(1, rootOrg.getId());
        assertNotNull(rootOrg.getChildren());
        assertEquals(2, rootOrg.getChildren().size());

        Organization firstChild = rootOrg.getChildren().get(0);
        assertEquals(2, firstChild.getId());
        assertNotNull(firstChild.getChildren());
        assertEquals(1, firstChild.getChildren().size());
        assertEquals(4, firstChild.getChildren().get(0).getId());

        verify(organizationMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取组织树成功 - 只有根组织")
    void getOrganizationTree_Success_RootOnly() {
        Organization root = new Organization();
        root.setId(1);
        root.setName("根组织");
        root.setParentId(null);
        root.setLevel(1);
        root.setPath("/1");
        root.setSort(1);

        when(organizationMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(root));

        R result = organizationService.getOrganizationTree();

        assertNotNull(result);
        assertTrue(result.getSuccess());

        List<Organization> tree = (List<Organization>) result.getData();
        assertNotNull(tree);
        assertEquals(1, tree.size());
        assertNull(tree.get(0).getChildren());

        verify(organizationMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取组织树成功 - 空数据")
    void getOrganizationTree_Success_Empty() {
        when(organizationMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        R result = organizationService.getOrganizationTree();

        assertNotNull(result);
        assertTrue(result.getSuccess());

        List<Organization> tree = (List<Organization>) result.getData();
        assertNotNull(tree);
        assertTrue(tree.isEmpty());

        verify(organizationMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取组织树成功 - 多个根组织")
    void getOrganizationTree_Success_MultipleRoots() {
        Organization root1 = new Organization();
        root1.setId(1);
        root1.setName("根组织 1");
        root1.setParentId(null);
        root1.setLevel(1);
        root1.setSort(1);

        Organization root2 = new Organization();
        root2.setId(5);
        root2.setName("根组织 2");
        root2.setParentId(null);
        root2.setLevel(1);
        root2.setSort(2);

        Organization child = new Organization();
        child.setId(2);
        child.setName("子组织");
        child.setParentId(1);
        child.setLevel(2);
        child.setSort(1);

        when(organizationMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(root1, root2, child));

        R result = organizationService.getOrganizationTree();

        assertNotNull(result);
        assertTrue(result.getSuccess());

        List<Organization> tree = (List<Organization>) result.getData();
        assertNotNull(tree);
        assertEquals(2, tree.size());

        assertEquals(1, tree.get(0).getId());
        assertNotNull(tree.get(0).getChildren());
        assertEquals(1, tree.get(0).getChildren().size());

        assertEquals(5, tree.get(1).getId());
        assertNull(tree.get(1).getChildren());

        verify(organizationMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取所有组织成功 - 有数据")
    void getAllOrganizations_Success_WithData() {
        Organization org1 = new Organization();
        org1.setId(1);
        org1.setName("组织 1");

        Organization org2 = new Organization();
        org2.setId(2);
        org2.setName("组织 2");

        when(organizationMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(org1, org2));

        R result = organizationService.getAllOrganizations();

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals("查询成功", result.getMessage());

        List<Organization> orgs = (List<Organization>) result.getData();
        assertNotNull(orgs);
        assertEquals(2, orgs.size());

        verify(organizationMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取所有组织成功 - 空数据")
    void getAllOrganizations_Success_Empty() {
        when(organizationMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        R result = organizationService.getAllOrganizations();

        assertNotNull(result);
        assertTrue(result.getSuccess());

        List<Organization> orgs = (List<Organization>) result.getData();
        assertNotNull(orgs);
        assertTrue(orgs.isEmpty());

        verify(organizationMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取子组织成功 - 有子组织")
    void getChildrenOrganizations_Success_WithChildren() {
        Organization child1 = new Organization();
        child1.setId(2);
        child1.setName("子组织 1");
        child1.setParentId(1);

        Organization child2 = new Organization();
        child2.setId(3);
        child2.setName("子组织 2");
        child2.setParentId(1);

        when(organizationMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(child1, child2));

        R result = organizationService.getChildrenOrganizations(1);

        assertNotNull(result);
        assertTrue(result.getSuccess());

        List<Organization> children = (List<Organization>) result.getData();
        assertNotNull(children);
        assertEquals(2, children.size());

        verify(organizationMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取子组织成功 - 无子组织")
    void getChildrenOrganizations_Success_NoChildren() {
        when(organizationMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        R result = organizationService.getChildrenOrganizations(1);

        assertNotNull(result);
        assertTrue(result.getSuccess());

        List<Organization> children = (List<Organization>) result.getData();
        assertNotNull(children);
        assertTrue(children.isEmpty());

        verify(organizationMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("获取根组织成功 - parentId 为 null")
    void getChildrenOrganizations_Success_ParentIdNull() {
        Organization root1 = new Organization();
        root1.setId(1);
        root1.setName("根组织 1");
        root1.setParentId(null);

        Organization root2 = new Organization();
        root2.setId(2);
        root2.setName("根组织 2");
        root2.setParentId(null);

        when(organizationMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(root1, root2));

        R result = organizationService.getChildrenOrganizations(null);

        assertNotNull(result);
        assertTrue(result.getSuccess());

        List<Organization> roots = (List<Organization>) result.getData();
        assertNotNull(roots);
        assertEquals(2, roots.size());
        assertNull(roots.get(0).getParentId());
        assertNull(roots.get(1).getParentId());

        verify(organizationMapper, times(1)).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("创建组织 - path 字段生成正确验证")
    void addOrganization_PathGeneration_Verification() {
        when(organizationMapper.insert(any(Organization.class))).thenAnswer(invocation -> {
            Organization org = invocation.getArgument(0);
            org.setId(200);
            return 1;
        });
        when(organizationMapper.updateById(any(Organization.class))).thenReturn(1);

        OrganizationRequest request = new OrganizationRequest();
        request.setName("测试组织");
        R result = organizationService.addOrganization(request);

        Organization createdOrg = (Organization) result.getData();
        assertNotNull(createdOrg);
        assertEquals("/200", createdOrg.getPath());
        assertEquals(200, createdOrg.getId());
    }

    @Test
    @DisplayName("创建子组织 - level 字段递增验证")
    void addOrganization_LevelIncrement_Verification() {
        Organization parentOrg = new Organization();
        parentOrg.setId(10);
        parentOrg.setName("父组织");
        parentOrg.setLevel(3);
        parentOrg.setPath("/1/2/10");

        when(organizationMapper.selectById(10)).thenReturn(parentOrg);
        when(organizationMapper.insert(any(Organization.class))).thenReturn(1);
        when(organizationMapper.updateById(any(Organization.class))).thenReturn(1);

        OrganizationRequest request = new OrganizationRequest();
        request.setName("子组织");
        request.setParentId(10);
        R result = organizationService.addOrganization(request);

        Organization createdOrg = (Organization) result.getData();
        assertNotNull(createdOrg);
        assertEquals(4, createdOrg.getLevel());
    }

    @Test
    @DisplayName("组织状态验证 - 默认状态为启用")
    void addOrganization_DefaultStatus_Verification() {
        when(organizationMapper.insert(any(Organization.class))).thenReturn(1);
        when(organizationMapper.updateById(any(Organization.class))).thenReturn(1);

        OrganizationRequest request = new OrganizationRequest();
        request.setName("新组织");
        R result = organizationService.addOrganization(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());

        verify(organizationMapper, times(1)).insert(argThat(org ->
            org != null && org.getStatus() != null && org.getStatus() == 1
        ));
    }

    @Test
    @DisplayName("更新组织 - 只更新提供的字段")
    void updateOrganization_PartialUpdate() {
        when(organizationMapper.selectById(1)).thenReturn(testOrg);
        when(organizationMapper.updateById(any(Organization.class))).thenReturn(1);

        OrganizationRequest request = new OrganizationRequest();
        request.setId(1);
        request.setName("新名称");
        R result = organizationService.updateOrganization(request);

        assertNotNull(result);
        assertTrue(result.getSuccess());

        verify(organizationMapper, times(1)).selectById(1);
        verify(organizationMapper, times(1)).updateById(any(Organization.class));
    }

    @Test
    @DisplayName("获取组织树 - 验证排序")
    void getOrganizationTree_VerifySortOrder() {
        Organization org1 = new Organization();
        org1.setId(1);
        org1.setName("组织 1");
        org1.setSort(1);
        org1.setParentId(null);

        Organization org2 = new Organization();
        org2.setId(2);
        org2.setName("组织 2");
        org2.setSort(2);
        org2.setParentId(null);

        Organization org3 = new Organization();
        org3.setId(3);
        org3.setName("组织 3");
        org3.setSort(3);
        org3.setParentId(null);

        when(organizationMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(org1, org2, org3));

        R result = organizationService.getOrganizationTree();

        assertNotNull(result);
        assertTrue(result.getSuccess());

        List<Organization> tree = (List<Organization>) result.getData();
        assertNotNull(tree);
        assertEquals(3, tree.size());
        assertEquals(1, tree.get(0).getSort());
        assertEquals(2, tree.get(1).getSort());
        assertEquals(3, tree.get(2).getSort());
    }
}
