package ag.act.service.stockboardgrouppost.comment;

import ag.act.entity.Comment;
import ag.act.entity.Post;
import ag.act.enums.CommentType;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.repository.CommentRepository;
import ag.act.service.blockeduser.BlockedUserEnhancer;
import ag.act.util.StatusUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService implements BlockedUserEnhancer {
    private final CommentRepository commentRepository;

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public Integer findMaxAnonymousCount(Long postId) {
        return commentRepository.findFirstByPostIdAndIsAnonymousTrueOrderByAnonymousCountDesc(postId)
            .orElse(new Comment()).getAnonymousCount();
    }

    public Optional<Comment> findFirstByPostIdAndUserIdAndIsAnonymous(Long postId, Long userId, Boolean isAnonymous) {
        return commentRepository.findFirstByPostIdAndUserIdAndIsAnonymous(postId, userId, isAnonymous);
    }

    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    public Comment getComment(Long commentId, String message) {
        return findById(commentId)
            .orElseThrow(() -> new BadRequestException(message));
    }

    public Page<Comment> getPostComments(Long postId, Long parentCommentId, List<Long> blockedUserIdList, PageRequest pageRequest) {
        if (parentCommentId <= 0) {
            return getCommentList(postId, blockedUserIdList, pageRequest);
        } else {
            return getReplyCommentList(postId, parentCommentId, blockedUserIdList, pageRequest);
        }
    }

    public Long countByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    public Long countByParentId(Long postId, Long parentCommentId) {
        return commentRepository.countByPostIdAndParentIdAndStatusNotIn(
            postId, parentCommentId, StatusUtil.getDeletedStatusesForComment()
        );
    }

    private Page<Comment> getCommentList(Long postId, List<Long> blockedUserIdList, PageRequest pageRequest) {
        return commentRepository.findAllByPostIdAndParentIdIsNullAndStatusNotInAndUserIdNotIn(
            postId, StatusUtil.getDeletedStatusesForComment(), refinedBlockedUserIdList(blockedUserIdList), pageRequest
        );
    }

    private Page<Comment> getReplyCommentList(Long postId, Long parentCommentId, List<Long> blockedUserIdList, PageRequest pageRequest) {
        return commentRepository.findAllByPostIdAndParentIdAndStatusNotInAndUserIdNotIn(
            postId, parentCommentId, StatusUtil.getDeletedStatusesForComment(), refinedBlockedUserIdList(blockedUserIdList), pageRequest
        );
    }

    public List<Comment> getRepliesByParentIds(List<Long> parentCommentIds, List<Long> blockedUserIdList, int size) {
        List<String> invalidStatuses = StatusUtil.getDeletedStatusesForComment()
            .stream()
            .map(Enum::name)
            .toList();

        return commentRepository.findAllByParentIdInAndIdNotInAndStatusNotIn(
            parentCommentIds, invalidStatuses, refinedBlockedUserIdList(blockedUserIdList), size
        );
    }

    public Comment makeComment(Post post, Long userId, Long parentCommentId, String content, Boolean isAnonymous) {
        Comment comment = new Comment();
        comment.setStatus(ag.act.model.Status.ACTIVE);
        comment.setContent(content);
        comment.setPostId(post.getId());
        comment.setUserId(userId);
        comment.setReplyCommentCount(0L);
        comment.setLikeCount(0L);

        comment.setType(CommentType.POST);
        if (parentCommentId > 0) {
            comment.setType(CommentType.REPLY_COMMENT);
            comment.setParentId(parentCommentId);
        }

        comment.setIsAnonymous(isAnonymous);
        if (isAnonymous && !Objects.equals(post.getUserId(), userId)) {
            comment.setAnonymousCount(getAnonymousCount(post.getId(), userId));
        }

        return comment;
    }

    private int getAnonymousCount(Long postId, Long userId) {
        return findFirstByPostIdAndUserIdAndIsAnonymous(postId, userId, Boolean.TRUE)
            .map(Comment::getAnonymousCount)
            .orElseGet(() -> findMaxAnonymousCount(postId) + 1);
    }

    public List<Comment> findAllByParentId(Long parentId) {
        return commentRepository.findAllByParentId(parentId);
    }

    public void updateReportStatus(Long commentId, ag.act.model.ReportStatus reportStatus) {
        Comment comment = findById(commentId)
            .orElseThrow(() -> new NotFoundException("신고된 댓글 정보가 없습니다."));
        comment.setStatus(
            reportStatus == ag.act.model.ReportStatus.COMPLETE
                ? ag.act.model.Status.DELETED_BY_ADMIN
                : ag.act.model.Status.ACTIVE
        );
        save(comment);
    }

    public Long countByPostIdWithoutAuthor(Long postId, Long userId) {
        return commentRepository.countByPostIdAndUserIdNot(postId, userId);
    }

    public Long countByPostIdAndParentIdWithoutAuthor(Long postId, Long parentCommentId, Long userId) {
        return commentRepository.countByPostIdAndParentIdAndUserIdNot(postId, parentCommentId, userId);
    }

    public Optional<Comment> findLatestCommentFrom(Long userId, Long postId, final LocalDateTime startTime) {
        return commentRepository.findFirstByUserIdAndPostIdAndCreatedAtAfterOrderByCreatedAtDesc(userId, postId, startTime);
    }
}
