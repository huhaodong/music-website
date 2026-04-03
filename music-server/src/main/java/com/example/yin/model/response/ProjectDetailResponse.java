package com.example.yin.model.response;

import com.example.yin.model.domain.Song;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProjectDetailResponse {
    private Map<String, Object> project;
    private List<Map<String, Object>> artists;
    private List<Song> songs;
}
