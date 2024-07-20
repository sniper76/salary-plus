package ag.act.core.aop;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.enums.AppPreferenceType;
import ag.act.module.cache.AppPreferenceCache;
import ag.act.module.post.PostAndCommentAopCurrentDateTimeProvider;
import ag.act.service.post.PostService;
import ag.act.validator.post.StockBoardGroupPostValidator;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Aspect
@RequiredArgsConstructor
@Component
public class BoardGroupPostCreationCheckAspect {

    private final PostService postService;
    private final StockBoardGroupPostValidator stockBoardGroupPostValidator;
    private final AppPreferenceCache appPreferenceCache;
    private final PostAndCommentAopCurrentDateTimeProvider postAndCommentAopCurrentDateTimeProvider;

    @Before("execution(* ag.act.handler.stockboardgrouppost.StockBoardGroupPostApiDelegateImpl.createBoardGroupPost(..))")
    public void checkPostCreation() {
        User user = ActUserProvider.getNoneNull();
        if (user.isAdmin()) {
            return;
        }

        validatePostCooldown(user);
    }

    private void validatePostCooldown(User user) {
        final int postCooldownSeconds = appPreferenceCache.getValue(AppPreferenceType.POST_RESTRICTION_INTERVAL_SECONDS);

        Optional<Post> latestPostOptional = findLatestPostWithin(user, postCooldownSeconds);

        latestPostOptional.ifPresent(latestPost ->
            stockBoardGroupPostValidator.validatePostCooldown(latestPost, postCooldownSeconds)
        );
    }

    private Optional<Post> findLatestPostWithin(User user, final int postCooldownSeconds) {
        LocalDateTime latestPostStartTime = postAndCommentAopCurrentDateTimeProvider.get().minusSeconds(postCooldownSeconds);

        return postService.findLatestPostFrom(user.getId(), latestPostStartTime);
    }
}
