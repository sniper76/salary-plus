package ag.act.converter.home;

import ag.act.converter.DateTimeConverter;
import ag.act.converter.post.BoardCategoryResponseConverter;
import ag.act.converter.user.UserProfileResponseForDetailsConverter;
import ag.act.core.annotation.ContentOverrider;
import ag.act.entity.Post;
import ag.act.model.SectionItemResponse;
import ag.act.module.cache.PostPreference;
import ag.act.util.AppLinkUrlGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SectionItemResponseConverter {
    private final AppLinkUrlGenerator appLinkUrlGenerator;
    private final BoardCategoryResponseConverter boardCategoryResponseConverter;
    private final UserProfileResponseForDetailsConverter userProfileResponseForDetailsConverter;
    private final PostPreference postPreference;

    @ContentOverrider
    public SectionItemResponse convert(Post post) {
        return new SectionItemResponse()
            .title(post.getTitle())
            .userProfile(userProfileResponseForDetailsConverter.convert(post.getUserId(), post.getPostUserProfile()))
            .boardCategory(boardCategoryResponseConverter.convert(post.getBoard()))
            .link(appLinkUrlGenerator.generateBoardGroupPostLinkUrl(post))
            .likeCount(post.getLikeCount())
            .viewCount(post.getViewCount())
            .commentCount(post.getCommentCount())
            .isNew(postPreference.isNew(post))
            .isExclusiveToHolders(post.getIsExclusiveToHolders())
            .createdAt(DateTimeConverter.convert(post.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(post.getEditedAt()));
    }
}
