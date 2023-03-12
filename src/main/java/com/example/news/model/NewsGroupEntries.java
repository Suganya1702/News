package com.example.news.model;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
public class NewsGroupEntries {

    private List<NewsGroupEntry> newsGroupEntry;

    private String groupedBy;
}
