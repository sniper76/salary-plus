package ag.act.dto.post.comment;

import ag.act.entity.Comment;
import ag.act.entity.Post;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record GetCommentRequestDto(
    Post post,
    Set<Long> userLikeCommentIdList,
    List<Comment> replyComments
) {

    public static GetCommentRequestDto of(
        Post post,
        Set<Long> userLikeCommentIdList,
        List<Comment> replyComments
    ) {
        return new GetCommentRequestDto(post, userLikeCommentIdList, replyComments);
    }

    public boolean isPostAuthor(Comment comment) {
        return Objects.equals(comment.getUserId(), post.getUserId());
    }

    public Long getPostId() {
        return post.getId();
    }

    public boolean isLikedComment(Long commentId) {
        return userLikeCommentIdList.contains(commentId);
    }
}
