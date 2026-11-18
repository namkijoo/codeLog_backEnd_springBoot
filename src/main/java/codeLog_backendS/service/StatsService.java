package codeLog_backendS.service;

import codeLog_backendS.dto.ActivityDay;
import codeLog_backendS.dto.ActivityRange;
import codeLog_backendS.dto.ActivityResponse;
import codeLog_backendS.repository.ActivityCountRow;
import codeLog_backendS.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public ActivityResponse getActivity(Long userId) {
        LocalDate today = LocalDate.now(KST);
        LocalDate from = today.minusDays(364);

        long totalSolved = boardRepository.countByUserId(userId);
        List<ActivityCountRow> rows =
                boardRepository.findActivityCountsByUserId(userId, from.toString());

        Map<String, Long> countByDate = new HashMap<>();
        for (ActivityCountRow row : rows) {
            countByDate.put(row.getDate(), row.getCount() == null ? 0L : row.getCount());
        }

        List<ActivityDay> days = new ArrayList<>();
        for (LocalDate cursor = from; !cursor.isAfter(today); cursor = cursor.plusDays(1)) {
            String key = cursor.toString();
            days.add(new ActivityDay(key, countByDate.getOrDefault(key, 0L)));
        }

        return ActivityResponse.builder()
                .totalSolved(totalSolved)
                .currentStreak(calcStreak(countByDate, today))
                .range(new ActivityRange(from.toString(), today.toString()))
                .days(days)
                .build();
    }

    private long calcStreak(Map<String, Long> countByDate, LocalDate today) {
        long streak = 0;
        LocalDate cursor = today;
        if (countByDate.getOrDefault(cursor.toString(), 0L) == 0L) {
            cursor = cursor.minusDays(1);
        }
        while (countByDate.getOrDefault(cursor.toString(), 0L) > 0L) {
            streak += 1;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }
}
