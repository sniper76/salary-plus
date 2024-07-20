package ag.act.repository;

import ag.act.dto.user.BlockedUserDto;
import ag.act.entity.BlockedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUser, Long> {

    boolean existsByUserIdAndBlockedUserId(Long userId, Long blockedUserId);

    @Query("""
        select bu.blockedUserId 
        from BlockedUser bu 
        where bu.userId = :userId
        """)
    List<Long> findBlockedUserIdsByUserId(@Param("userId") Long userId);

    void deleteByUserIdAndBlockedUserId(Long userId, Long blockedUserId);

    @Query(value = """
        SELECT bu.id
          , bu.blocked_user_id AS blockedUserId
          , u.nickname
          , u.profile_image_url AS profileImageUrl
          , bu.created_at AS createdAt
        FROM blocked_users bu
        INNER JOIN users u
         ON u.id = bu.blocked_user_id
         AND u.status = 'ACTIVE'
        WHERE bu.user_id = :userId
        """, nativeQuery = true)
    Page<BlockedUserDto> findBlockedUsersByUserId(Long userId, Pageable pageable);

    @Query(value = """
        SELECT
          bu.id,
          bu.blocked_user_id AS blockedUserId,
          u.nickname,
          u.profile_image_url AS profileImageUrl,
          bu.created_at AS createdAt
        FROM blocked_users bu
        INNER JOIN users u
          ON u.id = bu.blocked_user_id
          AND u.status = 'ACTIVE'
        WHERE bu.user_id = :userId
        AND EXISTS(
              SELECT 1
                  FROM  solidarity_leaders sl
                      INNER JOIN solidarities s
                  ON s.id = sl.solidarity_id
                  AND s.status = 'ACTIVE'
                  AND sl.user_id = bu.blocked_user_id
          )
           """, nativeQuery = true
    )
    Page<BlockedUserDto> findBlockedUsersByUserIdAndIsSolidarityLeader(Long userId, Pageable pageable);

    @Query(value = """
        SELECT bu.id, 
        bu.blocked_user_id AS blockedUserId, 
        u.nickname, 
        u.profile_image_url AS profileImageUrl, 
        bu.created_at AS createdAt
        FROM blocked_users bu
            INNER JOIN users u
                ON u.id = bu.blocked_user_id
                AND u.status = 'ACTIVE'
            LEFT JOIN solidarity_leaders sl
                ON sl.user_id = bu.blocked_user_id
        WHERE sl.user_id IS NULL
        AND bu.user_id = :userId
          """, nativeQuery = true)
    Page<BlockedUserDto> findBlockedUsersByUserIdAndIsNotSolidarityLeader(Long userId, Pageable pageable);
}
