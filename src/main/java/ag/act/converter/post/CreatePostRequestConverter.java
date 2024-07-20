package ag.act.converter.post;

import ag.act.constants.MessageConstants;
import ag.act.converter.DateTimeConverter;
import ag.act.converter.post.poll.CreatePollRequestConverter;
import ag.act.entity.ActUser;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.enums.ClientType;
import ag.act.model.CreatePostRequest;
import ag.act.service.image.ThumbnailImageService;
import ag.act.service.post.PostIsActiveDecisionMaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CreatePostRequestConverter implements MessageConstants  {
    private final PostIsActiveDecisionMaker postIsActiveDecisionMaker;
    private final ThumbnailImageService thumbnailImageService;
    private final CreatePollRequestConverter createPollRequestConverter;
    private final DigitalProxyCreateRequestConverter digitalProxyCreateRequestConverter;

    public Post convert(CreatePostRequest createPostRequest, ActUser user, Board board, ClientType clientType) {
        final String content = createPostRequest.getContent();
        final List<Long> imageIds = createPostRequest.getImageIds();

        Post post = new Post();
        post.setStatus(postIsActiveDecisionMaker.getIsActiveStatus(user, createPostRequest.getIsActive()));
        post.setBoardId(board.getId());
        post.setBoard(board);
        post.setContent(content);
        post.setTitle(createPostRequest.getTitle());
        post.setIsAnonymous(createPostRequest.getIsAnonymous());
        post.setIsNotification(createPostRequest.getIsNotification());
        post.setIsPinned(Boolean.FALSE); // TODO: 추후에 이 값이 request 에서 올까?
        post.setIsExclusiveToHolders(createPostRequest.getIsExclusiveToHolders());
        post.setUserId(user.getId());
        post.setThumbnailImageUrl(thumbnailImageService.generate(imageIds, content));
        post.setClientType(clientType);
        post.setActiveStartDate(DateTimeConverter.convert(createPostRequest.getActiveStartDate()));
        post.setActiveEndDate(DateTimeConverter.convert(createPostRequest.getActiveEndDate()));

        if (post.getIsAnonymous()) {
            post.setAnonymousName(ANONYMOUS_NAME);
        }

        if (createPostRequest.getPolls() != null) {
            post.setPolls(createPollRequestConverter.convert(createPostRequest.getPolls(), post));
        }

        if (createPostRequest.getDigitalProxy() != null) {
            post.setDigitalProxy(digitalProxyCreateRequestConverter.convert(createPostRequest, post));
        }

        return post;
    }
}
