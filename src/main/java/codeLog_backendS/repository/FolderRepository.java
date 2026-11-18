package codeLog_backendS.repository;

import codeLog_backendS.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Integer> {

    List<Folder> findByUserIdOrderBySortOrderAscIdAsc(Integer userId);

    Optional<Folder> findByIdAndUserId(Integer id, Integer userId);

    boolean existsByUserIdAndTitle(Integer userId, String title);

    boolean existsByUserIdAndTitleAndIdNot(Integer userId, String title, Integer id);

    @Query("SELECT COALESCE(MAX(f.sortOrder), -1) + 1 FROM Folder f WHERE f.userId = :userId")
    Integer findNextSortOrder(@Param("userId") Integer userId);
}
