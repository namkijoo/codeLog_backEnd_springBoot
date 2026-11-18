package codeLog_backendS.repository;

import codeLog_backendS.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByUserIdOrderByIdDesc(Long userId);

    Optional<Board> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndProblemNumber(Long userId, Integer problemNumber);

    boolean existsByUserIdAndProblemNumberAndIdNot(Long userId, Integer problemNumber, Long id);

    long countByUserId(Long userId);

    @Query(
            value = """
                    SELECT DATE_FORMAT(createdAt, '%Y-%m-%d') AS date, COUNT(*) AS count
                    FROM boards
                    WHERE userId = :userId AND createdAt >= :fromDate
                    GROUP BY date
                    ORDER BY date ASC
                    """,
            nativeQuery = true
    )
    List<ActivityCountRow> findActivityCountsByUserId(
            @Param("userId") Long userId,
            @Param("fromDate") String fromDate
    );
}
