package ag.act.core.aop.htmlcontent;

import ag.act.core.annotation.HtmlContentTarget;
import ag.act.dto.CampaignHtmlContent;
import ag.act.dto.HtmlContent;
import ag.act.util.AspectParameterUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@RequiredArgsConstructor
@Component
public class HtmlContentEscapingAspect {
    private final HtmlContentEscaperFactory htmlContentEscaperFactory;

    @Before("""
           execution(* ag.act.handler.stockboardgrouppost.StockBoardGroupPostApiDelegateImpl.createBoardGroupPost(..))
        || execution(* ag.act.handler.stockboardgrouppost.StockBoardGroupPostApiDelegateImpl.updateBoardGroupPost(..))
        || execution(* ag.act.handler.stockboardgrouppost.StockBoardGroupPostCommentApiDelegateImpl.createBoardGroupPostComment(..))
        || execution(* ag.act.handler.stockboardgrouppost.StockBoardGroupPostCommentApiDelegateImpl.updateBoardGroupPostComment(..))
        || execution(* ag.act.handler.stockboardgrouppost.StockBoardGroupPostCommentReplyApiDelegateImpl.createBoardGroupPostCommentReply(..))
        || execution(* ag.act.handler.admin.post.AdminBoardGroupPostApiDelegateImpl.createPost(..))
        || execution(* ag.act.handler.admin.post.AdminBoardGroupPostApiDelegateImpl.updatePost(..))
        || execution(* ag.act.handler.admin.comment.AdminBoardGroupPostCommentApiDelegateImpl.createPostComment(..))
        || execution(* ag.act.handler.admin.comment.AdminBoardGroupPostCommentApiDelegateImpl.updatePostComment(..))
        """)
    public void escapeHtmlContent(JoinPoint joinPoint) {
        final HtmlContent htmlContent = findHtmlContent(joinPoint, HtmlContent.class);

        escapeContent(htmlContent);
    }

    @Before("execution(* ag.act.handler.admin.AdminCampaignApiDelegateImpl.createCampaign(..))")
    public void createCampaignRequest(JoinPoint joinPoint) {
        final CampaignHtmlContent campaignHtmlContent = findHtmlContent(joinPoint, CampaignHtmlContent.class);

        escapeContent(campaignHtmlContent.getCreatePostRequest());
    }

    @Before("execution(* ag.act.handler.admin.AdminCampaignApiDelegateImpl.updateCampaign(..))")
    public void updateCampaignRequest(JoinPoint joinPoint) {
        final CampaignHtmlContent campaignHtmlContent = findHtmlContent(joinPoint, CampaignHtmlContent.class);

        escapeContent(campaignHtmlContent.getUpdatePostRequest());
    }

    private void escapeContent(HtmlContent htmlContent) {
        final String escapedContent = htmlContentEscaperFactory.getHtmlContentEscaper(htmlContent).escapeContent();

        htmlContent.setContent(escapedContent);
    }

    private <T> T findHtmlContent(final JoinPoint joinPoint, Class<T> targetClass) {
        return AspectParameterUtil.findParameter(joinPoint, HtmlContentTarget.class, targetClass)
            .orElseThrow(() -> new IllegalArgumentException(
                    "No parameter found with annotation: %s and targetClass: %s".formatted(
                        HtmlContentTarget.class.getSimpleName(),
                        targetClass.getSimpleName()
                    )
                )
            );
    }
}
