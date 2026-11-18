package codeLog_backendS.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ActivityResponse {
    private long totalSolved;
    private long currentStreak;
    private ActivityRange range;
    private List<ActivityDay> days;
}
