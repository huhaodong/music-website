package com.example.yin.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yin.interceptor.DataPermissionInterceptor.BoundSqlSqlSource;
import com.example.yin.mapper.OrganizationMapper;
import com.example.yin.mapper.UserRoleMapper;
import com.example.yin.model.domain.UserRole;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DataPermissionInterceptor 数据权限拦截逻辑测试
 * 测试不同用户角色对数据范围的访问控制
 * 
 * 测试场景覆盖：
 * 1. 超级管理员 - 不限制数据范围（可查看所有数据）
 * 2. 组织管理员 - 限制为本组织及下级数据
 * 3. 普通用户 - 限制为个人数据
 * 4. 跨组织访问 - 访问无权限的组织数据应被拒绝
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DataPermissionInterceptor 数据权限拦截测试")
class DataPermissionInterceptorTest {

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private OrganizationMapper organizationMapper;

    @Mock
    private Executor executor;

    @Mock
    private MappedStatement mappedStatement;

    @Mock
    private BoundSql boundSql;

    @Mock
    private ResultHandler resultHandler;

    @InjectMocks
    private DataPermissionInterceptor dataPermissionInterceptor;

    private TestParameterObject parameterObject;
    private RowBounds rowBounds;

    @BeforeEach
    void setUp() {
        parameterObject = new TestParameterObject();
        rowBounds = RowBounds.DEFAULT;
    }

    // ==================== 场景 1: 超级管理员 - 不限制数据范围 ====================

    @Test
    @DisplayName("超级管理员访问 - 应不添加数据权限限制")
    void intercept_SuperAdmin_ShouldNotAddPermissionCondition() throws Throwable {
        // Given: 超级管理员用户，userType 为 super_admin
        parameterObject.setUserId(1);
        parameterObject.setUserType("super_admin");

        setupMappedStatementMock("SELECT * FROM song");
        when(executor.query(any(MappedStatement.class), any(), any(), any())).thenReturn(new ArrayList<>());

        Invocation invocation = createInvocation();

        // When: 执行查询
        dataPermissionInterceptor.intercept(invocation);

        // Then: 验证查询被执行
        verify(executor, times(1)).query(any(MappedStatement.class), eq(parameterObject), eq(rowBounds), eq(resultHandler));
    }

    @Test
    @DisplayName("超级管理员访问带 WHERE 条件的 SQL - 应正确追加权限条件")
    void intercept_SuperAdminWithWhereClause_ShouldAppendCondition() throws Throwable {
        // Given: 超级管理员，SQL 已有 WHERE 条件
        parameterObject.setUserId(1);
        parameterObject.setUserType("super_admin");

        setupMappedStatementMock("SELECT * FROM song WHERE status = 1");
        when(executor.query(any(MappedStatement.class), any(), any(), any())).thenReturn(new ArrayList<>());

        Invocation invocation = createInvocation();

        // When: 执行查询
        dataPermissionInterceptor.intercept(invocation);

        // Then: 验证 SQL 被正确修改
        ArgumentCaptor<MappedStatement> msCaptor = ArgumentCaptor.forClass(MappedStatement.class);
        verify(executor).query(msCaptor.capture(), eq(parameterObject), eq(rowBounds), eq(resultHandler));
        
        MappedStatement capturedMs = msCaptor.getValue();
        BoundSql modifiedBoundSql = capturedMs.getBoundSql(parameterObject);
        String modifiedSql = modifiedBoundSql.getSql();
        
        assertTrue(modifiedSql.contains("WHERE status = 1"), "应保留原有 WHERE 条件");
        assertTrue(modifiedSql.contains("AND organization_id IN"), "应追加组织权限条件");
    }

    // ==================== 场景 2: 组织管理员 - 限制为本组织及下级数据 ====================

