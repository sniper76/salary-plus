package ag.act.service.push;

import ag.act.entity.AutomatedAuthorPush;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.enums.AppLinkType;
import ag.act.enums.AutomatedPushContentType;
import ag.act.enums.AutomatedPushCriteria;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
import ag.act.exception.BadRequestException;
import ag.act.repository.AutomatedAuthorPushRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AutomatedAuthorPushService {
    private final AutomatedAuthorPushRepository automatedAuthorPushRepository;
    private final PushService pushService;

    public AutomatedAuthorPush save(AutomatedAuthorPush automatedAuthorPush) {
        return automatedAuthorPushRepository.save(automatedAuthorPush);
    }

    public Optional<AutomatedAuthorPush> getAutomatedAuthorPush(
        Long contentId, AutomatedPushContentType contentType, AutomatedPushCriteria criteria, Long sumCount
    ) {
        return automatedAuthorPushRepository.findByContentIdAndContentTypeAndCriteriaAndCriteriaValue(
            contentId, contentType, criteria, sumCount
        );
    }

    public AutomatedAuthorPush findByPushId(Long pushId) {
        return automatedAuthorPushRepository.findByPushId(pushId)
            .orElseThrow(() -> new BadRequestException("개인 자동 푸쉬 정보가 없습니다"));
    }

    public void createAutomatedAuthorPushForPostUserLike(Post post, Long likeCount) {
        createAutomatedAuthorWithPush(post, post.getId(), likeCount, AutomatedPushCriteria.LIKE, AutomatedPushContentType.POST);
    }

    public void createAutomatedAuthorPushForPostCommentCount(Post post, Long parentCommentId, Long commentCount) {
        if (parentCommentId > 0) {
            createAutomatedAuthorWithPush(post, parentCommentId, commentCount, AutomatedPushCriteria.REPLY, AutomatedPushContentType.COMMENT);
        } else {
            createAutomatedAuthorWithPush(post, post.getId(), commentCount, AutomatedPushCriteria.COMMENT, AutomatedPushContentType.POST);
        }
    }

    private void createAutomatedAuthorWithPush(
        Post post, Long contentId, Long sumCount, AutomatedPushCriteria criteria, AutomatedPushContentType contentType
    ) {
        if (!criteria.canSendPush(sumCount.intValue())) {
            return;
        }

        if (getAutomatedAuthorPush(contentId, contentType, criteria, sumCount).isPresent()) {
            return;
        }
        final Push push = createPush(post, criteria);

        final AutomatedAuthorPush automatedAuthorPush = new AutomatedAuthorPush();
        automatedAuthorPush.setCriteria(criteria);
        automatedAuthorPush.setPushId(push.getId());
        automatedAuthorPush.setContentId(contentId);
        automatedAuthorPush.setContentType(contentType);
        automatedAuthorPush.setCriteriaValue(sumCount.intValue());
        save(automatedAuthorPush);
    }

    private Push createPush(Post post, AutomatedPushCriteria criteria) {
        return pushService.createPush(new ag.act.model.CreatePushRequest()
            .stockTargetType(PushTargetType.AUTOMATED_AUTHOR.name())
            .sendType(PushSendType.IMMEDIATELY.name())
            .title(criteria.getTitle())
            .content(criteria.getMessage())
            .linkType(AppLinkType.LINK.name())
            .postId(post.getId()));
    }
}
