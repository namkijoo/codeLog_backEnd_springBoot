package codeLog_backendS.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationItem {
    private String title;
    private Integer problemNumber;
    private String platform;
    private String difficulty;
    private String reason;
    private String problemUrl;
}