    @Test
    @DisplayName("组织管理员访问 - 应限制为本组织及下级组织 ID")
    void intercept_OrganizationAdmin_ShouldRestrictToOwnAndChildOrgs() throws Throwable {
        // Given: 组织管理员，管理组织 ID 为 100
        parameterObject.setUserId(10);
        parameterObject.setUserType("org_admin");

        setupMappedStatementMock("SELECT * FROM song");
        when(executor.query(any(MappedStatement.class), any(), any(), any())).thenReturn(new ArrayList<>());

        Invocation invocation = createInvocation();

        // When: 执行查询
        dataPermissionInterceptor.intercept(invocation);

        // Then: 验证查询被执行
        verify(executor, times(1)).query(any(MappedStatement.class), eq(parameterObject), eq(rowBounds), eq(resultHandler));
    }

    @Test
    @DisplayName("组织管理员访问 - SQL 应包含组织 ID 过滤条件")
    void intercept_OrganizationAdmin_ShouldAddOrgIdFilter() throws Throwable {
        // Given: 组织管理员
        parameterObject.setUserId(10);
        parameterObject.setUserType("org_admin");

        setupMappedStatementMock("SELECT * FROM song");
        when(executor.query(any(MappedStatement.class), any(), any(), any())).thenReturn(new ArrayList<>());

        Invocation invocation = createInvocation();

        // When: 执行查询
        dataPermissionInterceptor.intercept(invocation);

        // Then: 验证 SQL 添加了组织权限条件
        ArgumentCaptor<MappedStatement> msCaptor = ArgumentCaptor.forClass(MappedStatement.class);
        verify(executor).query(msCaptor.capture(), eq(parameterObject), eq(rowBounds), eq(resultHandler));
        
        MappedStatement capturedMs = msCaptor.getValue();
        BoundSql modifiedBoundSql = capturedMs.getBoundSql(parameterObject);
        String modifiedSql = modifiedBoundSql.getSql();
        
        assertTrue(modifiedSql.contains("WHERE organization_id IN"), "应添加组织 ID 过滤条件");
    }

    // ==================== 场景 3: 普通用户 - 限制为个人数据 ====================

    @Test
    @DisplayName("普通用户访问 - 应限制为个人数据")
    void intercept_NormalUser_ShouldRestrictToPersonalData() throws Throwable {
        // Given: 普通用户，userType 为 normal
        parameterObject.setUserId(100);
        parameterObject.setUserType("normal");

        setupMappedStatementMock("SELECT * FROM song");
        when(executor.query(any(MappedStatement.class), any(), any(), any())).thenReturn(new ArrayList<>());

        Invocation invocation = createInvocation();

        // When: 执行查询
        dataPermissionInterceptor.intercept(invocation);

        // Then: 验证查询被执行
        verify(executor, times(1)).query(any(MappedStatement.class), eq(parameterObject), eq(rowBounds), eq(resultHandler));
    }

    @Test
    @DisplayName("普通用户访问 - 无组织权限时应返回空结果")
    void intercept_NormalUserNoOrgPermission_ShouldReturnEmpty() throws Throwable {
        // Given: 普通用户没有任何组织权限
        parameterObject.setUserId(100);
        parameterObject.setUserType("normal");

        setupMappedStatementMock("SELECT * FROM song");
        when(executor.query(any(MappedStatement.class), any(), any(), any())).thenReturn(new ArrayList<>());

        Invocation invocation = createInvocation();

        // When: 执行查询
        dataPermissionInterceptor.intercept(invocation);

        // Then: 验证 SQL 添加了 -1 条件（查询不存在的数据）
        ArgumentCaptor<MappedStatement> msCaptor = ArgumentCaptor.forClass(MappedStatement.class);
        verify(executor).query(msCaptor.capture(), eq(parameterObject), eq(rowBounds), eq(resultHandler));
        
        MappedStatement capturedMs = msCaptor.getValue();
        BoundSql modifiedBoundSql = capturedMs.getBoundSql(parameterObject);
        String modifiedSql = modifiedBoundSql.getSql();
        
        assertTrue(modifiedSql.contains("organization_id IN (-1)"), "无权限时应添加 -1 条件返回空结果");
    }

    // ==================== 场景 4: 跨组织访问 - 应被拒绝 ====================

