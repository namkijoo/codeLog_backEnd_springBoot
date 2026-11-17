package codeLog_backendS.repository;

import codeLog_backendS.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByUserIdOrderByIdDesc(Long userId);

    Optional<Board> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndProblemNumber(Long userId, Integer problemNumber);

    boolean existsByUserIdAndProblemNumberAndIdNot(Long userId, Integer problemNumber, Long id);
}