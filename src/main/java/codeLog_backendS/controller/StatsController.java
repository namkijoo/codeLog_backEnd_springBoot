package codeLog_backendS.controller;

import codeLog_backendS.dto.ActivityResponse;
import codeLog_backendS.security.AuthUser;
import codeLog_backendS.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/activity")
    public ActivityResponse activity(@AuthenticationPrincipal AuthUser user) {
        return statsService.getActivity(user.getId());
    }
}
