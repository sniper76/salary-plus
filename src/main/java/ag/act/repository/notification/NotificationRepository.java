package ag.act.repository.notification;

import ag.act.entity.notification.Notification;
import ag.act.repository.interfaces.UserNotificationDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value = """
            SELECT
                l.id AS id,
                l.post_id AS postId,
                l.category AS category,
                l.type AS type,
                l.created_at AS createdAt,
                l.active_start_date AS activeStartDate,
                CASE WHEN nuv.id IS NOT NULL THEN true ELSE false END AS read,
                l.title AS postTitle,
                l.board_category AS boardCategory,
                l."group" AS boardGroup,
                l.code AS stockCode,
                l.name AS stockName
            FROM notifications n
             inner join (
                SELECT s.code, s.name, b.category as board_category, b."group", p.id as post_id, p.title, 
                        n.id, n.type, n.created_at, n.category, n.active_start_date
                FROM notifications n
                        inner join posts p on n.post_id = p.id
                        inner join boards b on p.board_id = b.id
                        inner join stocks s on b.stock_code = s.code
                        inner join user_holding_stocks uhs on uhs.stock_code = s.code and uhs.user_id = :userId
                WHERE n.status = 'ACTIVE'
                AND n.active_start_date between :searchStartDateTime AND current_timestamp
                AND (n.active_end_date is null OR n.active_end_date >= current_timestamp)
                AND (n.category = :category OR :category is null)
                union all
                SELECT s.code, s.name, b.category as board_category, b."group", p.id as post_id, p.title, 
                        n.id, n.type, n.created_at, n.category, n.active_start_date
                FROM notifications n
                        inner join posts p on n.post_id = p.id
                        inner join boards b on p.board_id = b.id
                        inner join stocks s on b.stock_code = s.code and b.stock_code = :globalStockCode
                WHERE n.status = 'ACTIVE'
                AND n.active_start_date between :searchStartDateTime AND current_timestamp
                AND (n.active_end_date is null OR n.active_end_date >= current_timestamp)
                AND (n.category = :category OR :category is null)
            ) l on n.id = l.id
            LEFT JOIN notification_user_views nuv ON l.id = nuv.notification_id AND nuv.user_id = :userId
        """, nativeQuery = true)
    Page<UserNotificationDetails> findAllUserNotifications(
        Long userId,
        String category,
        LocalDateTime searchStartDateTime,
        String globalStockCode,
        Pageable pageable
    );

    @Query(value = """
        SELECT COUNT(DISTINCT n.id)
        FROM (
            SELECT n.id
            FROM notifications n
                     inner join posts p on n.post_id = p.id
                     inner join boards b on p.board_id = b.id and b.stock_code = :globalStockCode
            WHERE n.status = 'ACTIVE'
            AND n.active_start_date between :searchStartDateTime AND current_timestamp
            AND (n.active_end_date is null OR n.active_end_date >= current_timestamp)
            union all
            SELECT n.id
            FROM notifications n
                     inner join posts p on n.post_id = p.id
                     inner join boards b on p.board_id = b.id
                     inner join user_holding_stocks uhs on b.stock_code = uhs.stock_code and uhs.user_id = :userId
            WHERE n.status = 'ACTIVE'
            AND n.active_start_date between :searchStartDateTime AND current_timestamp
            AND (n.active_end_date is null OR n.active_end_date >= current_timestamp)
        ) n
        LEFT OUTER JOIN notification_user_views nuv
            ON n.id = nuv.notification_id
            AND nuv.user_id = :userId
        WHERE nuv.id is null
        """, nativeQuery = true
    )
    long countAllUserUnreadNotifications(
        @Param("userId") Long userId,
        @Param("globalStockCode") String globalStockCode,
        @Param("searchStartDateTime") LocalDateTime searchStartDateTime
    );

    Optional<Notification> findByPostId(Long postId);
}
