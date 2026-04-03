package com.example.yin.controller;

import com.example.yin.annotation.RequirePermission;
import com.example.yin.common.R;
import com.example.yin.model.request.ProjectArtistBindRequest;
import com.example.yin.model.request.ProjectArtistUnbindRequest;
import com.example.yin.model.request.ProjectCreateRequest;
import com.example.yin.model.request.ProjectStatusUpdateRequest;
import com.example.yin.model.request.ProjectUpdateRequest;
import com.example.yin.service.ProjectArtistService;
import com.example.yin.service.ProjectService;
import com.example.yin.service.ProjectStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/api/project")
@Validated
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectStatusService projectStatusService;

    @Autowired
    private ProjectArtistService projectArtistService;

    @GetMapping
    @RequirePermission(codes = {"system:project:query", "project:list"})
    public R pageProjects(@RequestParam(required = false) @Size(max = 32) String status,
                          @RequestParam(required = false) @Min(1) Integer orgId,
                          @RequestParam(required = false) @Size(max = 200) String keyword,
                          @RequestParam(required = false) @Size(max = 32) String sortBy,
                          @RequestParam(required = false) @Size(max = 8) String sortOrder,
                          @RequestParam(required = false) @Min(1) Integer page,
                          @RequestParam(required = false) @Min(1) Integer size) {
        return projectService.pageProjects(status, orgId, keyword, sortBy, sortOrder, page, size);
    }

    @GetMapping("/{id}")
    @RequirePermission(codes = {"system:project:query", "project:detail"})
    public R getProjectDetail(@PathVariable("id") @Min(1) Integer id) {
        return projectService.getProjectDetail(id);
    }

    @PostMapping
    @RequirePermission(codes = {"system:project:add", "project:create"})
    public R createProject(@Valid @RequestBody ProjectCreateRequest request) {
        return projectService.createProject(request);
    }

    @PutMapping("/{id}")
    @RequirePermission(codes = {"system:project:update", "project:update", "project:edit"})
    public R updateProject(@PathVariable("id") @Min(1) Integer id,
                           @Valid @RequestBody ProjectUpdateRequest request) {
        return projectService.updateProject(id, request);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(codes = {"system:project:delete", "project:delete"})
    public R deleteProject(@PathVariable("id") @Min(1) Integer id) {
        return projectService.deleteProject(id);
    }

    @PutMapping("/{id}/status")
    @RequirePermission(codes = {"system:project:status", "project:status"})
    public R changeStatus(@PathVariable("id") @Min(1) Integer id,
                          @Valid @RequestBody ProjectStatusUpdateRequest request) {
        return projectStatusService.changeStatus(id, request.getStatus(), request.getRemark());
    }

    @GetMapping("/{id}/artists")
    @RequirePermission(codes = {"system:project:query", "project:detail"})
    public R listProjectArtists(@PathVariable("id") @Min(1) Integer id) {
        return projectArtistService.listProjectArtists(id);
    }

    @PostMapping("/{id}/artists")
    @RequirePermission(codes = {"system:project:update", "project:artist:bind"})
    public R bindProjectArtists(@PathVariable("id") @Min(1) Integer id,
                                @Valid @RequestBody ProjectArtistBindRequest request) {
        return projectArtistService.bindArtistRoles(id, request.getArtistId(), request.getRoles());
    }

    @DeleteMapping("/{id}/artists")
    @RequirePermission(codes = {"system:project:update", "project:artist:unbind"})
    public R unbindProjectArtist(@PathVariable("id") @Min(1) Integer id,
                                 @Valid @RequestBody ProjectArtistUnbindRequest request) {
        return projectArtistService.unbindArtistRole(id, request.getArtistId(), request.getRole());
    }
}

