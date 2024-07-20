package ag.act.repository;

import ag.act.entity.UserBadgeVisibility;
import ag.act.enums.UserBadgeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeVisibilityRepository extends JpaRepository<UserBadgeVisibility, Long> {
    List<UserBadgeVisibility> findAllByUserId(Long userId);

    Optional<UserBadgeVisibility> findByUserIdAndType(Long userId, UserBadgeType userBadgeType);
}
