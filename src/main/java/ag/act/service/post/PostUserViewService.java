package ag.act.service.post;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.PostUserView;
import ag.act.entity.User;
import ag.act.repository.PostUserViewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PostUserViewService {
    private final PostUserViewRepository postUserViewRepository;

    public PostUserViewService(PostUserViewRepository postUserViewRepository) {
        this.postUserViewRepository = postUserViewRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createOrUpdatePostUserViewCount(Long postId) {
        Optional<User> user = ActUserProvider.get();
        if (user.isEmpty()) {
            return;
        }

        PostUserView postUserView = findOrCreatePostUserView(postId, user.get());

        incrementViewCount(postUserView);

        postUserViewRepository.save(postUserView);
    }

    private PostUserView findOrCreatePostUserView(Long postId, User user) {
        return postUserViewRepository.findByPostIdAndUserId(postId, user.getId())
            .orElseGet(() -> {
                PostUserView newPostUserView = new PostUserView();
                newPostUserView.setPostId(postId);
                newPostUserView.setUserId(user.getId());
                newPostUserView.setCount(0L);
                return newPostUserView;
            });
    }

    private void incrementViewCount(PostUserView postUserView) {
        postUserView.setCount(postUserView.getCount() + 1);
    }

    public Long countByPostId(Long postId) {
        return postUserViewRepository.countByPostId(postId);
    }

    public Long sumViewCountByPostId(Long postId) {
        return postUserViewRepository.sumViewCountByPostId(postId).orElse(0L);
    }
}
