package ag.act.repository;

import ag.act.dto.SimpleUserDto;
import ag.act.dto.user.SolidarityLeaderApplicantUserDto;
import ag.act.dto.user.UserWithStockDto;
import ag.act.entity.User;
import ag.act.enums.RoleType;
import ag.act.enums.push.UserPushAgreementType;
import ag.act.model.Status;
import ag.act.module.dashboard.statistics.ICountItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByHashedCI(String hashedCI);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByNickname(String nickname);

    List<User> findAllByStatusIn(List<Status> statuses);

    @Query("""
            SELECT u
            FROM User u
            LEFT JOIN FETCH u.nicknameHistory
            JOIN FETCH u.userHoldingStocks uhs
            WHERE uhs.stockCode = :stockCode AND u.status = 'ACTIVE'
        """)
    List<User> findAllByStockCode(@Param("stockCode") String stockCode);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            LEFT JOIN FETCH u.nicknameHistory
            JOIN FETCH UserHoldingStock uhs ON uhs.userId = u.id
            INNER JOIN StockGroup sg ON sg.id = :stockGroupId
            INNER JOIN StockGroupMapping sgm ON sgm.stockGroupId = sg.id and sgm.stockCode = uhs.stockCode
            WHERE u.status = 'ACTIVE'
        """)
    Set<User> findAllByStockGroupId(@Param("stockGroupId") Long stockGroupId);

    Optional<User> findByHashedPhoneNumber(String hashedPhoneNumber);

    @Query("""
        SELECT new ag.act.dto.user.UserWithStockDto(u, uhs.quantity)
        FROM User u
        JOIN FETCH UserHoldingStock uhs ON uhs.userId = u.id
        WHERE uhs.stockCode = :stockCode
        AND u.status = 'ACTIVE'
        ORDER BY u.name ASC
        """)
    Page<UserWithStockDto> findUserWitStocksByStockCode(@Param("stockCode") String stockCode, Pageable pageable);

    Optional<User> findByNameAndBirthDateAndStatus(String name, LocalDateTime birthDate, Status status);

    @Query("""
        SELECT new ag.act.dto.user.SolidarityLeaderApplicantUserDto(sla, u)
        FROM User u
        JOIN FETCH SolidarityLeaderApplicant sla ON sla.userId = u.id
        WHERE sla.solidarityLeaderElectionId = :solidarityLeaderElectionId
        AND u.status = 'ACTIVE'
        AND sla.applyStatus = 'COMPLETE'
        ORDER BY u.name ASC
        """)
    List<SolidarityLeaderApplicantUserDto> findAllCompletedSolidarityLeaderApplicantUsers(Long solidarityLeaderElectionId);

    @Query("""
        SELECT distinct u.id
        FROM User u
        JOIN FETCH UserRole ur ON ur.userId = u.id
        JOIN FETCH Role r ON ur.roleId = r.id
        WHERE r.status = 'ACTIVE'
        AND r.type = :roleType
        """)
    List<Long> findAllByRole(@Param("roleType") RoleType roleType);

    @Query("""
        select count(u.id)
        from User u
        join fetch MyDataSummary mds on u.id = mds.userId
        where u.status = 'ACTIVE'
        and mds.updatedAt between :start and :end
        """)
    Optional<Long> countByUserActiveMyDataAccessCount(LocalDateTime start, LocalDateTime end);

    @Query("""
        select count(u.id)
        from User u
        join fetch MyDataSummary mds on u.id = mds.userId
        where u.status = 'ACTIVE'
        and mds.updatedAt <= :end
        """)
    Optional<Long> countByUserActiveMyDataAccessAccumulate(LocalDateTime end);

    Optional<Long> countByStatusInAndCreatedAtBetween(List<Status> statusList, LocalDateTime start, LocalDateTime end);

    Optional<Long> countByStatusInAndUpdatedAtBetween(List<Status> statusList, LocalDateTime start, LocalDateTime end);

    @Query(value = """
        SELECT
             CASE
                 WHEN EXTRACT(YEAR FROM AGE(current_date, birth_date)) BETWEEN 0 AND 19 THEN '10s'
                 WHEN EXTRACT(YEAR FROM AGE(current_date, birth_date)) BETWEEN 20 AND 29 THEN '20s'
                 WHEN EXTRACT(YEAR FROM AGE(current_date, birth_date)) BETWEEN 30 AND 39 THEN '30s'
                 WHEN EXTRACT(YEAR FROM AGE(current_date, birth_date)) BETWEEN 40 AND 49 THEN '40s'
                 WHEN EXTRACT(YEAR FROM AGE(current_date, birth_date)) BETWEEN 50 AND 59 THEN '50s'
                 WHEN EXTRACT(YEAR FROM AGE(current_date, birth_date)) BETWEEN 60 AND 69 THEN '60s'
                 WHEN EXTRACT(YEAR FROM AGE(current_date, birth_date)) BETWEEN 70 AND 79 THEN '70s'
                 WHEN EXTRACT(YEAR FROM AGE(current_date, birth_date)) BETWEEN 80 AND 89 THEN '80s'
                 WHEN EXTRACT(YEAR FROM AGE(current_date, birth_date)) >= 90 THEN '90s'
                 ELSE 'NONE'
                 END AS title,
             COUNT(*) AS longValue
         FROM
             users
        where status in :statusList
         GROUP BY
             title
         ORDER BY
             MIN(EXTRACT(YEAR FROM AGE(current_date, birth_date)))
        """, nativeQuery = true)
    List<ICountItem> findAgeGroupByStatusIn(List<String> statusList);

    @Query(value = """
            select gender as title, count(1) as longValue
            from users
            where status in :statusList
            group by gender
        """, nativeQuery = true)
    List<ICountItem> findGenderCountByStatusIn(List<String> statusList);


    @Query("""
        SELECT DISTINCT u
        FROM User u
        INNER JOIN PollAnswer pa
            ON pa.userId = u.id
        WHERE pa.pollId = :pollId
        """)
    Page<User> findAllVotedInPoll(Long pollId, Pageable pageRequest);

    @Query("""
        select u.id
        from User u
        inner join CorporateUser cu on u.id = cu.userId
        """)
    List<Long> findAllCorporateUserIds();

    @Query(value = """
        SELECT u.push_token
        FROM users u
        INNER JOIN user_holding_stocks uhs
            ON u.id = uhs.user_id
        LEFT JOIN user_push_agreements upa
            ON u.id = upa.user_id
            AND upa.type IN :#{#agreementTypes.![name()]}
        WHERE uhs.stock_code = :stockCode
            AND u.status = 'ACTIVE'
        GROUP BY u.push_token, u.created_at
        HAVING COUNT(upa.id) FILTER (WHERE upa.is_agree_to_receive = TRUE) > 0
            OR COUNT(upa.id) = 0
        ORDER BY u.created_at
        """, nativeQuery = true)
    List<String> findAllPushTokensByStockCode(
        String stockCode,
        List<UserPushAgreementType> agreementTypes
    );

    @Query(value = """
        SELECT u.push_token
        FROM users u
        INNER JOIN user_holding_stocks uhs ON u.id = uhs.user_id
        INNER JOIN stock_group_mappings sgm ON uhs.stock_code = sgm.stock_code
            AND sgm.stock_group_id = :stockGroupId
        LEFT JOIN user_push_agreements upa ON u.id = upa.user_id
            AND upa.type IN :#{#agreementTypes.![name()]}
        WHERE u.status = 'ACTIVE'
        GROUP BY u.push_token, u.created_at
        HAVING COUNT(upa.id) FILTER (WHERE upa.is_agree_to_receive = TRUE) > 0
            OR COUNT(upa.id) = 0
        ORDER BY u.created_at
        """, nativeQuery = true)
    List<String> findAllPushTokensByStockGroup(
        Long stockGroupId,
        List<UserPushAgreementType> agreementTypes
    );

    @Query(value = """
        SELECT u.push_token
        FROM users u
        INNER JOIN posts post
            ON u.id = post.user_id
        INNER JOIN pushes push
            ON post.id = push.post_id
        INNER JOIN automated_author_pushes aap
            ON post.id = aap.content_id
        LEFT JOIN user_push_agreements upa
            ON u.id = upa.user_id
            AND upa.type IN :#{#agreementTypes.![name()]}
        WHERE push.id = :pushId
            AND aap.id = :automatedAuthorPushId
            AND u.status = 'ACTIVE'
        GROUP BY u.push_token, u.created_at
        HAVING COUNT(upa.id) FILTER (WHERE upa.is_agree_to_receive = TRUE) > 0
            OR COUNT(upa.id) = 0
        ORDER BY u.created_at
        """, nativeQuery = true)
    List<String> findAllPushTokensForPostAuthor(
        Long pushId,
        Long automatedAuthorPushId,
        List<UserPushAgreementType> agreementTypes
    );

    @Query(value = """
        SELECT u.push_token
        FROM users u
        INNER JOIN comments comment
            ON u.id = comment.user_id
        INNER JOIN posts post
            ON comment.post_id = post.id
        INNER JOIN automated_author_pushes aap
            ON comment.id = aap.content_id
        INNER JOIN pushes push
            ON post.id = push.post_id
        LEFT JOIN user_push_agreements upa
            ON u.id = upa.user_id
            AND upa.type IN :#{#agreementTypes.![name()]}
        WHERE push.id = :pushId
            AND aap.id = :automatedAuthorPushId
            AND u.status = 'ACTIVE'
        GROUP BY u.push_token, u.created_at
        HAVING COUNT(upa.id) FILTER (WHERE upa.is_agree_to_receive = TRUE) > 0
            OR COUNT(upa.id) = 0
        ORDER BY u.created_at
        """, nativeQuery = true)
    List<String> findAllPushTokensForCommentAuthor(
        Long pushId,
        Long automatedAuthorPushId,
        List<UserPushAgreementType> agreementTypes
    );

    @Query("""
        select u
        from User u
        inner join StockAcceptorUser sau on u.id = sau.userId
        where sau.stockCode = :stockCode
        and u.status = 'ACTIVE'
        """)
    Optional<User> findAcceptorByStockCode(@Param("stockCode") String stockCode);

    @Query("""
        select u
        from User u
        inner join StockAcceptorUser sau on u.id = sau.userId
        where u.id = :userId
        and sau.stockCode = :stockCode
        and u.status = 'ACTIVE'
        """)
    Optional<User> findAcceptorByUserIdAndStockCode(@Param("userId") Long userId, @Param("stockCode") String stockCode);

    @Query("""
        SELECT new ag.act.dto.SimpleUserDto(u.id, u.birthDate, u.createdAt, u.gender)
        FROM User u
        WHERE u.status = 'ACTIVE'
        """)
    Page<SimpleUserDto> findActiveUsers(Pageable pageable);

    @Query("""
        SELECT new ag.act.dto.SimpleUserDto(u.id, u.birthDate, u.createdAt, u.gender)
        FROM User u
        WHERE u.id IN :userIds
        """)
    List<SimpleUserDto> findAllSimpleUsersInAllStatus(List<Long> userIds);

    @Query("""
        select u
        from User u
        inner join SolidarityLeaderApplicant sla on u.id = sla.userId
        where sla.id = :applicantId
        and u.status = 'ACTIVE'
        """)
    Optional<User> findUserByApplicantId(Long applicantId);
}
