package ag.act.repository;

import ag.act.entity.StopWord;
import ag.act.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StopWordRepository extends JpaRepository<StopWord, Long> {

    Page<StopWord> findAllByStatusIn(List<Status> statusList, Pageable pageable);

    Page<StopWord> findAllByStatusInAndWordContaining(List<Status> statusList, String searchKeyword, Pageable pageable);

    Optional<StopWord> findByWord(String word);

    List<StopWord> findAllByStatusIs(Status status);

}
