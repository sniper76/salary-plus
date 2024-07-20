package ag.act.converter;

import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.user.SimpleUserProfileDto;
import ag.act.entity.CommentUserProfile;
import ag.act.entity.ContentUserProfile;
import ag.act.entity.PostUserProfile;
import ag.act.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;

@Component
@RequiredArgsConstructor
public class ContentUserProfileConverter {
    private final ContentUserProfileBadgeVisibilitySetter contentUserProfileBadgeVisibilitySetter;
    private final ContentUserProfileAnonymousFieldsSetter contentUserProfileAnonymousFieldsSetter;

    public PostUserProfile convertPostUserProfile(
        Long userId,
        SimpleUserProfileDto simpleUserProfileDto,
        Boolean isAnonymous,
        Boolean isSolidarityLeader
    ) {
        return (PostUserProfile) convert(userId, simpleUserProfileDto, isAnonymous, isSolidarityLeader, PostUserProfile.class);
    }

    public CommentUserProfile convertCommentUserProfile(
        Long userId,
        SimpleUserProfileDto simpleUserProfileDto,
        Boolean isAnonymous,
        Boolean isSolidarityLeader
    ) {
        return (CommentUserProfile) convert(userId, simpleUserProfileDto, isAnonymous, isSolidarityLeader, CommentUserProfile.class);
    }

    private ContentUserProfile convert(
        Long userId,
        SimpleUserProfileDto simpleUserProfileDto,
        Boolean isAnonymous,
        Boolean isSolidarityLeader,
        Class<?> target
    ) {
        try {
            Constructor<?> constructor = target.getDeclaredConstructor();
            ContentUserProfile targetInstance = (ContentUserProfile) constructor.newInstance();

            targetInstance.setNickname(simpleUserProfileDto.getNickname());
            targetInstance.setIndividualStockCountLabel(simpleUserProfileDto.getIndividualStockCountLabel());
            targetInstance.setTotalAssetLabel(simpleUserProfileDto.getTotalAssetLabel());
            targetInstance.setProfileImageUrl(simpleUserProfileDto.getProfileImageUrl());
            targetInstance.setUserIp(RequestContextHolder.getUserIP());

            if (isAnonymous) {
                contentUserProfileAnonymousFieldsSetter.setFieldsForAnonymous(targetInstance);
            } else {
                contentUserProfileBadgeVisibilitySetter.setBadgeVisibilities(userId, targetInstance);
                targetInstance.setIsSolidarityLeader(isSolidarityLeader);
            }

            return targetInstance;
        } catch (Exception e) {
            throw new InternalServerException("요청을 처리하던 중에 알 수 없는 오류가 발생하였습니다. 잠시 후 다시 이용해 주세요.", e);
        }
    }
}
