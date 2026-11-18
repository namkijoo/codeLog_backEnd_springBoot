package codeLog_backendS.service;

import codeLog_backendS.domain.Folder;
import codeLog_backendS.dto.FolderRequest;
import codeLog_backendS.dto.FolderResponse;
import codeLog_backendS.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    @Transactional(readOnly = true)
    public List<FolderResponse> getAll(Long userId) {
        return folderRepository.findByUserIdOrderBySortOrderAscIdAsc(toInt(userId)).stream()
                .map(FolderResponse::from)
                .toList();
    }

    @Transactional
    public FolderResponse create(FolderRequest req, Long userId) {
        Integer uid = toInt(userId);
        String title = assertTitle(req.getTitle());

        if (folderRepository.existsByUserIdAndTitle(uid, title)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 폴더명입니다.");
        }

        Integer next = folderRepository.findNextSortOrder(uid);
        Folder folder = new Folder();
        folder.setUserId(uid);
        folder.setTitle(title);
        folder.setSortOrder(next == null ? 0 : next);

        Folder saved = folderRepository.save(folder);
        Folder created = folderRepository.findByIdAndUserId(saved.getId(), uid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "생성 실패"));
        return FolderResponse.from(created);
    }

    @Transactional
    public FolderResponse update(Integer id, FolderRequest req, Long userId) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 폴더 ID입니다.");
        }

        Integer uid = toInt(userId);
        Folder folder = folderRepository.findByIdAndUserId(id, uid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "폴더를 찾을 수 없습니다."));

        String title = assertTitle(req.getTitle());
        int sortOrder = req.getSortOrder() == null ? folder.getSortOrder() : req.getSortOrder();
        if (sortOrder < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 정렬 순서입니다.");
        }

        if (folderRepository.existsByUserIdAndTitleAndIdNot(uid, title, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 폴더명입니다.");
        }

        folder.setTitle(title);
        folder.setSortOrder(sortOrder);

        Folder updated = folderRepository.findByIdAndUserId(id, uid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "폴더를 찾을 수 없습니다."));
        return FolderResponse.from(updated);
    }

    @Transactional
    public void delete(Integer id, Long userId) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 폴더 ID입니다.");
        }

        Integer uid = toInt(userId);
        Folder folder = folderRepository.findByIdAndUserId(id, uid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "폴더를 찾을 수 없습니다."));

        folderRepository.delete(folder);
    }

    @Transactional(readOnly = true)
    public Folder assertOwned(Integer folderId, Long userId) {
        if (folderId == null || folderId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 폴더 ID입니다.");
        }
        return folderRepository.findByIdAndUserId(folderId, toInt(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "폴더를 찾을 수 없습니다."));
    }

    private String assertTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "폴더 제목이 필요합니다.");
        }
        String trimmed = title.trim();
        if (trimmed.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "폴더 제목은 100자 이하여야 합니다.");
        }
        return trimmed;
    }

    private Integer toInt(Long userId) {
        return Math.toIntExact(userId);
    }
}
