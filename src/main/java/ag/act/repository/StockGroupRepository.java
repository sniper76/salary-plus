package ag.act.repository;

import ag.act.dto.stock.SimpleStockGroupDto;
import ag.act.entity.StockGroup;
import ag.act.model.Status;
import ag.act.repository.interfaces.StockGroupSearchResultItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockGroupRepository extends JpaRepository<StockGroup, Long> {

    @Query(value = """
            SELECT
                sg.id,
                sg.name,
                sg.status,
                sg.description,
                sg.created_at AS createdAt,
                sg.updated_at AS updatedAt,
                sg.deleted_at AS deletedAt,
                COUNT(sgm.id) AS stockCount
            FROM stock_groups sg
            LEFT JOIN stock_group_mappings sgm ON sgm.stock_group_id = sg.id
            WHERE (sg.id in :stockGroupIds OR :stockGroupIds is null)
            AND sg.status = 'ACTIVE'
            GROUP BY sg.id, sg.name, sg.description, sg.status, sg.created_at, sg.updated_at, sg.deleted_at
        """, nativeQuery = true
    )
    Page<StockGroupSearchResultItem> findAllByStockGroupIdIn(List<Long> stockGroupIds, PageRequest pageRequest);

    Optional<StockGroup> findByName(String stockGroupName);

    List<StockGroup> findTop10ByStatusInOrderByNameAsc(List<Status> statuses);

    List<StockGroup> findTop10ByNameContainingAndStatusInOrderByNameAsc(String searchKeyword, List<Status> statuses);

    @Query("""
        select new ag.act.dto.stock.SimpleStockGroupDto(s.id, s.name)
        from StockGroup s
        WHERE s.status = 'ACTIVE'
        """
    )
    List<SimpleStockGroupDto> findAllSimpleStockGroups(Sort sort);
}
