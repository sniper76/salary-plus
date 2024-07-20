package ag.act.repository;

import ag.act.entity.UserAnonymousCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAnonymousCountRepository extends JpaRepository<UserAnonymousCount, Long> {
    Optional<UserAnonymousCount> findByUserIdAndWriteDate(Long userId, String writeDate);
}
