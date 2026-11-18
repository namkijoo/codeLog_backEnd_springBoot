package codeLog_backendS.service;

import codeLog_backendS.domain.Note;
import codeLog_backendS.dto.NoteListItem;
import codeLog_backendS.dto.NoteRequest;
import codeLog_backendS.dto.NoteResponse;
import codeLog_backendS.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final FolderService folderService;

    @Transactional(readOnly = true)
    public List<NoteListItem> getByFolder(Integer folderId, Long userId) {
        folderService.assertOwned(folderId, userId);
        Integer uid = Math.toIntExact(userId);
        return noteRepository.findByFolderIdAndUserIdOrderByUpdatedAtDescIdDesc(folderId, uid).stream()
                .map(NoteListItem::from)
                .toList();
    }

    @Transactional
    public NoteResponse create(Integer folderId, NoteRequest req, Long userId) {
        folderService.assertOwned(folderId, userId);
        Integer uid = Math.toIntExact(userId);
        ValidatedNote body = assertNoteRequest(req);

        Note note = new Note();
        note.setFolderId(folderId);
        note.setUserId(uid);
        note.setTitle(body.title());
        note.setContent(body.content());

        Note saved = noteRepository.save(note);
        Note created = noteRepository.findByIdAndUserId(saved.getId(), uid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "생성 실패"));
        return NoteResponse.from(created);
    }

    @Transactional(readOnly = true)
    public NoteResponse getById(Integer id, Long userId) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 노트 ID입니다.");
        }
        Note note = noteRepository.findByIdAndUserId(id, Math.toIntExact(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "노트를 찾을 수 없습니다."));
        return NoteResponse.from(note);
    }

    @Transactional
    public NoteResponse update(Integer id, NoteRequest req, Long userId) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 노트 ID입니다.");
        }

        Integer uid = Math.toIntExact(userId);
        Note note = noteRepository.findByIdAndUserId(id, uid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "노트를 찾을 수 없습니다."));

        ValidatedNote body = assertNoteRequest(req);
        note.setTitle(body.title());
        note.setContent(body.content());

        Note updated = noteRepository.findByIdAndUserId(id, uid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "노트를 찾을 수 없습니다."));
        return NoteResponse.from(updated);
    }

    @Transactional
    public void delete(Integer id, Long userId) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 노트 ID입니다.");
        }

        Integer uid = Math.toIntExact(userId);
        Note note = noteRepository.findByIdAndUserId(id, uid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "노트를 찾을 수 없습니다."));

        noteRepository.delete(note);
    }

    private ValidatedNote assertNoteRequest(NoteRequest req) {
        if (req.getTitle() == null || req.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "노트 제목이 필요합니다.");
        }
        if (req.getContent() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "노트 본문이 필요합니다.");
        }

        String title = req.getTitle().trim();
        if (title.length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "노트 제목은 200자 이하여야 합니다.");
        }
        return new ValidatedNote(title, req.getContent());
    }

    private record ValidatedNote(String title, String content) {}
}
