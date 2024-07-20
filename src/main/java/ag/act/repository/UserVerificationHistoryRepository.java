package ag.act.repository;

import ag.act.entity.UserVerificationHistory;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserVerificationHistoryRepository extends JpaRepository<UserVerificationHistory, Long> {
    Optional<UserVerificationHistory> findFirstByUserIdOrderByIdDesc(Long userId);

    Optional<Long> countByVerificationTypeAndOperationTypeAndCreatedAtBetween(
        VerificationType verificationType, VerificationOperationType operationType,
        LocalDateTime startDateTime, LocalDateTime endDateTime
    );

    @Query("""
        SELECT count(distinct uvh.userId)
        FROM UserVerificationHistory uvh
        WHERE uvh.verificationType = 'PIN'
        AND uvh.operationType = 'VERIFICATION'
        AND uvh.createdAt between :startDateTime and :endDateTime
        """)
    Optional<Long> countByActiveUser(
        LocalDateTime startDateTime, LocalDateTime endDateTime
    );

    @Query(value = """
        select coalesce(sum(a.loginCnt)/sum(a.userCnt), 0)
        from (
            select count(id) as loginCnt, 1 as userCnt
            from user_verification_histories
            where operation_type = 'VERIFICATION'
            and verification_type = 'PIN'
            and created_at between :startDateTime and :endDateTime
            group by user_id
        ) a
        """, nativeQuery = true)
    double findAverageLoginCountByUser(LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query(value = """
        select coalesce(sum(a.loginCnt)/(select count(1) from users where status = 'ACTIVE'), 0) * 100
        from (
            select count(distinct user_id) as loginCnt
            from user_verification_histories
            where operation_type = 'VERIFICATION'
            and verification_type = 'PIN'
            and created_at between :startDateTime and :endDateTime
        ) a
        """, nativeQuery = true)
    double findReuseAverageByUser(LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<UserVerificationHistory> findByDigitalDocumentUserId(Long digitalDocumentUserId);

    // 사용자의 최초 인증 히스토리 조회
    Optional<UserVerificationHistory> findFirstByUserIdAndVerificationTypeAndOperationTypeOrderByCreatedAtAsc(
            Long userId, VerificationType verificationType, VerificationOperationType operationType
    );

    // 사용자의 가장 최근 인증 히스토리 조회
    Optional<UserVerificationHistory> findFirstByUserIdAndVerificationTypeAndOperationTypeOrderByCreatedAtDesc(
        Long userId, VerificationType verificationType, VerificationOperationType operationType
    );

    // 사용자의 가장 최근 인증 히스토리 조회 (특정 디지털 문서)
    Optional<UserVerificationHistory> findFirstByUserIdAndVerificationTypeAndOperationTypeAndDigitalDocumentUserIdOrderByCreatedAtDesc(
        Long userId, VerificationType verificationType, VerificationOperationType operationType, Long digitalDocumentUserId
    );

    @Query(value = """
        SELECT COUNT(DISTINCT oldest_digital_docs_signature.user_id)
        FROM (
            SELECT uvh.user_id, MIN(uvh.created_at) AS created_at
            FROM user_verification_histories uvh
            WHERE uvh.verification_type = 'SIGNATURE'
            AND uvh.operation_type = 'SIGNATURE'
            GROUP BY uvh.user_id
        ) AS oldest_digital_docs_signature
        WHERE oldest_digital_docs_signature.created_at >= :startDateTime 
          AND oldest_digital_docs_signature.created_at < :endDateTime
        """, nativeQuery = true)
    long countFirstDigitalDocumentSaveDuring(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    @Query(value = """
        SELECT COUNT(DISTINCT uvh.user_id)
        FROM user_verification_histories uvh
        WHERE uvh.user_id IN (
            SELECT DISTINCT oldest_digital_docs_signature.user_id
            FROM (
                SELECT uvh2.user_id, MIN(uvh2.created_at) AS created_at
                FROM user_verification_histories uvh2
                WHERE uvh2.verification_type = 'SIGNATURE'
                AND uvh2.operation_type = 'SIGNATURE'
                GROUP BY uvh2.user_id
            ) AS oldest_digital_docs_signature
            WHERE oldest_digital_docs_signature.created_at >= :referenceWeekDateTime 
              AND oldest_digital_docs_signature.created_at < :referenceWeekNextWeekDateTime
        )
        AND uvh.verification_type = 'PIN'
        AND uvh.operation_type = 'VERIFICATION'
        AND uvh.created_at >= :startDateTime 
        AND uvh.created_at < :endDateTime
        """, nativeQuery = true)
    long countPinVerificationWeeklyGivenFirstDigitalDocumentSaveDuring(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime referenceWeekNextWeekDateTime,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    @Query(value = """
        SELECT COUNT(DISTINCT uvh.user_id)
        FROM user_verification_histories uvh
        INNER JOIN my_data_summaries mds ON mds.user_id = uvh.user_id
        WHERE uvh.verification_type = 'PIN'
        AND uvh.operation_type = 'REGISTER'
        AND uvh.created_at >= :startDateTime 
        AND uvh.created_at < :endDateTime
        AND exists(
            SELECT 1
            FROM user_verification_histories uvh2
            WHERE uvh2.user_id = uvh.user_id
            AND uvh2.verification_type = 'SIGNATURE'
            AND uvh2.operation_type = 'SIGNATURE'
        )
        """, nativeQuery = true)
    long countUserWithDigitalDocumentSignaturePinRegistrationBetween(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    @Query(value = """
        SELECT COUNT(DISTINCT uvh.user_id)
        FROM user_verification_histories uvh
        INNER JOIN my_data_summaries mds ON mds.user_id = uvh.user_id
        WHERE uvh.verification_type = 'PIN'
        AND uvh.operation_type = 'REGISTER'
        AND uvh.created_at >= :referenceWeekDateTime 
        AND uvh.created_at < :referenceWeekNextWeekDateTime
        AND exists(
            SELECT 1
            FROM user_verification_histories uvh1
            WHERE uvh1.user_id = uvh.user_id
            AND uvh1.verification_type = 'PIN'
            AND uvh1.operation_type = 'VERIFICATION'
            AND uvh1.created_at >= :startDateTime 
            AND uvh1.created_at < :endDateTime
        )
        AND exists(
            SELECT 1
            FROM user_verification_histories uvh2
            WHERE uvh2.user_id = uvh.user_id
            AND uvh2.verification_type = 'SIGNATURE'
            AND uvh2.operation_type = 'SIGNATURE'
        )
        """, nativeQuery = true)
    long countPinVerificationOfUserWithDigitalDocumentSignatureRegisteredDuringReferenceWeek(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime referenceWeekNextWeekDateTime,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    @Query(value = """
        SELECT MIN(csv_indicator)
          FROM (
                SELECT CASE WHEN dd.type = 'DIGITAL_PROXY' THEN 'a'
                            WHEN dd.type = 'JOINT_OWNERSHIP_DOCUMENT' THEN 'b'
                            WHEN dd.type = 'ETC_DOCUMENT' THEN 'c'
                        END AS csv_indicator                   
                  FROM user_verification_histories uvh
                 INNER JOIN my_data_summaries mds 
                    ON mds.user_id = uvh.user_id
                 INNER JOIN digital_document_users ddu 
                    ON ddu.user_id = uvh.user_id
                   AND ddu.created_at >= :startDateTime
                   AND ddu.created_at < :endDateTime
                 INNER JOIN digital_documents dd 
                    ON dd.id = ddu.digital_document_id
                 WHERE uvh.user_id = :userId
                   AND EXISTS
                            ( SELECT 1
                                FROM user_verification_histories uvh1
                               WHERE uvh1.user_id = uvh.user_id
                                 AND uvh1.verification_type = 'PIN'
                                 AND uvh1.operation_type = 'VERIFICATION'
                                 AND uvh1.created_at >= :startDateTime
                                 AND uvh1.created_at < :endDateTime
                            )
                   AND EXISTS
                            ( SELECT 1
                               FROM user_verification_histories uvh2
                              WHERE uvh2.user_id = uvh.user_id
                                AND uvh2.verification_type = 'SIGNATURE'
                                AND uvh2.operation_type = 'SIGNATURE'
                            )
               ) result
        """, nativeQuery = true)
    String getCsvIndicatorByUserIdAndDigitalDocumentSignatureDuring(
        Long userId,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    @Query(value = """
        SELECT COUNT(DISTINCT uvh.user_id)
        FROM user_verification_histories uvh
        INNER JOIN my_data_summaries mds ON mds.user_id = uvh.user_id
        WHERE uvh.verification_type = 'PIN'
        AND uvh.operation_type = 'REGISTER'
        AND uvh.created_at >= :startDateTime 
        AND uvh.created_at < :endDateTime
        AND NOT EXISTS(
            SELECT 1
            FROM user_verification_histories uvh2
            WHERE uvh2.verification_type = 'SIGNATURE'
            AND uvh2.operation_type = 'SIGNATURE'
            AND uvh.user_id = uvh2.user_id
        )
        """, nativeQuery = true)
    long countUserWithoutDigitalDocumentSignaturePinRegistrationBetween(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    @Query(value = """
        SELECT COUNT(DISTINCT(uvh.user_id))
        FROM user_verification_histories uvh
        INNER JOIN my_data_summaries mds ON mds.user_id = uvh.user_id
        WHERE uvh.verification_type = 'PIN'
        AND uvh.operation_type = 'REGISTER'
        AND uvh.created_at >= :referenceWeekDateTime
        AND uvh.created_at < :referenceWeekNextWeekDateTime
        AND EXISTS(
            SELECT 1
            FROM user_verification_histories uvh2
            WHERE uvh2.verification_type = 'PIN'
            AND uvh2.operation_type = 'VERIFICATION'
            AND uvh2.created_at >= :startDateTime
            AND uvh2.created_at < :endDateTime
            AND uvh.user_id = uvh2.user_id
        )
        AND NOT EXISTS(
            SELECT 1
            FROM user_verification_histories uvh3
            WHERE uvh3.verification_type = 'SIGNATURE'
            AND uvh3.operation_type = 'SIGNATURE'
            AND uvh.user_id = uvh3.user_id
        )
        """, nativeQuery = true)
    long countPinVerificationOfUserWithoutDigitalDocumentSignatureRegisteredDuringReferenceWeek(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime referenceWeekNextWeekDateTime,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    @Query(value = """
        SELECT COUNT(DISTINCT uvh.user_id)
        FROM user_verification_histories uvh
        INNER JOIN my_data_summaries mds ON mds.user_id = uvh.user_id
        WHERE uvh.verification_type = 'PIN'
        AND uvh.operation_type = 'REGISTER'
        AND uvh.created_at >= :referenceWeekDateTime
        AND uvh.created_at < :referenceWeekNextWeekDateTime
        AND EXISTS(
            SELECT 1
            FROM user_verification_histories uvh_signature
            WHERE uvh_signature.verification_type = 'SIGNATURE'
            AND uvh_signature.operation_type = 'SIGNATURE'
            AND uvh.user_id = uvh_signature.user_id
        )
        AND NOT EXISTS(
            SELECT 1
            FROM user_verification_histories uvh_verification
            WHERE uvh_verification.verification_type = 'PIN'
            AND uvh_verification.operation_type = 'VERIFICATION'
            AND uvh_verification.created_at >= :startDateTime
            AND uvh_verification.created_at < :endDateTime
            AND uvh.user_id = uvh_verification.user_id
        )
        """, nativeQuery = true)
    long countNonRetainedUserWithDigitalDocumentSignatureRegisteredDuringReferenceWeek(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime referenceWeekNextWeekDateTime,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    @Query(value = """
        SELECT COUNT(DISTINCT uvh.user_id)
        FROM user_verification_histories uvh
        INNER JOIN my_data_summaries mds ON mds.user_id = uvh.user_id
        WHERE uvh.verification_type = 'PIN'
        AND uvh.operation_type = 'REGISTER'
        AND uvh.created_at >= :referenceWeekDateTime
        AND uvh.created_at < :referenceWeekNextWeekDateTime
        AND NOT EXISTS(
            SELECT 1
            FROM user_verification_histories uvh_signature
            WHERE uvh_signature.verification_type = 'SIGNATURE'
            AND uvh_signature.operation_type = 'SIGNATURE'
            AND uvh.user_id = uvh_signature.user_id
        )
        AND NOT EXISTS(
            SELECT 1
            FROM user_verification_histories uvh_verification
            WHERE uvh_verification.verification_type = 'PIN'
            AND uvh_verification.operation_type = 'VERIFICATION'
            AND uvh_verification.created_at >= :startDateTime
            AND uvh_verification.created_at < :endDateTime
            AND uvh.user_id = uvh_verification.user_id
        )
        """, nativeQuery = true)
    long countNonRetainedUserWithoutDigitalDocumentSignatureRegisteredDuringReferenceWeek(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime referenceWeekNextWeekDateTime,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    @Query(value = """
            SELECT COUNT(DISTINCT uvh.user_id)
            FROM user_verification_histories uvh
            WHERE 1=1
            AND EXISTS(
                SELECT 1 
                    FROM my_data_summaries mds
                    WHERE mds.user_id = uvh.user_id
                      AND uvh.verification_type = 'PIN'
                      AND uvh.operation_type = 'REGISTER'
                      AND uvh.created_at >= :referenceWeekDateTime 
                      AND uvh.created_at < :referenceWeekNextWeekDateTime
            )
            AND EXISTS(
                SELECT 1 
                FROM user_verification_histories uvh_signature
                WHERE uvh.user_id = uvh_signature.user_id
                  AND uvh_signature.verification_type = 'SIGNATURE'
                  AND uvh_signature.operation_type = 'SIGNATURE'
            )
            AND EXISTS (
                SELECT 1 
                FROM user_verification_histories  uvh_verification_week1
                WHERE uvh.user_id = uvh_verification_week1.user_id
                  AND uvh_verification_week1.verification_type = 'PIN'
                  AND uvh_verification_week1.operation_type = 'VERIFICATION'
                  AND uvh_verification_week1.created_at >= :startDateTime
                  AND uvh_verification_week1.created_at < :secondWeekStartDateTime
                )
            AND EXISTS (
                SELECT 1 
                FROM user_verification_histories  uvh_verification_week2
                WHERE uvh.user_id = uvh_verification_week2.user_id
                  AND uvh_verification_week2.verification_type = 'PIN'
                  AND uvh_verification_week2.operation_type = 'VERIFICATION'
                  AND uvh_verification_week2.created_at >= :secondWeekStartDateTime
                  AND uvh_verification_week2.created_at < :thirdWeekStartDateTime
                )
            AND EXISTS (
                SELECT 1 
                FROM user_verification_histories  uvh_verification_week3
                WHERE uvh.user_id = uvh_verification_week3.user_id
                  AND uvh_verification_week3.verification_type = 'PIN'
                  AND uvh_verification_week3.operation_type = 'VERIFICATION'
                  AND uvh_verification_week3.created_at >= :thirdWeekStartDateTime
                  AND uvh_verification_week3.created_at < :fourthWeekStartDateTime
            )
        """, nativeQuery = true)
    long countPinVerificationOfUserForThreeWeeksInARowWithDigitalDocumentSignature(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime referenceWeekNextWeekDateTime,
        LocalDateTime startDateTime,
        LocalDateTime secondWeekStartDateTime,
        LocalDateTime thirdWeekStartDateTime,
        LocalDateTime fourthWeekStartDateTime
    );


    @Query(value = """
        SELECT COUNT(DISTINCT uvh.user_id)
            FROM user_verification_histories uvh
            WHERE 1=1
            AND EXISTS(
                SELECT 1 
                    FROM my_data_summaries mds
                    WHERE mds.user_id = uvh.user_id
                      AND uvh.verification_type = 'PIN'
                      AND uvh.operation_type = 'REGISTER'
                      AND uvh.created_at >= :referenceWeekDateTime 
                      AND uvh.created_at < :referenceWeekNextWeekDateTime
            )
            AND NOT EXISTS(
                SELECT 1 
                FROM user_verification_histories uvh_signature
                WHERE uvh.user_id = uvh_signature.user_id
                  AND uvh_signature.verification_type = 'SIGNATURE'
                  AND uvh_signature.operation_type = 'SIGNATURE'
            )
            AND EXISTS (
                SELECT 1 
                FROM user_verification_histories  uvh_verification_week1
                WHERE uvh.user_id = uvh_verification_week1.user_id
                  AND uvh_verification_week1.verification_type = 'PIN'
                  AND uvh_verification_week1.operation_type = 'VERIFICATION'
                  AND uvh_verification_week1.created_at >= :startDateTime
                  AND uvh_verification_week1.created_at < :secondWeekStartDateTime
                )
            AND EXISTS (
                SELECT 1 
                FROM user_verification_histories  uvh_verification_week2
                WHERE uvh.user_id = uvh_verification_week2.user_id
                  AND uvh_verification_week2.verification_type = 'PIN'
                  AND uvh_verification_week2.operation_type = 'VERIFICATION'
                  AND uvh_verification_week2.created_at >= :secondWeekStartDateTime
                  AND uvh_verification_week2.created_at < :thirdWeekStartDateTime
                )
            AND EXISTS (
                SELECT 1 
                FROM user_verification_histories  uvh_verification_week3
                WHERE uvh.user_id = uvh_verification_week3.user_id
                  AND uvh_verification_week3.verification_type = 'PIN'
                  AND uvh_verification_week3.operation_type = 'VERIFICATION'
                  AND uvh_verification_week3.created_at >= :thirdWeekStartDateTime
                  AND uvh_verification_week3.created_at < :fourthWeekStartDateTime
            )
        """, nativeQuery = true)
    long countPinVerificationOfUserForThreeWeeksInARowWithNoDigitalDocumentSignature(
        LocalDateTime referenceWeekDateTime,
        LocalDateTime referenceWeekNextWeekDateTime,
        LocalDateTime startDateTime,
        LocalDateTime secondWeekStartDateTime,
        LocalDateTime thirdWeekStartDateTime,
        LocalDateTime fourthWeekStartDateTime
    );

    boolean existsByUserIdAndOperationTypeAndVerificationTypeAndCreatedAtIsGreaterThanEqualAndCreatedAtIsLessThan(
        Long userId,
        VerificationOperationType verificationOperationType,
        VerificationType verificationType,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    @Query(value = """
            SELECT COUNT(DISTINCT new_users.user_id)
             FROM (
               SELECT uvh.user_id
               FROM user_verification_histories uvh
               WHERE uvh.verification_type = 'PIN'
                AND uvh.operation_type = 'REGISTER'
                AND uvh.created_at >= :referenceWeekStartDate
                AND uvh.created_at < :referenceWeekNextWeek
                  ) AS new_users
             INNER JOIN my_data_summaries mds 
                ON mds.user_id = new_users.user_id
             INNER JOIN digital_document_users ddu
                ON ddu.user_id = new_users.user_id
             INNER JOIN digital_documents dd
                ON dd.id = ddu.digital_document_id
                AND dd.id IN :digitalDocumentIds
             WHERE ddu.digital_document_answer_status = 'COMPLETE'
                AND exists(
                    SELECT 1
                    FROM user_verification_histories uvh2
                    WHERE uvh2.verification_type = 'PIN'
                    AND uvh2.operation_type = 'VERIFICATION'
                    AND uvh2.created_at >= :startDateTime
                    AND uvh2.created_at < :endDateTime
                    AND new_users.user_id = uvh2.user_id
                )
        """, nativeQuery = true)
    long countPinVerificationOfUserBetweenAndSpecificDigitalDocumentCompleteAndRegisteredBetween(
        List<Long> digitalDocumentIds,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        LocalDateTime referenceWeekStartDate,
        LocalDateTime referenceWeekNextWeek
    );

    @Query(value = """
            SELECT COUNT(DISTINCT(u.id))
            FROM users u
            INNER JOIN digital_document_users ddu
                ON ddu.user_id = u.id
               AND ddu.digital_document_id IN :digitalDocumentIds
            INNER JOIN digital_documents dd
                ON dd.id = ddu.digital_document_id
            INNER JOIN user_verification_histories uvh
                ON uvh.user_id = u.id
            WHERE uvh.verification_type = 'PIN'
                AND uvh.operation_type = 'REGISTER'
                AND uvh.created_at >= :startDateTime 
                AND uvh.created_at < :endDateTime
                AND EXISTS(
                    SELECT 1
                    FROM user_verification_histories uvh2
                    WHERE ddu.digital_document_answer_status = 'COMPLETE'
                    AND uvh2.operation_type = 'SIGNATURE'
                    AND uvh2.verification_type = 'SIGNATURE'
                    AND uvh2.digital_document_user_id = ddu.id
                )
        """, nativeQuery = true)
    long countNewRegisteredUserBetweenByDigitalDocumentIds(
        List<Long> digitalDocumentIds,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
    );

    @Query(value = """
        SELECT COUNT(DISTINCT (new_users.user_id))
          FROM (
            SELECT uvh.user_id
            FROM user_verification_histories uvh
            WHERE uvh.verification_type = 'PIN'
             AND uvh.operation_type = 'REGISTER'
             AND uvh.created_at >= :referenceWeekStartDate
             AND uvh.created_at < :referenceWeekNextWeek
               ) AS new_users
          WHERE not exists(
            select 1
            FROM user_verification_histories uvh1
            INNER JOIN digital_document_users ddu
             ON ddu.user_id = new_users.user_id
            INNER JOIN digital_documents dd
             ON dd.id = ddu.digital_document_id
             AND dd.id IN :digitalDocumentIds
            AND ddu.digital_document_answer_status = 'COMPLETE'
            WHERE uvh1.user_id = new_users.user_id
          )
          AND exists(
             SELECT 1
             FROM user_verification_histories uvh2
             WHERE uvh2.verification_type = 'PIN'
             AND uvh2.operation_type = 'VERIFICATION'
             AND uvh2.created_at >= :startDateTime
             AND uvh2.created_at < :endDateTime
             AND new_users.user_id = uvh2.user_id
          )     
          """, nativeQuery = true)
    long countPinVerificationOfUserBetweenAndSpecificDigitalDocumentNotCompleteAndRegisterBetween(
        List<Long> digitalDocumentIds,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        LocalDateTime referenceWeekStartDate,
        LocalDateTime referenceWeekNextWeek
    );

    @Query(value = """
            SELECT COUNT(DISTINCT(u.id))
            FROM users u
            INNER JOIN user_verification_histories uvh
                ON uvh.user_id = u.id
            WHERE uvh.verification_type = 'PIN'
                AND uvh.operation_type = 'REGISTER'
                AND uvh.created_at >= :startDateTime
                AND uvh.created_at < :endDateTime
                AND not exists(
                   SELECT 1
                   FROM user_verification_histories uvh1
                   INNER JOIN digital_document_users ddu
                    ON ddu.user_id = u.id
                   INNER JOIN digital_documents dd
                    ON dd.id = ddu.digital_document_id
                    AND dd.id IN :digitalDocumentIds
                   AND ddu.digital_document_answer_status = 'COMPLETE'
                   WHERE uvh1.user_id = u.id
                )
        """, nativeQuery = true)
    long countNewRegisteredUserBetweenAndNotCompletedSpecificDigitalDocument(
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        List<Long> digitalDocumentIds
    );

}
