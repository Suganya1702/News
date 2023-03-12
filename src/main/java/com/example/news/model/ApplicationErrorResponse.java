package com.example.news.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
@Setter
@Getter
public class ApplicationErrorResponse {
    @JsonProperty("code")
    private String code;
    @JsonProperty("possibleCauses")
    private String possibleCauses;
    @JsonProperty("status")
    private String status;
}
