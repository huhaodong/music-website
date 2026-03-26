package com.example.yin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.yin.common.R;
import com.example.yin.model.domain.Organization;
import com.example.yin.model.request.OrganizationRequest;

import java.util.List;

public interface OrganizationService extends IService<Organization> {

    R addOrganization(OrganizationRequest organizationRequest);

    R updateOrganization(OrganizationRequest organizationRequest);

    R deleteOrganization(Integer id);

    R getOrganizationById(Integer id);

    R getAllOrganizations();

    R getOrganizationTree();

    R getChildrenOrganizations(Integer parentId);
}