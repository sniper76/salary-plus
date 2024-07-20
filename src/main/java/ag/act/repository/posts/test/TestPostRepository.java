package ag.act.repository.posts.test;

import ag.act.entity.Post;
import ag.act.entity.posts.test.TestPost;
import ag.act.enums.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TestPostRepository extends JpaRepository<TestPost, Long> {

    @Query(value = """
        select p
        from TestUser tu
        inner join TestPost tp on 1=1
        inner join Post p on tp.postId = p.id
        join fetch p.board
        where tu.userId = :userId
        and tp.targetEndDate > :targetEndDate
        and (p.board.category = :boardCategory or :boardCategory is null)
        and p.board.stockCode = :stockCode
        and p.status = 'INACTIVE_BY_ADMIN'
        """)
    List<Post> findAllByUserIdAndTargetEndDateAndCategoryAndStockCode(
        Long userId, LocalDateTime targetEndDate, BoardCategory boardCategory, String stockCode
    );
}
