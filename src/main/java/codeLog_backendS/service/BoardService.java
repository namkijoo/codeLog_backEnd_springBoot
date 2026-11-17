package codeLog_backendS.service;

import codeLog_backendS.domain.Board;
import codeLog_backendS.dto.BoardRequest;
import codeLog_backendS.dto.BoardResponse;
import codeLog_backendS.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public List<BoardResponse> getAll(Long userId) {
        return boardRepository.findByUserIdOrderByIdDesc(userId).stream()
                .map(BoardResponse::from)
                .toList();
    }

    @Transactional
    public BoardResponse create(BoardRequest req, Long userId, String username) {
        validate(req);

        if (boardRepository.existsByUserIdAndProblemNumber(userId, req.getProblemNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 문제입니다.");
        }

        Board board = new Board();
        board.setUserId(userId);
        board.setUsername(username);
        apply(board, req);
        board.setMemo(req.getMemo() == null ? "" : req.getMemo());

        return BoardResponse.from(boardRepository.save(board));
    }

    @Transactional
    public BoardResponse update(Long id, BoardRequest req, Long userId) {
        validate(req);

        Board board = boardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        if (boardRepository.existsByUserIdAndProblemNumberAndIdNot(userId, req.getProblemNumber(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 문제입니다.");
        }

        apply(board, req);
        board.setMemo(req.getMemo() == null ? "" : req.getMemo());

        return BoardResponse.from(board);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 게시글 ID입니다.");
        }

        Board board = boardRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));

        boardRepository.delete(board);
    }

    private void validate(BoardRequest req) {
        if (req.getProblemNumber() == null
                || req.getPlatformName() == null || req.getPlatformName().isBlank()
                || req.getTags() == null
                || req.getDifficulty() == null || req.getDifficulty().isBlank()
                || req.getTitle() == null || req.getTitle().isBlank()
                || req.getLanguage() == null || req.getLanguage().isBlank()
                || req.getCode() == null || req.getCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "필수 값이 없습니다.");
        }
    }

    private void apply(Board board, BoardRequest req) {
        board.setProblemNumber(req.getProblemNumber());
        board.setProblemUrl(req.getProblemUrl() == null ? "" : req.getProblemUrl());
        board.setPlatformName(req.getPlatformName());
        board.setTags(req.getTags());
        board.setDifficulty(req.getDifficulty());
        board.setTitle(req.getTitle());
        board.setLanguage(req.getLanguage());
        board.setCode(req.getCode());
    }
}