package ag.act.converter.post.comment;

import ag.act.dto.post.comment.GetCommentRequestDto;
import ag.act.entity.Comment;
import ag.act.model.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class CommentPagedResponseConverter {

    private final CommentResponseConverter commentResponseConverter;

    public Page<CommentResponse> convert(Page<Comment> comments, GetCommentRequestDto getCommentRequestDto) {
        Map<Long, List<Comment>> parentIdReplyCommentListMap = getCommentRequestDto.replyComments()
            .stream()
            .collect(Collectors.groupingBy(Comment::getParentId, Collectors.toList()));

        return comments.map(comment -> {
            boolean isLikedByMe = getCommentRequestDto.isLikedComment(comment.getId());
            List<CommentResponse> replyCommentResponses = getReplyCommentResponses(
                getCommentRequestDto,
                parentIdReplyCommentListMap.getOrDefault(comment.getId(), new ArrayList<>())
            );

            return commentResponseConverter.convert(
                comment,
                getCommentRequestDto.isPostAuthor(comment),
                isLikedByMe,
                comment.getCommentUserProfile(),
                comment.getAnonymousCount(),
                replyCommentResponses
            );
        });
    }

    private List<CommentResponse> getReplyCommentResponses(
        GetCommentRequestDto getCommentRequestDto,
        List<Comment> replyComments
    ) {
        return  replyComments
            .stream()
            .map(replyComment -> {
                boolean isReplyCommentLikedByMe = getCommentRequestDto.isLikedComment(replyComment.getId());
                return commentResponseConverter.convert(
                    replyComment,
                    getCommentRequestDto.isPostAuthor(replyComment),
                    isReplyCommentLikedByMe,
                    replyComment.getCommentUserProfile(),
                    replyComment.getAnonymousCount()
                );
            })
            .toList();
    }
}
