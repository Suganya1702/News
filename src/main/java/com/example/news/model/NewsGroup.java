package com.example.news.model;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Getter
@Setter
public class NewsGroup {

    private String intervalGroup;

    private List<NewsGroupEntries> newsGroupEnteries;

    private String status;
    private String code;
    private String message;

}
