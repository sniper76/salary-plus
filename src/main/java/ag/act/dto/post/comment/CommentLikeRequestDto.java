package ag.act.dto.post.comment;

public record CommentLikeRequestDto(
    String stockCode,
    String boardGroupName,
    Long postId,
    Long commentId
) {
    public static CommentLikeRequestDto of(
        final String stockCode,
        final String boardGroupName,
        final Long postId,
        final Long commentId
    ) {
        return new CommentLikeRequestDto(
            stockCode,
            boardGroupName,
            postId,
            commentId
        );
    }
}
