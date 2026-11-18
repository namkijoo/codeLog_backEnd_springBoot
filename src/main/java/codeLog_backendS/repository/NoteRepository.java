package codeLog_backendS.repository;

import codeLog_backendS.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Integer> {

    List<Note> findByFolderIdAndUserIdOrderByUpdatedAtDescIdDesc(Integer folderId, Integer userId);

    Optional<Note> findByIdAndUserId(Integer id, Integer userId);
}
