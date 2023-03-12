package com.example.news.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class NewsSearchExternalApiResponse {
    @JsonProperty("status")
    private String status;
    @JsonProperty("totalResults")
    private int totalResults;
    @JsonProperty("articles")
    private List<Articles> articles;
    @JsonProperty("code")
    private String code;
    @JsonProperty("message")
    private String message;

}
