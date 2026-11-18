package codeLog_backendS.controller;

import codeLog_backendS.dto.DailyRecommendResponse;
import codeLog_backendS.dto.FeedbackResponse;
import codeLog_backendS.security.AuthUser;
import codeLog_backendS.service.FeedbackService;
import codeLog_backendS.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;
    private final FeedbackService feedbackService;

    @GetMapping("/daily")
    public DailyRecommendResponse daily(@AuthenticationPrincipal AuthUser user) {
        return recommendService.getDailyRecommend(user.getId());
    }

    @GetMapping("/feedback")
    public FeedbackResponse feedback(@AuthenticationPrincipal AuthUser user) {
        return feedbackService.getFeedback(user.getId());
    }
}