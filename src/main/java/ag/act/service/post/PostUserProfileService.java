package ag.act.service.post;

import ag.act.entity.PostUserProfile;
import ag.act.exception.NotFoundException;
import ag.act.repository.PostUserProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostUserProfileService {

    private final PostUserProfileRepository postUserProfileRepository;

    public PostUserProfile save(PostUserProfile postUserProfile) {
        return postUserProfileRepository.save(postUserProfile);
    }

    public Optional<PostUserProfile> findByPostId(Long postId) {
        return postUserProfileRepository.findByPostId(postId);
    }

    public PostUserProfile getPostUserProfileNotNull(Long postId) {
        return findByPostId(postId).orElseThrow(() -> new NotFoundException("게시글 작성자 정보를 찾을수 없습니다."));
    }
}
