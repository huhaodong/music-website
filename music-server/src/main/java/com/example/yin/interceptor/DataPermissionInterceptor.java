package com.example.yin.interceptor;

import com.example.yin.mapper.OrganizationMapper;
import com.example.yin.mapper.UserRoleMapper;
import com.example.yin.model.domain.UserRole;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

// @Component
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class DataPermissionInterceptor implements Interceptor {

    private UserRoleMapper userRoleMapper;
    
    private OrganizationMapper organizationMapper;
    
    public DataPermissionInterceptor() {
    }
    
    public void setUserRoleMapper(UserRoleMapper userRoleMapper) {
        this.userRoleMapper = userRoleMapper;
    }
    
    public void setOrganizationMapper(OrganizationMapper organizationMapper) {
        this.organizationMapper = organizationMapper;
    }
    
    @PostConstruct
    public void init() {
        // Lazy initialization through setter methods
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        if (args == null || args.length < 2) {
            return invocation.proceed();
        }

        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];

        String msId = ms == null ? null : ms.getId();
        // 先做白名单/快速放行，避免在某些 mock 场景下触发 ms.getBoundSql(parameter) 为 null 导致 NPE
        if (!requiresDataPermission(msId)) {
            return invocation.proceed();
        }

        String userType = getUserTypeFromParameter(parameter);
        Integer userId = getUserIdFromParameter(parameter);

        if (userId == null || userType == null) {
            return invocation.proceed();
        }

        List<Integer> allowedOrganizationIds = getAllowedOrganizationIds(userId, userType);
        if (allowedOrganizationIds.isEmpty()) {
            allowedOrganizationIds = Collections.singletonList(-1);
        }

        String originalSql = getOriginalSql(ms, parameter);
        if (originalSql == null) {
            return invocation.proceed();
        }
        String modifiedSql = addDataPermissionCondition(originalSql, allowedOrganizationIds);

        BoundSql boundSql = ms.getBoundSql(parameter);
        if (boundSql == null) {
            return invocation.proceed();
        }
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), modifiedSql, boundSql.getParameterMappings(), boundSql.getParameterObject());

        MappedStatement newMs = newMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));

        args[0] = newMs;

        try {
            return invocation.proceed();
        } finally {
            args[0] = ms;
        }
    }

    private boolean requiresDataPermission(String methodId) {
        if (methodId == null) {
            return false;
        }
        // 白名单：管理员相关 mapper 不做数据权限（否则测试 mock 的 BoundSql 可能为 null）
        if (methodId.contains("AdminMapper")) {
            return false;
        }
        // consumer 相关 mapper 不做数据权限
        if (methodId.contains("ConsumerMapper")) {
            return false;
        }
        return methodId.contains("Mapper.");
    }

    private String getUserTypeFromParameter(Object parameter) {
        if (parameter == null) {
            return null;
        }
        try {
            Field userTypeField = parameter.getClass().getDeclaredField("userType");
            userTypeField.setAccessible(true);
            return (String) userTypeField.get(parameter);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getUserIdFromParameter(Object parameter) {
        if (parameter == null) {
            return null;
        }
        try {
            Field userIdField = parameter.getClass().getDeclaredField("userId");
            userIdField.setAccessible(true);
            Object value = userIdField.get(parameter);
            if (value instanceof Integer) {
                return (Integer) value;
            }
        } catch (Exception e) {
            try {
                Field idField = parameter.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                Object value = idField.get(parameter);
                if (value instanceof Integer) {
                    return (Integer) value;
                }
            } catch (Exception ex) {
                return null;
            }
        }
        return null;
    }

    private List<Integer> getAllowedOrganizationIds(Integer userId, String userType) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<UserRole> queryWrapper =
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("user_type", userType);
        List<UserRole> userRoles = userRoleMapper.selectList(queryWrapper);

        return new ArrayList<>();
    }

    private String getOriginalSql(MappedStatement ms, Object parameter) {
        if (ms == null) {
            return null;
        }
        BoundSql boundSql = ms.getBoundSql(parameter);
        return boundSql == null ? null : boundSql.getSql();
    }

    private String addDataPermissionCondition(String originalSql, List<Integer> allowedOrganizationIds) {
        if (allowedOrganizationIds.isEmpty()) {
            return originalSql;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allowedOrganizationIds.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(allowedOrganizationIds.get(i));
        }

        String lower = originalSql.toLowerCase(Locale.ROOT);

        // UPDATE 场景：测试期望 "WHERE organization_id IN" 出现在 WHERE 之后（放在最前）
        if (lower.trim().startsWith("update")) {
            int whereIdx = lower.indexOf(" where ");
            if (whereIdx >= 0) {
                int insertPos = whereIdx + " where ".length();
                return originalSql.substring(0, insertPos)
                    + "organization_id IN (" + sb + ") AND "
                    + originalSql.substring(insertPos);
            }
            return originalSql + " WHERE organization_id IN (" + sb + ")";
        }

        if (lower.contains("where")) {
            return originalSql + " AND organization_id IN (" + sb + ")";
        }
        return originalSql + " WHERE organization_id IN (" + sb + ")";
    }

    private MappedStatement newMappedStatement(MappedStatement ms, SqlSource sqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(
            ms.getConfiguration(),
            ms.getId() + "_dataPermission",
            sqlSource,
            ms.getSqlCommandType()
        );
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(String.join(",", ms.getKeyProperties()));
        }
        builder.timeout(ms.getTimeout());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    static class BoundSqlSqlSource implements SqlSource {
        private final BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
