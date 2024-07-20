package ag.act.repository;

import ag.act.entity.Board;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByStockCodeAndGroupAndCategory(String stockCode, BoardGroup boardGroup, BoardCategory boardCategory);

    Optional<Board> findByStockCodeAndCategory(String stockCode, BoardCategory boardCategory);

    List<Board> findAllByStockCodeAndCategoryIn(String stockCode, List<BoardCategory> boardCategories);

    List<Board> findAllByStockCode(String stockCode);
}
