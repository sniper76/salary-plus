package ag.act.repository;

import ag.act.entity.CommentUserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentUserProfileRepository extends JpaRepository<CommentUserProfile, Long> {
}