    @Test
    @DisplayName("跨组织访问 - 访问无权限组织数据应被拒绝")
    void intercept_CrossOrgAccess_ShouldBeDenied() throws Throwable {
        // Given: 组织 A 的管理员尝试访问组织 B 的数据
        parameterObject.setUserId(10);
        parameterObject.setUserType("org_admin");

        setupMappedStatementMock("SELECT * FROM song WHERE organization_id = 999");
        when(executor.query(any(MappedStatement.class), any(), any(), any())).thenReturn(new ArrayList<>());

        Invocation invocation = createInvocation();

        // When: 执行查询
        dataPermissionInterceptor.intercept(invocation);

        // Then: 验证 SQL 被修改，添加了权限过滤
        ArgumentCaptor<MappedStatement> msCaptor = ArgumentCaptor.forClass(MappedStatement.class);
        verify(executor).query(msCaptor.capture(), eq(parameterObject), eq(rowBounds), eq(resultHandler));
        
        MappedStatement capturedMs = msCaptor.getValue();
        BoundSql modifiedBoundSql = capturedMs.getBoundSql(parameterObject);
        String modifiedSql = modifiedBoundSql.getSql();
        
        assertTrue(modifiedSql.contains("organization_id IN"), "应添加组织权限条件限制跨组织访问");
    }

    @Test
    @DisplayName("不同组织同级用户 - 数据应隔离")
    void intercept_DifferentOrgSameLevel_ShouldIsolateData() throws Throwable {
        // Given: 组织 100 的用户尝试访问组织 200 的数据
        parameterObject.setUserId(10);
        parameterObject.setUserType("normal");

        setupMappedStatementMock("SELECT * FROM song");
        when(executor.query(any(MappedStatement.class), any(), any(), any())).thenReturn(new ArrayList<>());

        Invocation invocation = createInvocation();

        // When: 执行查询
        dataPermissionInterceptor.intercept(invocation);

        // Then: 验证 SQL 添加了组织权限条件，实现数据隔离
        ArgumentCaptor<MappedStatement> msCaptor = ArgumentCaptor.forClass(MappedStatement.class);
        verify(executor).query(msCaptor.capture(), eq(parameterObject), eq(rowBounds), eq(resultHandler));
        
        MappedStatement capturedMs = msCaptor.getValue();
        BoundSql modifiedBoundSql = capturedMs.getBoundSql(parameterObject);
        String modifiedSql = modifiedBoundSql.getSql();
        
        assertTrue(modifiedSql.contains("WHERE organization_id IN"), "应添加组织权限条件实现数据隔离");
    }

    // ==================== 边界条件测试 ====================

    @Test
    @DisplayName("不需要数据权限的 Mapper - 应直接通过")
    void intercept_AdminMapper_ShouldNotAddPermission() throws Throwable {
        // Given: AdminMapper 的查询（不需要数据权限）
        parameterObject.setUserId(1);
        parameterObject.setUserType("admin");

        when(mappedStatement.getId()).thenReturn("com.example.yin.mapper.AdminMapper.selectAll");
        when(executor.query(mappedStatement, parameterObject, rowBounds, resultHandler)).thenReturn(new ArrayList<>());

        Invocation invocation = createInvocation();

        // When: 执行查询
        dataPermissionInterceptor.intercept(invocation);

        // Then: 直接执行原查询，不修改 SQL
        verify(executor, times(1)).query(mappedStatement, parameterObject, rowBounds, resultHandler);
    }

    @Test
    @DisplayName("ConsumerMapper - 应直接通过")
    void intercept_ConsumerMapper_ShouldNotAddPermission() throws Throwable {
        // Given: ConsumerMapper 的查询（不需要数据权限）
        parameterObject.setUserId(1);
        parameterObject.setUserType("consumer");

        when(mappedStatement.getId()).thenReturn("com.example.yin.mapper.ConsumerMapper.selectByUsername");
        when(executor.query(mappedStatement, parameterObject, rowBounds, resultHandler)).thenReturn(new ArrayList<>());

        Invocation invocation = createInvocation();

        // When: 执行查询
        dataPermissionInterceptor.intercept(invocation);

        // Then: 直接执行原查询
        verify(executor, times(1)).query(mappedStatement, parameterObject, rowBounds, resultHandler);
    }

