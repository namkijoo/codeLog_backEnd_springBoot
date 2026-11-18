package codeLog_backendS.dto;

import codeLog_backendS.domain.Folder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Builder
public class FolderResponse {
    private Integer id;
    private String title;
    private Integer sortOrder;
    private Integer noteCount;
    private String createdAt;
    private String updatedAt;

    public static FolderResponse from(Folder folder) {
        return FolderResponse.builder()
                .id(folder.getId())
                .title(folder.getTitle())
                .sortOrder(folder.getSortOrder())
                .noteCount(folder.getNoteCount() == null ? 0 : folder.getNoteCount())
                .createdAt(toIso(folder.getCreatedAt()))
                .updatedAt(toIso(folder.getUpdatedAt()))
                .build();
    }

    private static String toIso(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atZone(ZoneOffset.systemDefault()).toInstant().toString();
    }
}
