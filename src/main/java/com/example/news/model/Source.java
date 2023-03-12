package com.example.news.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class Source {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;

}
