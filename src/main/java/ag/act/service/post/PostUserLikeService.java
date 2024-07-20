package ag.act.service.post;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.PostUserLike;
import ag.act.repository.PostUserLikeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PostUserLikeService {
    private final PostUserLikeRepository postUserLikeRepository;

    public PostUserLikeService(PostUserLikeRepository postUserLikeRepository) {
        this.postUserLikeRepository = postUserLikeRepository;
    }

    public void deletePostUserLikeList(List<PostUserLike> deleteList) {
        postUserLikeRepository.deleteAllInBatch(deleteList);
    }

    public void savePostUserLike(PostUserLike postUserLike) {
        postUserLikeRepository.save(postUserLike);
    }

    public List<PostUserLike> findAllByPostIdAndUserId(Long postId, Long likedUserId) {
        return postUserLikeRepository.findAllByPostIdAndUserId(postId, likedUserId);
    }

    public long countByPostId(Long postId) {
        return postUserLikeRepository.countByPostId(postId);
    }

    public boolean isUserLikedPost(Long postId) {
        return ActUserProvider.get()
            .map(user -> postUserLikeRepository.existsByPostIdAndUserId(postId, user.getId()))
            .orElse(Boolean.FALSE);
    }
}
