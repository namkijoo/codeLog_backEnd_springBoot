package codeLog_backendS.service;

import codeLog_backendS.dto.BoardResponse;
import codeLog_backendS.dto.DailyRecommendResponse;
import codeLog_backendS.dto.RecommendationItem;
import codeLog_backendS.llm.OpenAiClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final BoardService boardService;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
            당신은 프로그래머스 코딩테스트 학습 코치입니다.
            사용자 풀이 기록을 보고 아래 JSON만 반환하세요. 다른 텍스트 금지.

            {
              "recommendations": [
                { "title": "문제명", "problemNumber": 42576, "difficulty": "Lv.2", "reason": "추천 이유" },
                { "title": "문제명", "problemNumber": 1845, "difficulty": "Lv.1", "reason": "추천 이유" },
                { "title": "문제명", "problemNumber": 43165, "difficulty": "Lv.2", "reason": "추천 이유" }
              ]
            }

            규칙:
            - 프로그래머스 문제만 추천
            - recommendations는 정확히 3개
            - problemNumber는 실제 프로그래머스 문제 번호(숫자)
            - 사용자가 이미 푼 problemNumber는 제외
            - problemUrl, platform, feedback 필드는 만들지 말 것
            - 약한 태그/난이도를 우선 보완
            - 한국어로 작성
            """;

    public DailyRecommendResponse getDailyRecommend(Long userId) {
        List<BoardResponse> boards = boardService.getAll(userId);
        List<BoardResponse> programmers = boards.stream()
                .filter(b -> isProgrammers(b.getPlatformName()))
                .toList();

        if (programmers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "프로그래머스 문제 기록이 없습니다. 먼저 문제를 등록해주세요.");
        }

        Set<Integer> solved = programmers.stream()
                .map(BoardResponse::getProblemNumber)
                .collect(Collectors.toSet());

        String userPrompt = """
                아래는 사용자의 프로그래머스 풀이 기록 요약입니다.
                이미 푼 문제 번호는 추천에서 제외하세요.
                반드시 recommendations 3개만 포함하세요. feedback은 넣지 마세요.

                %s
                """.formatted(summarize(programmers));

        try {
            String raw = openAiClient.chatJson(SYSTEM_PROMPT, userPrompt);
            JsonNode root = objectMapper.readTree(raw);
            JsonNode list = root.path("recommendations");
            if (!list.isArray() || list.isEmpty()) {
                list = root.path("recommendation");
            }
            if (!list.isArray() || list.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AI 응답 형식이 올바르지 않습니다.");
            }

            List<RecommendationItem> items = new ArrayList<>();
            for (JsonNode node : list) {
                String title = node.path("title").asText("").trim();
                String difficulty = node.path("difficulty").asText("").trim();
                String reason = node.path("reason").asText("").trim();
                int problemNumber = node.path("problemNumber").asInt(0);
                if (title.isEmpty() || difficulty.isEmpty() || reason.isEmpty() || problemNumber <= 0) {
                    continue;
                }
                if (solved.contains(problemNumber)) {
                    continue;
                }
                items.add(RecommendationItem.builder()
                        .title(title)
                        .problemNumber(problemNumber)
                        .platform("Programmers")
                        .difficulty(difficulty)
                        .reason(reason)
                        .problemUrl("https://school.programmers.co.kr/learn/courses/30/lessons/" + problemNumber)
                        .build());
                if (items.size() == 3) break;
            }

            if (items.size() != 3) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "이미 푼 문제가 포함되었거나 추천이 3개가 아닙니다. 다시 시도해주세요.");
            }

            return DailyRecommendResponse.builder()
                    .date(LocalDate.now().toString())
                    .recommendations(items)
                    .build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AI 추천 처리에 실패했습니다.");
        }
    }

    private boolean isProgrammers(String platformName) {
        if (platformName == null) return false;
        String p = platformName.toLowerCase();
        return p.contains("programmers") || platformName.contains("프로그래머스");
    }

    private String summarize(List<BoardResponse> programmers) {
        try {
            var recent = programmers.stream().limit(30).map(b -> Map.of(
                    "problemNumber", b.getProblemNumber(),
                    "title", b.getTitle(),
                    "difficulty", b.getDifficulty(),
                    "tags", b.getTags() == null ? List.of() : b.getTags(),
                    "language", b.getLanguage(),
                    "memo", b.getMemo() == null ? "" : b.getMemo().substring(0, Math.min(120, b.getMemo().length()))
            )).toList();

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(Map.of(
                    "totalCount", programmers.size(),
                    "solvedProblemNumbers", programmers.stream().map(BoardResponse::getProblemNumber).toList(),
                    "recentProblems", recent
            ));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "기록 요약 실패");
        }
    }
}