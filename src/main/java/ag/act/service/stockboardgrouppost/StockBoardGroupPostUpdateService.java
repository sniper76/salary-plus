package ag.act.service.stockboardgrouppost;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.DateTimeConverter;
import ag.act.converter.post.PostUpdateResponseConverter;
import ag.act.dto.post.UpdatePostRequestDto;
import ag.act.entity.DigitalProxy;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.exception.BadRequestException;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PushRequest;
import ag.act.model.Status;
import ag.act.model.UpdatePollRequest;
import ag.act.model.UpdatePostRequest;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.image.ThumbnailImageService;
import ag.act.service.notification.NotificationService;
import ag.act.service.post.PostImageService;
import ag.act.service.post.PostIsActiveDecisionMaker;
import ag.act.service.post.PostService;
import ag.act.service.post.PostUserLikeService;
import ag.act.service.push.PushService;
import ag.act.service.stockboardgrouppost.postpush.PostPushUpdaterInput;
import ag.act.service.stockboardgrouppost.postpush.PostPushUpdaterResolver;
import ag.act.validator.post.PostCategoryValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockBoardGroupPostUpdateService {

    private static final Boolean INACTIVE = Boolean.FALSE;

    private final PostService postService;
    private final PostIsActiveDecisionMaker postIsActiveDecisionMaker;
    private final PostImageService postImageService;
    private final PostUpdateResponseConverter postUpdateResponseConverter;
    private final PostUpdatePreProcessor postUpdatePreProcessor;
    private final PostUserLikeService postUserLikeService;
    private final DigitalDocumentService digitalDocumentService;
    private final NotificationService notificationService;
    private final ThumbnailImageService thumbnailImageService;
    private final PushService pushService;
    private final PostPushUpdaterResolver postPushUpdaterResolver;
    private final PostCategoryValidator postCategoryValidator;

    public PostDetailsDataResponse updateBoardGroupPost(UpdatePostRequestDto updatePostRequestDto) {
        final UpdatePostRequest updatePostRequest = updatePostRequestDto.getUpdatePostRequest();
        final Post post = postUpdatePreProcessor.proceed(updatePostRequestDto);
        postCategoryValidator.validateForUpdate(post.getBoard().getCategory());
        final LocalDateTime updateTime = LocalDateTime.now();
        final List<Long> imageIds = updatePostRequest.getImageIds();

        updatePostImages(post.getId(), imageIds, updateTime);
        updatePostNotification(post, updatePostRequest);

        final Post updatedPost = updatePost(post, updatePostRequest, updateTime, imageIds);

        return toPostDetailsDataResponse(updatedPost, imageIds);
    }

    private PostDetailsDataResponse toPostDetailsDataResponse(Post post, List<Long> imageIds) {
        final Optional<Push> push = findPush(post); // 최종적으로 변경된 push 를 다시 조회한다.

        return new PostDetailsDataResponse()
            .data(
                postUpdateResponseConverter.convert(
                    post,
                    push.orElse(null),
                    post.getBoard(),
                    postUserLikeService.isUserLikedPost(post.getId()),
                    postImageService.getFileContentByImageIds(imageIds),
                    post.getPostUserProfile()
                )
            );
    }

    private Optional<Push> findPush(Post post) {
        return pushService.findPush(post.getPushId());
    }

    private void updatePostNotification(Post post, UpdatePostRequest updatePostRequest) {
        if (!updatePostRequest.getIsActive()) {
            updatePostRequest.isNotification(INACTIVE);
        }

        notificationService.updateNotification(post, updatePostRequest.getIsNotification());
    }

    private Post updatePost(
        Post post, UpdatePostRequest updatePostRequest, LocalDateTime updateTime, List<Long> imageIds
    ) {
        final Status postStatus = post.getStatus();
        final String content = updatePostRequest.getContent();
        post.setTitle(updatePostRequest.getTitle());
        post.setContent(content);
        post.setEditedAt(updateTime);
        post.setStatus(postIsActiveDecisionMaker.getIsActiveStatus(ActUserProvider.getNoneNull(), updatePostRequest.getIsActive()));
        post.setIsNotification(updatePostRequest.getIsNotification());
        post.setIsExclusiveToHolders(updatePostRequest.getIsExclusiveToHolders());
        post.setThumbnailImageUrl(thumbnailImageService.generate(imageIds, content, post.getThumbnailImageUrl()));

        updatePush(post, postStatus, updatePostRequest);
        updatePolls(post, updatePostRequest);
        updateModuSignDigitalProxy(post, updatePostRequest);
        updateDigitalDocument(post, updatePostRequest);

        return postService.savePost(post);
    }

    private void updatePush(Post post, Status postOriginalStatus, UpdatePostRequest updatePostRequest) {
        final Boolean isActiveForUpdate = updatePostRequest.getIsActive();
        final Optional<Push> optionalPush = findPush(post);
        final PushRequest requestPush = updatePostRequest.getPush();

        final boolean isActiveStartDateInFuture = postUpdatePreProcessor.isActiveStartDateInFuture(post, LocalDateTime.now());
        postPushUpdaterResolver.update(new PostPushUpdaterInput(
            postOriginalStatus,
            isActiveForUpdate,
            post,
            requestPush,
            optionalPush.orElse(null),
            isActiveStartDateInFuture,
            updatePostRequest.getActiveStartDate()
        ));
    }

    private void updatePolls(Post post, UpdatePostRequest updatePostRequest) {
        final List<Poll> polls = post.getPolls();
        final List<UpdatePollRequest> updatePollRequestList = updatePostRequest.getPolls();
        if (CollectionUtils.isEmpty(updatePollRequestList)) {
            return;
        }
        for (Poll poll : polls) {
            final Optional<UpdatePollRequest> optionalUpdatePollRequest = updatePollRequestList.stream()
                .filter(it -> Objects.equals(it.getId(), poll.getId()))
                .findFirst();
            if (optionalUpdatePollRequest.isEmpty()) {
                throw new BadRequestException("수정하려는 설문 아이디가 존재하지 않습니다. id:%d".formatted(poll.getId()));
            }
            final UpdatePollRequest updatePollRequest = optionalUpdatePollRequest.get();
            if (updatePollRequest.getTargetStartDate() != null) {
                poll.setTargetStartDate(
                    DateTimeConverter.convert(updatePollRequest.getTargetStartDate())
                );
            }
            poll.setTargetEndDate(
                DateTimeConverter.convert(updatePollRequest.getTargetEndDate())
            );
        }
    }

    private void updateModuSignDigitalProxy(Post post, UpdatePostRequest updatePostRequest) {
        final DigitalProxy moduSignDigitalProxy = post.getDigitalProxy();
        if (moduSignDigitalProxy == null || updatePostRequest.getUpdateTargetDate() == null) {
            return;
        }

        if (updatePostRequest.getUpdateTargetDate().getTargetStartDate() != null) {
            moduSignDigitalProxy.setTargetStartDate(
                DateTimeConverter.convert(updatePostRequest.getUpdateTargetDate().getTargetStartDate())
            );
        }
        moduSignDigitalProxy.setTargetEndDate(
            DateTimeConverter.convert(updatePostRequest.getUpdateTargetDate().getTargetEndDate())
        );
    }

    private void updateDigitalDocument(Post post, UpdatePostRequest updatePostRequest) {
        if (post.getDigitalDocument() == null || updatePostRequest.getDigitalDocument() == null) {
            return;
        }

        post.setDigitalDocument(
            digitalDocumentService.updateDigitalDocument(post.getDigitalDocument(), updatePostRequest.getDigitalDocument())
        );
    }

    private void updatePostImages(Long postId, List<Long> imageIds, LocalDateTime deleteTime) {
        deletePostImages(postId, deleteTime);

        if (CollectionUtils.isEmpty(imageIds)) {
            return;
        }

        postImageService.savePostImageList(postId, imageIds);
    }

    private void deletePostImages(Long postId, LocalDateTime deleteTime) {
        postImageService.deleteAll(postId, deleteTime);
    }

}
