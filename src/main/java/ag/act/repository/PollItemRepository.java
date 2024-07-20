package ag.act.repository;

import ag.act.entity.PollItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollItemRepository extends JpaRepository<PollItem, Long> {
    List<PollItem> findAllByPollId(Long pollId);
}
