package ag.act.converter.report;

import ag.act.converter.DateTimeConverter;
import ag.act.converter.user.UserProfileResponseForDetailsConverter;
import ag.act.entity.Comment;
import ag.act.model.CommentResponse;
import ag.act.util.StatusUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminReportCommentResponseConverter {
    private final UserProfileResponseForDetailsConverter userProfileResponseConverter;

    public CommentResponse convert(Comment comment) {
        return new CommentResponse()
            .id(comment.getId())
            .userId(comment.getUserId())
            .content(comment.getContent())
            .deleted(StatusUtil.isDeletedContent(comment.getStatus()))
            .likeCount(comment.getLikeCount())
            .replyCommentCount(comment.getReplyCommentCount())
            .userProfile(userProfileResponseConverter.convert(comment.getUserId(), comment.getCommentUserProfile()))
            .createdAt(DateTimeConverter.convert(comment.getCreatedAt()))
            .editedAt(DateTimeConverter.convert(comment.getEditedAt()))
            .updatedAt(DateTimeConverter.convert(comment.getUpdatedAt()));
    }
}
