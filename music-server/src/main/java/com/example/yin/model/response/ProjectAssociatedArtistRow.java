package com.example.yin.model.response;

import lombok.Data;

import java.util.List;

@Data
public class ProjectAssociatedArtistRow {
    private Integer id;
    private String name;
    private List<String> types;
    private String pic;
    private String role;
}

