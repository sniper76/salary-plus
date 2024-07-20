package ag.act.entity;

import ag.act.enums.CommentType;

public interface CommentExtension {

    Long getReplyCommentCount();

    CommentType getType();

    default boolean hasReplyComment() {
        return getReplyCommentCount() > 0;
    }

    default boolean isReplyComment() {
        return getType() == CommentType.REPLY_COMMENT;
    }
}
