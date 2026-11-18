package codeLog_backendS.controller;

import codeLog_backendS.dto.FolderRequest;
import codeLog_backendS.dto.FolderResponse;
import codeLog_backendS.dto.NoteListItem;
import codeLog_backendS.dto.NoteRequest;
import codeLog_backendS.dto.NoteResponse;
import codeLog_backendS.security.AuthUser;
import codeLog_backendS.service.FolderService;
import codeLog_backendS.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;
    private final NoteService noteService;

    @GetMapping
    public List<FolderResponse> getAll(@AuthenticationPrincipal AuthUser user) {
        return folderService.getAll(user.getId());
    }

    @PostMapping
    public ResponseEntity<FolderResponse> create(
            @AuthenticationPrincipal AuthUser user,
            @RequestBody FolderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(folderService.create(request, user.getId()));
    }

    @PutMapping("/{id}")
    public FolderResponse update(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthUser user,
            @RequestBody FolderRequest request
    ) {
        return folderService.update(id, request, user.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthUser user
    ) {
        folderService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{folderId}/notes")
    public List<NoteListItem> getNotes(
            @PathVariable Integer folderId,
            @AuthenticationPrincipal AuthUser user
    ) {
        return noteService.getByFolder(folderId, user.getId());
    }

    @PostMapping("/{folderId}/notes")
    public ResponseEntity<NoteResponse> createNote(
            @PathVariable Integer folderId,
            @AuthenticationPrincipal AuthUser user,
            @RequestBody NoteRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.create(folderId, request, user.getId()));
    }
}
