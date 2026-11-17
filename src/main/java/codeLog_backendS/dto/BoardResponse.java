package codeLog_backendS.dto;

import codeLog_backendS.domain.Board;
import lombok.Builder;
import lombok.Getter;

import java.time.ZoneOffset;
import java.util.List;

@Getter
@Builder
public class BoardResponse {
    private Long id;
    private String username;
    private Integer problemNumber;
    private String problemUrl;
    private String platformName;
    private List<String> tags;
    private String difficulty;
    private String title;
    private String memo;
    private String language;
    private String code;
    private String createdAt;
    private String updatedAt;

    public static BoardResponse from(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .username(board.getUsername())
                .problemNumber(board.getProblemNumber())
                .problemUrl(board.getProblemUrl())
                .platformName(board.getPlatformName())
                .tags(board.getTags() == null ? List.of() : board.getTags())
                .difficulty(board.getDifficulty())
                .title(board.getTitle())
                .memo(board.getMemo())
                .language(board.getLanguage())
                .code(board.getCode())
                .createdAt(board.getCreatedAt() == null ? null :
                        board.getCreatedAt().atZone(ZoneOffset.systemDefault()).toInstant().toString())
                .updatedAt(board.getUpdatedAt() == null ? null :
                        board.getUpdatedAt().atZone(ZoneOffset.systemDefault()).toInstant().toString())
                .build();
    }
}