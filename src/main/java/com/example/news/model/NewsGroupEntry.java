package com.example.news.model;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
public class NewsGroupEntry {
    private int groupSize;

    private List<Articles> groupEntries;

    private Object groupName;
}
