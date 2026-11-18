package codeLog_backendS.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DailyRecommendResponse {
    private String date;
    private List<RecommendationItem> recommendations;
}