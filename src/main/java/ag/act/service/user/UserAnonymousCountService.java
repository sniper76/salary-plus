package ag.act.service.user;

import ag.act.configuration.security.ActUserProvider;
import ag.act.core.configuration.AnonymousUserContentLimits;
import ag.act.entity.User;
import ag.act.entity.UserAnonymousCount;
import ag.act.model.AnonymousCountResponse;
import ag.act.model.GetAnonymousCountResponse;
import ag.act.repository.UserAnonymousCountRepository;
import ag.act.service.post.PostAnonymousCountService;
import ag.act.service.stockboardgrouppost.comment.CommentAnonymousCountService;
import ag.act.util.DateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserAnonymousCountService {
    private final UserAnonymousCountRepository userAnonymousCountRepository;
    private final AnonymousUserContentLimits anonymousUserContentLimits;
    private final PostAnonymousCountService postAnonymousCountService;
    private final CommentAnonymousCountService commentAnonymousCountService;

    public UserAnonymousCountService(
        AnonymousUserContentLimits anonymousUserContentLimits,
        UserAnonymousCountRepository userAnonymousCountRepository,
        PostAnonymousCountService postAnonymousCountService,
        CommentAnonymousCountService commentAnonymousCountService
    ) {
        this.userAnonymousCountRepository = userAnonymousCountRepository;
        this.anonymousUserContentLimits = anonymousUserContentLimits;
        this.postAnonymousCountService = postAnonymousCountService;
        this.commentAnonymousCountService = commentAnonymousCountService;
    }

    public ag.act.model.GetAnonymousCountResponse getUserAnonymousCount(String currentDate) {

        Optional<UserAnonymousCount> userAnonymousCount =
            userAnonymousCountRepository.findByUserIdAndWriteDate(ActUserProvider.getNoneNull().getId(), currentDate);

        ag.act.model.GetAnonymousCountResponse response = new GetAnonymousCountResponse();

        response.setData(
            new AnonymousCountResponse()
                .post(
                    new ag.act.model.AnonymousCountResponsePost()
                        .max(anonymousUserContentLimits.getPostLimitCount())
                        .current(userAnonymousCount.map(UserAnonymousCount::getPostCount).orElse(0))
                )
                .comment(
                    new ag.act.model.AnonymousCountResponsePost()
                        .max(anonymousUserContentLimits.getCommentLimitCount())
                        .current(userAnonymousCount.map(UserAnonymousCount::getCommentCount).orElse(0))
                )
        );
        return response;
    }

    public UserAnonymousCount validateAndIncreaseCommentCount(User user) {
        return userAnonymousCountRepository.save(
            commentAnonymousCountService.validateAndIncreaseCount(user, findOrCreateUserAnonymousCount(user.getId()))
        );
    }

    public UserAnonymousCount validateAndIncreasePostCount(Long userId) {
        return userAnonymousCountRepository.save(
            postAnonymousCountService.validateAndIncreaseCount(findOrCreateUserAnonymousCount(userId))
        );
    }

    private UserAnonymousCount findOrCreateUserAnonymousCount(Long userId) {
        final String writeDate = DateTimeUtil.getFormattedCurrentTimeInKorean("yyyyMMdd");

        return userAnonymousCountRepository.findByUserIdAndWriteDate(userId, writeDate)
            .orElseGet(() -> {
                final UserAnonymousCount newUserAnonymousCount = new UserAnonymousCount();
                newUserAnonymousCount.setUserId(userId);
                newUserAnonymousCount.setWriteDate(writeDate);
                newUserAnonymousCount.setCommentCount(0);
                newUserAnonymousCount.setPostCount(0);
                return newUserAnonymousCount;
            });
    }
}
