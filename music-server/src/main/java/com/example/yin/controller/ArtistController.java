package com.example.yin.controller;

import com.example.yin.annotation.RequirePermission;
import com.example.yin.common.R;
import com.example.yin.model.request.ArtistCreateRequest;
import com.example.yin.model.request.ArtistUpdateRequest;
import com.example.yin.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/api/artist")
@Validated
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    @GetMapping
    @RequirePermission(codes = {"system:artist:query", "artist:list"})
    public R pageArtists(@RequestParam(required = false) @Size(max = 32) String type,
                         @RequestParam(required = false) @Size(max = 200) String keyword,
                         @RequestParam(required = false) @Min(1) Integer page,
                         @RequestParam(required = false) @Min(1) Integer size) {
        return artistService.pageArtists(type, keyword, page, size);
    }

    @GetMapping("/{id}")
    @RequirePermission(codes = {"system:artist:query", "artist:detail"})
    public R getArtist(@PathVariable("id") @Min(1) Integer id) {
        return artistService.getArtist(id);
    }

    @PostMapping
    @RequirePermission(codes = {"system:artist:add", "artist:create"})
    public R createArtist(@Valid @RequestBody ArtistCreateRequest request) {
        return artistService.createArtist(request);
    }

    @PutMapping("/{id}")
    @RequirePermission(codes = {"system:artist:update", "artist:update", "artist:edit"})
    public R updateArtist(@PathVariable("id") @Min(1) Integer id,
                          @Valid @RequestBody ArtistUpdateRequest request) {
        return artistService.updateArtist(id, request);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(codes = {"system:artist:delete", "artist:delete"})
    public R deleteArtist(@PathVariable("id") @Min(1) Integer id) {
        return artistService.deleteArtist(id);
    }

    @GetMapping("/{id}/projects")
    @RequirePermission(codes = {"system:artist:query", "artist:projects"})
    public R listAssociatedProjects(@PathVariable("id") @Min(1) Integer id) {
        return artistService.listAssociatedProjects(id);
    }
}
