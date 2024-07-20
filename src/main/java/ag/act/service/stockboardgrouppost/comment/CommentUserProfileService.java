package ag.act.service.stockboardgrouppost.comment;

import ag.act.entity.CommentUserProfile;
import ag.act.repository.CommentUserProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CommentUserProfileService {
    private final CommentUserProfileRepository commentUserProfileRepository;

    public CommentUserProfileService(CommentUserProfileRepository commentUserProfileRepository) {
        this.commentUserProfileRepository = commentUserProfileRepository;
    }

    public CommentUserProfile save(CommentUserProfile commentUserProfile) {
        return commentUserProfileRepository.save(commentUserProfile);
    }
}
