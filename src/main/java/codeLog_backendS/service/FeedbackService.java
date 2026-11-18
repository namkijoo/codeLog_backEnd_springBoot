package codeLog_backendS.service;

import codeLog_backendS.dto.BoardResponse;
import codeLog_backendS.dto.FeedbackResponse;
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

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BoardService boardService;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
            당신은 프로그래머스 코딩테스트 학습 코치입니다.
            사용자 풀이 기록을 보고 아래 JSON만 반환하세요. 다른 텍스트 금지.

            {
              "strengths": ["강점1", "강점2"],
              "improvements": ["보완점1", "보완점2"],
              "focusToday": "오늘 집중할 한 문장"
            }

            규칙:
            - strengths, improvements는 각각 2~4개
            - 태그/난이도/메모를 근거로 구체적으로
            - 문제 번호나 링크는 넣지 말 것
            - 한국어로 작성
            """;

    public FeedbackResponse getFeedback(Long userId) {
        List<BoardResponse> boards = boardService.getAll(userId);
        List<BoardResponse> programmers = boards.stream()
                .filter(b -> {
                    if (b.getPlatformName() == null) return false;
                    String p = b.getPlatformName().toLowerCase();
                    return p.contains("programmers") || b.getPlatformName().contains("프로그래머스");
                })
                .toList();

        if (programmers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "프로그래머스 문제 기록이 없습니다.");
        }

        try {
            String summary = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(Map.of(
                    "totalCount", programmers.size(),
                    "recentProblems", programmers.stream().limit(30).map(b -> Map.of(
                            "title", b.getTitle(),
                            "difficulty", b.getDifficulty(),
                            "tags", b.getTags() == null ? List.of() : b.getTags(),
                            "memo", b.getMemo() == null ? "" : b.getMemo().substring(0, Math.min(120, b.getMemo().length()))
                    )).toList()
            ));

            String raw = openAiClient.chatJson(SYSTEM_PROMPT, "아래 풀이 기록으로 피드백을 주세요.\n\n" + summary);
            JsonNode root = objectMapper.readTree(raw);

            List<String> strengths = toStringList(root.path("strengths"));
            List<String> improvements = toStringList(root.path("improvements"));
            String focusToday = root.path("focusToday").asText("").trim();

            if (strengths.isEmpty() || improvements.isEmpty() || focusToday.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AI 피드백 형식이 올바르지 않습니다.");
            }

            return FeedbackResponse.builder()
                    .date(LocalDate.now().toString())
                    .strengths(strengths)
                    .improvements(improvements)
                    .focusToday(focusToday)
                    .build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AI 피드백 처리에 실패했습니다.");
        }
    }

    private List<String> toStringList(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (!node.isArray()) return list;
        for (JsonNode n : node) {
            String v = n.asText("").trim();
            if (!v.isEmpty()) list.add(v);
        }
        return list;
    }
}