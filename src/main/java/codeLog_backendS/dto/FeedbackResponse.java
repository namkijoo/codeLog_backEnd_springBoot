package codeLog_backendS.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FeedbackResponse {
    private String date;
    private List<String> strengths;
    private List<String> improvements;
    private String focusToday;
}