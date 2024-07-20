package ag.act.repository;

import ag.act.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findAllByPostIdAndStatusNotIn(Long postId, List<ag.act.model.Status> statusList);
}
