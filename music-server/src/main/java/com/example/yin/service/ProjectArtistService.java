package com.example.yin.service;

import com.example.yin.common.R;

import java.util.List;

public interface ProjectArtistService {

    R listProjectArtists(Integer projectId);

    R bindArtistRoles(Integer projectId, Integer artistId, List<String> roles);

    R unbindArtistRole(Integer projectId, Integer artistId, String role);
}

