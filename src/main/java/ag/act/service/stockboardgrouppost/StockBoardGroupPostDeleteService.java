package ag.act.service.stockboardgrouppost;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.post.DeletePostRequestDto;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.digitaldocument.modusign.DigitalProxyModuSignService;
import ag.act.service.notification.NotificationService;
import ag.act.service.poll.PollService;
import ag.act.service.post.PostImageService;
import ag.act.service.post.PostService;
import ag.act.service.user.UserRoleService;
import ag.act.util.SimpleStringResponseUtil;
import ag.act.util.StatusUtil;
import ag.act.validator.post.PostCategoryValidator;
import ag.act.validator.post.StockBoardGroupPostValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class StockBoardGroupPostDeleteService {
    private final PollService pollService;
    private final PostService postService;
    private final PostImageService postImageService;
    private final StockBoardGroupPostValidator stockBoardGroupPostValidator;
    private final DigitalDocumentService digitalDocumentService;
    private final DigitalProxyModuSignService digitalProxyModuSignService;
    private final NotificationService notificationService;
    private final UserRoleService userRoleService;
    private final PostCategoryValidator postCategoryValidator;

    public SimpleStringResponse deleteBoardGroupPost(DeletePostRequestDto deletePostRequestDto) {
        final Post post = stockBoardGroupPostValidator.validateBoardGroupPost(deletePostRequestDto, StatusUtil.getDeleteStatuses());
        postCategoryValidator.validateForDelete(post.getBoard().getCategory());
        final User currentUser = ActUserProvider.getNoneNull();
        if (!currentUser.isAdmin()) {
            stockBoardGroupPostValidator.validateAuthor(currentUser, post.getUserId(), "게시글");
        }
        final Status deleteStatus = getDeleteStatus(currentUser, post.getUserId());

        final LocalDateTime deleteTime = LocalDateTime.now();
        deletePostImages(post.getId(), deleteTime);
        digitalDocumentService.deleteDigitalDocument(post.getDigitalDocument(), deleteStatus, deleteTime);
        digitalProxyModuSignService.deleteDigitalProxyModuSign(post.getDigitalProxy(), deleteStatus, deleteTime);
        pollService.deletePolls(post.getPolls(), deleteStatus, deleteTime);
        postService.deletePost(post, deleteStatus, deleteTime);
        notificationService.updateNotificationStatusByPostIdIfExist(post.getId(), deleteStatus);

        return SimpleStringResponseUtil.ok();
    }

    private Status getDeleteStatus(User currentUser, Long authorId) {
        if (!currentUser.isAdmin()) {
            return Status.DELETED_BY_USER;
        }

        if (isAdminAuthorOfPost(authorId)) {
            return Status.DELETED;
        }
        return Status.DELETED_BY_ADMIN;
    }

    private boolean isAdminAuthorOfPost(Long authorId) {
        return userRoleService.isAdmin(authorId);
    }

    private void deletePostImages(Long postId, LocalDateTime now) {
        postImageService.deleteAll(postId, now);
    }
}
