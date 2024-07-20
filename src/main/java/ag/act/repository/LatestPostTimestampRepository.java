package ag.act.repository;

import ag.act.entity.LatestPostTimestamp;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LatestPostTimestampRepository extends JpaRepository<LatestPostTimestamp, Long> {
    Optional<LatestPostTimestamp> findByStockCodeAndBoardGroupAndBoardCategory(
        String stockCode,
        BoardGroup boardGroup,
        BoardCategory boardCategory
    );
}
