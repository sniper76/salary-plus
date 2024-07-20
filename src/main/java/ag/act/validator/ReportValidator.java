package ag.act.validator;

import ag.act.enums.ReportType;
import ag.act.exception.NotFoundException;
import ag.act.service.post.PostService;
import ag.act.service.stockboardgrouppost.comment.CommentService;
import org.springframework.stereotype.Component;

@Component
public class ReportValidator {

    private final PostService postService;
    private final CommentService commentService;

    public ReportValidator(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    public void validate(ReportType type, Long contentId) {
        if (type == ReportType.POST) {
            postService.findById(contentId).orElseThrow(() -> new NotFoundException("신고 대상 게시글이 없습니다."));
        }

        if (type == ReportType.COMMENT) {
            commentService.findById(contentId).orElseThrow(() -> new NotFoundException("신고 대상 댓글/답글이 없습니다."));
        }
    }
}