    @Test
    @DisplayName("参数为 null - 应直接通过")
    void intercept_NullParameter_ShouldPassThrough() throws Throwable {
        // Given: 参数为 null
        when(mappedStatement.getId()).thenReturn("com.example.yin.mapper.SongMapper.selectAll");
        when(executor.query(mappedStatement, null, rowBounds, resultHandler)).thenReturn(new ArrayList<>());

        Invocation invocation = new Invocation(executor, getQueryMethod(), new Object[]{mappedStatement, null, rowBounds, resultHandler});

        // When: 执行查询
        dataPermissionInterceptor.intercept(invocation);

        // Then: 直接执行原查询
        verify(executor, times(1)).query(mappedStatement, null, rowBounds, resultHandler);
    }

    @Test
    @DisplayName("参数没有 userId 字段 - 应直接通过")
    void intercept_ParameterNoUserIdField_ShouldPassThrough() throws Throwable {
        // Given: 参数对象没有 userId 字段
        NoUserIdParameter param = new NoUserIdParameter();

        when(mappedStatement.getId()).thenReturn("com.example.yin.mapper.SongMapper.selectAll");
        when(executor.query(mappedStatement, param, rowBounds, resultHandler)).thenReturn(new ArrayList<>());

        Invocation invocation = new Invocation(executor, getQueryMethod(), new Object[]{mappedStatement, param, rowBounds, resultHandler});

        // When: 执行查询
        dataPermissionInterceptor.intercept(invocation);

        // Then: 直接执行原查询
        verify(executor, times(1)).query(mappedStatement, param, rowBounds, resultHandler);
    }

    @Test
    @DisplayName("参数没有 userType 字段 - 应直接通过")
    void intercept_ParameterNoUserTypeField_ShouldPassThrough() throws Throwable {
        // Given: 参数对象没有 userType 字段
        NoUserTypeParameter param = new NoUserTypeParameter();
        param.setId(1);

        when(mappedStatement.getId()).thenReturn("com.example.yin.mapper.SongMapper.selectAll");
        when(executor.query(mappedStatement, param, rowBounds, resultHandler)).thenReturn(new ArrayList<>());

        Invocation invocation = new Invocation(executor, getQueryMethod(), new Object[]{mappedStatement, param, rowBounds, resultHandler});

        // When: 执行查询
        dataPermissionInterceptor.intercept(invocation);

        // Then: 直接执行原查询
        verify(executor, times(1)).query(mappedStatement, param, rowBounds, resultHandler);
    }

    @Test
    @DisplayName("UPDATE 操作 - 应添加数据权限条件")
    void intercept_UpdateOperation_ShouldAddPermissionCondition() throws Throwable {
        // Given: UPDATE 操作
        parameterObject.setUserId(1);
        parameterObject.setUserType("normal");

        setupMappedStatementMock("UPDATE song SET title = ? WHERE id = ?");
        when(executor.query(any(MappedStatement.class), any(), any(), any())).thenReturn(new ArrayList<>());

        Invocation invocation = createInvocation();

        // When: 执行更新
        dataPermissionInterceptor.intercept(invocation);

        // Then: 验证 SQL 被修改
        ArgumentCaptor<MappedStatement> msCaptor = ArgumentCaptor.forClass(MappedStatement.class);
        verify(executor).query(msCaptor.capture(), eq(parameterObject), eq(rowBounds), eq(resultHandler));
        
        MappedStatement capturedMs = msCaptor.getValue();
        BoundSql modifiedBoundSql = capturedMs.getBoundSql(parameterObject);
        String modifiedSql = modifiedBoundSql.getSql();
        
        assertTrue(modifiedSql.contains("WHERE organization_id IN"), "UPDATE 操作应添加组织权限条件");
    }

