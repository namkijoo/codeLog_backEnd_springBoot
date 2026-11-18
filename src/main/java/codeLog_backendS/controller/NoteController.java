package codeLog_backendS.controller;

import codeLog_backendS.dto.NoteRequest;
import codeLog_backendS.dto.NoteResponse;
import codeLog_backendS.security.AuthUser;
import codeLog_backendS.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping("/{id}")
    public NoteResponse getById(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthUser user
    ) {
        return noteService.getById(id, user.getId());
    }

    @PutMapping("/{id}")
    public NoteResponse update(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthUser user,
            @RequestBody NoteRequest request
    ) {
        return noteService.update(id, request, user.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id,
            @AuthenticationPrincipal AuthUser user
    ) {
        noteService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
