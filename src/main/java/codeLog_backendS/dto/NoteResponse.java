package codeLog_backendS.dto;

import codeLog_backendS.domain.Note;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Builder
public class NoteResponse {
    private Integer id;
    private Integer folderId;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;

    public static NoteResponse from(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .folderId(note.getFolderId())
                .title(note.getTitle())
                .content(note.getContent())
                .createdAt(toIso(note.getCreatedAt()))
                .updatedAt(toIso(note.getUpdatedAt()))
                .build();
    }

    private static String toIso(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atZone(ZoneOffset.systemDefault()).toInstant().toString();
    }
}
