package ag.act.repository;

import ag.act.entity.NicknameHistory;
import ag.act.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NicknameHistoryRepository extends JpaRepository<NicknameHistory, Long> {
    List<NicknameHistory> findAllByUserIdAndStatus(Long userId, Status status);

    List<NicknameHistory> findAllByUserId(Long userId);

    int countByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
