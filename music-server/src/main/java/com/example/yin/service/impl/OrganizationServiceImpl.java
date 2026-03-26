package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.mapper.OrganizationMapper;
import com.example.yin.model.domain.Organization;
import com.example.yin.model.request.OrganizationRequest;
import com.example.yin.service.OrganizationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization> implements OrganizationService {

    @Override
    public R addOrganization(OrganizationRequest organizationRequest) {
        if (!StringUtils.hasText(organizationRequest.getName())) {
            return R.error("组织名称不能为空");
        }

        Organization organization = new Organization();
        BeanUtils.copyProperties(organizationRequest, organization);
        organization.setStatus(1);

        if (organization.getParentId() != null) {
            Organization parent = baseMapper.selectById(organization.getParentId());
            if (parent != null) {
                organization.setLevel(parent.getLevel() + 1);
                organization.setPath(parent.getPath() + "/" + organization.getId());
            }
        } else {
            organization.setLevel(1);
            organization.setPath("/");
        }

        if (baseMapper.insert(organization) > 0) {
            if (organization.getParentId() != null) {
                organization.setPath("/" + organization.getParentId() + "/" + organization.getId());
            } else {
                organization.setPath("/" + organization.getId());
            }
            baseMapper.updateById(organization);
            return R.success("添加组织成功", organization);
        }
        return R.error("添加组织失败");
    }

    @Override
    public R updateOrganization(OrganizationRequest organizationRequest) {
        if (organizationRequest.getId() == null) {
            return R.error("组织ID不能为空");
        }

        Organization organization = baseMapper.selectById(organizationRequest.getId());
        if (organization == null) {
            return R.error("组织不存在");
        }

        BeanUtils.copyProperties(organizationRequest, organization);

        if (baseMapper.updateById(organization) > 0) {
            return R.success("更新组织成功", organization);
        }
        return R.error("更新组织失败");
    }

    @Override
    @Transactional
    public R deleteOrganization(Integer id) {
        if (id == null) {
            return R.error("组织ID不能为空");
        }

        Organization organization = baseMapper.selectById(id);
        if (organization == null) {
            return R.error("组织不存在");
        }

        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        if (baseMapper.selectCount(queryWrapper) > 0) {
            return R.error("该组织存在子组织，不能删除");
        }

        if (baseMapper.deleteById(id) > 0) {
            return R.success("删除组织成功");
        }
        return R.error("删除组织失败");
    }

    @Override
    public R getOrganizationById(Integer id) {
        if (id == null) {
            return R.error("组织ID不能为空");
        }

        Organization organization = baseMapper.selectById(id);
        if (organization == null) {
            return R.error("组织不存在");
        }
        return R.success("查询成功", organization);
    }

    @Override
    public R getAllOrganizations() {
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        return R.success("查询成功", baseMapper.selectList(queryWrapper));
    }

    @Override
    public R getOrganizationTree() {
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        List<Organization> allOrganizations = baseMapper.selectList(queryWrapper);

        List<Organization> tree = buildOrganizationTree(allOrganizations, null);
        return R.success("查询成功", tree);
    }

    private List<Organization> buildOrganizationTree(List<Organization> organizations, Integer parentId) {
        return organizations.stream()
                .filter(org -> (parentId == null && org.getParentId() == null) ||
                              (parentId != null && parentId.equals(org.getParentId())))
                .peek(org -> {
                    List<Organization> children = buildOrganizationTree(organizations, org.getId());
                    if (!children.isEmpty()) {
                        org.setChildren(children);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public R getChildrenOrganizations(Integer parentId) {
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        if (parentId == null) {
            queryWrapper.isNull("parent_id");
        } else {
            queryWrapper.eq("parent_id", parentId);
        }
        queryWrapper.orderByAsc("sort");
        return R.success("查询成功", baseMapper.selectList(queryWrapper));
    }
}