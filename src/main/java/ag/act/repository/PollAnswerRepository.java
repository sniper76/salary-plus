package ag.act.repository;

import ag.act.entity.PollAnswer;
import ag.act.repository.interfaces.JoinCount;
import ag.act.repository.interfaces.PollItemCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollAnswerRepository extends JpaRepository<PollAnswer, Long> {

    List<PollAnswer> findAllByPollIdAndUserId(Long pollId, Long userId);

    @Query("""
        select pa
        from PollAnswer pa
        inner join Poll p on pa.pollId = p.id
        where p.post.id = :postId
        and pa.userId = :userId
        order by pa.pollId
        """)
    List<PollAnswer> findAllByPostIdAndUserId(Long postId, Long userId);

    @Query(value = """
        SELECT
            pa.poll_id as pollId,
            pa.poll_item_id as pollItemId,
            pa.user_id as userId,
            count(pa.user_id) over (partition by pa.poll_id, pa.poll_item_id, pa.user_id) as joinCnt,
            sum(pa.stock_quantity) over (partition by pa.poll_id, pa.poll_item_id, pa.user_id) as stockQuantity
        FROM poll_answers pa
        WHERE pa.poll_id = :pollId
        """,
        nativeQuery = true
    )
    List<PollItemCount> findPollItemCountsByPollId(@Param("pollId") Long pollId);

    @Query(value = """
        SELECT
            pa.poll_id as pollId,
            pa.poll_item_id as pollItemId,
            pa.user_id as userId,
            count(pa.user_id) over (partition by pa.poll_id, pa.poll_item_id, pa.user_id) as joinCnt,
            sum(pa.stock_quantity) over (partition by pa.poll_id, pa.poll_item_id, pa.user_id) as stockQuantity
        FROM poll_answers pa
        WHERE pa.poll_id in (select id from polls where post_id = :postId)
        order by pa.poll_id
        """,
        nativeQuery = true
    )
    List<PollItemCount> findPollItemCountsByPostId(@Param("postId") Long postId);

    @Query(value = """
        SELECT
            sum(1) as joinCnt,
            sum(pa.stock_quantity)  as stockQuantity
        FROM poll_answers pa
        inner join polls p on pa.poll_id = p.id
        inner join posts p2 on p.id = p2.poll_id
        where (p2.id = :postId or p2.source_post_id = :postId)
        """, nativeQuery = true)
    Optional<JoinCount> findPollCountByPostId(@Param("postId") Long postId);

    List<PollAnswer> findAllByPollId(Long pollId);

    @Query("""
        select pa
        from PollAnswer pa
        inner join Poll p on pa.pollId = p.id
        where p.post.id = :postId
        order by pa.pollId
        """)
    List<PollAnswer> findPollAnswersByPostId(@Param("postId") Long postId);

    @Query("""
        select COALESCE(SUM(pa.stockQuantity), 0) as stockQuantity
        from PollAnswer pa
        inner join PollItem pi on pa.pollItemId = pi.id
        inner join Poll p on pi.poll.id = p.id
        inner join SolidarityLeaderElectionPollItemMapping m on pi.id = m.pollItemId
        where p.post.id = :postId
        and m.solidarityLeaderApplicantId = :solidarityLeaderApplicantId
        and m.electionAnswerType = ag.act.enums.solidarity.election.SolidarityLeaderElectionAnswerType.APPROVAL
        """)
    long sumApprovalPollAnswersByPostId(@Param("postId") Long postId, @Param("solidarityLeaderApplicantId") Long solidarityLeaderApplicantId);
}
