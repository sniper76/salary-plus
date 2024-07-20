package ag.act.converter.post.comment;

import ag.act.constants.MessageConstants;
import ag.act.converter.ContentUserProfileAnonymousFieldsSetter;
import ag.act.converter.DateTimeConverter;
import ag.act.converter.user.UserProfileResponseForDetailsConverter;
import ag.act.core.annotation.ContentOverrider;
import ag.act.entity.Comment;
import ag.act.entity.CommentUserProfile;
import ag.act.model.CommentResponse;
import ag.act.util.StatusUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentResponseConverter implements MessageConstants {
    private final UserProfileResponseForDetailsConverter userProfileResponseConverter;
    private final ContentUserProfileAnonymousFieldsSetter contentUserProfileAnonymousFieldsSetter;

    @ContentOverrider
    public CommentResponse convert(
        Comment comment,
        boolean matchAnonymousPostWriter,
        boolean liked,
        CommentUserProfile commentUserProfile,
        Integer anonymousCount
    ) {
        if (comment.getIsAnonymous()) {
            setFieldsForAnonymous(matchAnonymousPostWriter, commentUserProfile, anonymousCount);
        }

        return new CommentResponse()
            .id(comment.getId())
            .userId(comment.getUserId())
            .content(comment.getContent())
            .liked(liked)
            .deleted(StatusUtil.isDeletedContent(comment.getStatus()))
            .likeCount(comment.getLikeCount())
            .replyCommentCount(comment.isReplyComment() ? null : comment.getReplyCommentCount())
            .status(comment.getStatus().name())
            .userProfile(userProfileResponseConverter.convert(comment.getUserId(), commentUserProfile))
            .createdAt(DateTimeConverter.convert(comment.getCreatedAt()))
            .editedAt(DateTimeConverter.convert(comment.getEditedAt()))
            .updatedAt(DateTimeConverter.convert(comment.getUpdatedAt()));
    }

    @ContentOverrider
    public CommentResponse convert(
        Comment comment,
        boolean matchAnonymousPostWriter,
        boolean liked,
        CommentUserProfile commentUserProfile,
        Integer anonymousCount,
        List<CommentResponse> replyComments
    ) {
        CommentResponse response = convert(
            comment,
            matchAnonymousPostWriter,
            liked,
            commentUserProfile,
            anonymousCount
        );

        return response.replyComments(comment.isReplyComment() ? null : replyComments);
    }

    private void setFieldsForAnonymous(boolean matchAnonymousPostWriter, CommentUserProfile commentUserProfile, Integer anonymousCount) {
        if (commentUserProfile == null) {
            return;
        }

        if (matchAnonymousPostWriter) {
            contentUserProfileAnonymousFieldsSetter.setFieldsForAnonymous(commentUserProfile);
        } else {
            contentUserProfileAnonymousFieldsSetter.setFieldsForAnonymous(
                commentUserProfile,
                "%s%d".formatted(ANONYMOUS_NAME, anonymousCount)
            );
        }
    }
}
