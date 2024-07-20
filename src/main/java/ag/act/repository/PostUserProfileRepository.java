package ag.act.repository;

import ag.act.entity.PostUserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostUserProfileRepository extends JpaRepository<PostUserProfile, Long> {
    Optional<PostUserProfile> findByPostId(Long postId);
}