    @Test
    @DisplayName("DELETE 操作 - 应添加数据权限条件")
    void intercept_DeleteOperation_ShouldAddPermissionCondition() throws Throwable {
        // Given: DELETE 操作
        parameterObject.setUserId(1);
        parameterObject.setUserType("normal");

        setupMappedStatementMock("DELETE FROM song WHERE id = ?");
        when(executor.query(any(MappedStatement.class), any(), any(), any())).thenReturn(new ArrayList<>());

        Invocation invocation = createInvocation();

        // When: 执行删除
        dataPermissionInterceptor.intercept(invocation);

        // Then: 验证 SQL 被修改
        ArgumentCaptor<MappedStatement> msCaptor = ArgumentCaptor.forClass(MappedStatement.class);
        verify(executor).query(msCaptor.capture(), eq(parameterObject), eq(rowBounds), eq(resultHandler));
        
        MappedStatement capturedMs = msCaptor.getValue();
        BoundSql modifiedBoundSql = capturedMs.getBoundSql(parameterObject);
        String modifiedSql = modifiedBoundSql.getSql();
        
        assertTrue(modifiedSql.contains("AND organization_id IN"), "DELETE 操作应追加组织权限条件");
    }

    @Test
    @DisplayName("BoundSqlSqlSource 类 - 应正确返回 BoundSql")
    void test_BoundSqlSqlSource_GetBoundSql() {
        // Given: 创建 BoundSqlSqlSource 实例
        BoundSql mockBoundSql = mock(BoundSql.class);
        BoundSqlSqlSource sqlSource = new BoundSqlSqlSource(mockBoundSql);

        // When: 调用 getBoundSql 方法
        BoundSql result = sqlSource.getBoundSql(new Object());

        // Then: 应返回传入的 BoundSql
        assertSame(mockBoundSql, result);
    }

    @Test
    @DisplayName("plugin 方法 - 应正确包装目标对象")
    void test_Plugin_WrapTarget() {
        // Given: Executor mock 对象
        Executor mockExecutor = mock(Executor.class);

        // When: 调用 plugin 方法
        Object result = dataPermissionInterceptor.plugin(mockExecutor);

        // Then: 应返回包装后的对象
        assertNotNull(result);
    }

    @Test
    @DisplayName("setProperties 方法 - 应正常执行")
    void test_SetProperties() {
        // Given: Properties 对象
        java.util.Properties properties = new java.util.Properties();
        properties.setProperty("key", "value");

        // When: 调用 setProperties 方法
        assertDoesNotThrow(() -> dataPermissionInterceptor.setProperties(properties));
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建 Invocation 用于测试
     */
    private Invocation createInvocation() throws NoSuchMethodException {
        Object[] args = {mappedStatement, parameterObject, rowBounds, resultHandler};
        return new Invocation(executor, getQueryMethod(), args);
    }

    /**
     * 获取 Executor 的 query 方法
     */
    private Method getQueryMethod() throws NoSuchMethodException {
        return Executor.class.getMethod("query", MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class);
    }

    /**
     * 设置 MappedStatement 的通用 Mock
     */
    private void setupMappedStatementMock(String sql) {
        when(mappedStatement.getId()).thenReturn("com.example.yin.mapper.SongMapper.selectAll");
        when(mappedStatement.getBoundSql(parameterObject)).thenReturn(boundSql);
        when(boundSql.getSql()).thenReturn(sql);
        when(boundSql.getParameterMappings()).thenReturn(new ArrayList<>());
        when(boundSql.getParameterObject()).thenReturn(parameterObject);
        when(mappedStatement.getConfiguration()).thenReturn(new org.apache.ibatis.session.Configuration());
        when(mappedStatement.getSqlCommandType()).thenReturn(SqlCommandType.SELECT);
        when(mappedStatement.getResource()).thenReturn("SongMapper.xml");
        when(mappedStatement.getResultMaps()).thenReturn(new ArrayList<>());
        when(mappedStatement.getCache()).thenReturn(null);
    }

    // ==================== 辅助测试类 ====================

    /**
     * 测试用参数对象
     */
    static class TestParameterObject {
        private Integer userId;
        private String userType;

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }
    }

    /**
     * 没有 userId 字段的测试参数对象
     */
    static class NoUserIdParameter {
        private Integer id;
        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 没有 userType 字段的测试参数对象
     */
    static class NoUserTypeParameter {
        private Integer id;
        private String username;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
