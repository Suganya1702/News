package com.example.news.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
@Setter
@Getter
public class NewsSearchExternalApiRequest {
    private String searchKeyword;
    private String publishedAt;


}
