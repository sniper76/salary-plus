package ag.act.repository;

import ag.act.entity.Popup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("checkstyle:LineLength")
@Repository
public interface PopupRepository extends JpaRepository<Popup, Long>, JpaSpecificationExecutor<Popup> {
    Optional<Popup> findByIdAndStatus(Long popupId, ag.act.model.Status status);

    @Query(value = """
        select p.*
        from popups p
        where p.target_start_datetime <= current_timestamp
          and p.target_end_datetime >= current_timestamp
          and p.display_target_type = :displayTargetType
          and p.stock_target_type = 'ALL'
          and p.status = 'ACTIVE'
        order by case when p.link_type = 'LINK' then 1
                      when p.link_type = 'NOTIFICATION' then 2
                      when p.link_type = 'MAIN_HOME' then 3
                      when p.link_type = 'NEWS_HOME' then 4
                      when p.link_type = 'STOCK_HOME' then 5
                      when p.link_type = 'NONE' then 6 else 100 end asc,
                 case when p.board_category = 'DIGITAL_DELEGATION' then 1
                      when p.board_category = 'CO_HOLDING_ARRANGEMENTS' then 2
                      when p.board_category = 'ETC' then 3
                      when p.board_category = 'SURVEYS' then 4
                      when p.board_category = 'SOLIDARITY_LEADER_LETTERS' then 5
                      when p.board_category = 'DAILY_ACT' then 6
                      when p.board_category = 'STOCKHOLDER_ACTION' then 7
                      when p.board_category = 'NOTICE' then 8
                      when p.board_category = 'CAMPAIGN' then 8
                      when p.board_category = 'EVENT' then 8
                      when p.board_category = 'STOCK_ANALYSIS_DATA' then 9 else 100 end asc
        limit 1
        """, nativeQuery = true)
    Optional<Popup> findExclusiveByStockTargetTypeIsAll(String displayTargetType);

    @Query(value = """
        select p.*
        from popups p
            inner join stock_group_mappings sgm on p.stock_group_id = sgm.stock_group_id
            inner join user_holding_stocks uhs on sgm.stock_code = uhs.stock_code and uhs.user_id = :userId
        where p.target_start_datetime <= current_timestamp
          and p.target_end_datetime >= current_timestamp
          and p.display_target_type = :displayTargetType
          and p.stock_target_type = 'STOCK_GROUP'
          and p.status = 'ACTIVE'
          and (uhs.stock_code in :stockCode or :stockCode is null)
        order by case when p.link_type = 'LINK' then 1
                      when p.link_type = 'NOTIFICATION' then 2
                      when p.link_type = 'MAIN_HOME' then 3
                      when p.link_type = 'NEWS_HOME' then 4
                      when p.link_type = 'STOCK_HOME' then 5
                      when p.link_type = 'NONE' then 6 else 100 end asc,
                 case when p.board_category = 'DIGITAL_DELEGATION' then 1
                      when p.board_category = 'CO_HOLDING_ARRANGEMENTS' then 2
                      when p.board_category = 'ETC' then 3
                      when p.board_category = 'SURVEYS' then 4
                      when p.board_category = 'SOLIDARITY_LEADER_LETTERS' then 5
                      when p.board_category = 'DAILY_ACT' then 6
                      when p.board_category = 'STOCKHOLDER_ACTION' then 7
                      when p.board_category = 'NOTICE' then 8
                      when p.board_category = 'CAMPAIGN' then 8
                      when p.board_category = 'EVENT' then 8
                      when p.board_category = 'STOCK_ANALYSIS_DATA' then 9 else 100 end asc
        limit 1
        """, nativeQuery = true)
    Optional<Popup> findExclusiveByStockTargetTypeIsStockGroup(String displayTargetType, List<String> stockCode, Long userId);

    @Query(value = """
        select p.*
        from popups p
        inner join user_holding_stocks uhs on p.stock_code = uhs.stock_code and uhs.user_id = :userId
        where p.target_start_datetime <= current_timestamp
          and p.target_end_datetime >= current_timestamp
          and p.display_target_type = :displayTargetType
          and p.stock_target_type = 'STOCK'
          and p.status = 'ACTIVE'
          and (uhs.stock_code in :stockCode or :stockCode is null)
        order by case when p.link_type = 'LINK' then 1
                      when p.link_type = 'NOTIFICATION' then 2
                      when p.link_type = 'MAIN_HOME' then 3
                      when p.link_type = 'NEWS_HOME' then 4
                      when p.link_type = 'STOCK_HOME' then 5
                      when p.link_type = 'NONE' then 6 else 100 end asc,
                 case when p.board_category = 'DIGITAL_DELEGATION' then 1
                      when p.board_category = 'CO_HOLDING_ARRANGEMENTS' then 2
                      when p.board_category = 'ETC' then 3
                      when p.board_category = 'SURVEYS' then 4
                      when p.board_category = 'SOLIDARITY_LEADER_LETTERS' then 5
                      when p.board_category = 'DAILY_ACT' then 6
                      when p.board_category = 'STOCKHOLDER_ACTION' then 7
                      when p.board_category = 'NOTICE' then 8
                      when p.board_category = 'CAMPAIGN' then 8
                      when p.board_category = 'EVENT' then 8
                      when p.board_category = 'STOCK_ANALYSIS_DATA' then 9 else 100 end asc
        limit 1
        """, nativeQuery = true)
    Optional<Popup> findExclusiveByStockTargetTypeIsStock(String displayTargetType, List<String> stockCode, Long userId);
}
