package codeLog_backendS.controller;

import codeLog_backendS.dto.BoardRequest;
import codeLog_backendS.dto.BoardResponse;
import codeLog_backendS.security.AuthUser;
import codeLog_backendS.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public List<BoardResponse> getAll(@AuthenticationPrincipal AuthUser user) {
        return boardService.getAll(user.getId());
    }

    @PostMapping
    public ResponseEntity<BoardResponse> create(
            @AuthenticationPrincipal AuthUser user,
            @RequestBody BoardRequest request
    ) {
        BoardResponse created = boardService.create(request, user.getId(), user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public BoardResponse update(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthUser user,
            @RequestBody BoardRequest request
    ) {
        return boardService.update(id, request, user.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthUser user
    ) {
        boardService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}