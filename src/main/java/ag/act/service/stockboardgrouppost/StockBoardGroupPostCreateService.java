package ag.act.service.stockboardgrouppost;

import ag.act.converter.ContentUserProfileConverter;
import ag.act.converter.post.CreatePostRequestConverter;
import ag.act.converter.post.PostUpdateResponseConverter;
import ag.act.dto.PostDetailsParamDto;
import ag.act.dto.post.CreatePostRequestDto;
import ag.act.dto.user.SimpleUserProfileDto;
import ag.act.entity.ActUser;
import ag.act.entity.Board;
import ag.act.entity.FileContent;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Push;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.BoardCategory;
import ag.act.enums.ClientType;
import ag.act.facade.user.UserFacade;
import ag.act.model.CreatePostRequest;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PushRequest;
import ag.act.service.notification.NotificationService;
import ag.act.service.post.PostImageService;
import ag.act.service.post.PostService;
import ag.act.service.post.PostUserProfileService;
import ag.act.service.push.PushService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import ag.act.service.user.UserRoleService;
import ag.act.util.DateTimeUtil;
import ag.act.validator.post.PostCategoryValidator;
import ag.act.validator.post.StockBoardGroupPostCreateValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockBoardGroupPostCreateService {

    private static final String SOLIDARITY_LEADER_PUSH_TITLE = "주주대표 새글 알림";
    private static final String SOLIDARITY_LEADER_PUSH_CONTENT = "주주대표가 새로운 글을 작성하였습니다.";
    private static final List<BoardCategory> PUSH_APPLICABLE_CATEGORIES_BY_SOLIDARITY_LEADER =
        List.of(BoardCategory.SOLIDARITY_LEADER_LETTERS, BoardCategory.DEBATE, BoardCategory.DAILY_ACT);

    private final UserFacade userFacade;
    private final PostService postService;
    private final PostImageService postImageService;
    private final PostUserProfileService postUserProfileService;
    private final StockBoardGroupPostCreateValidator stockBoardGroupPostCreateValidator;
    private final PostUpdateResponseConverter postUpdateResponseConverter;
    private final ContentUserProfileConverter contentUserProfileConverter;
    private final NotificationService notificationService;
    private final SolidarityLeaderService solidarityLeaderService;
    private final UserRoleService userRoleService;
    private final PushService pushService;
    private final PostCategoryValidator postCategoryValidator;
    private final CreatePostRequestConverter createPostRequestConverter;

    public PostDetailsDataResponse createBoardGroupPost(CreatePostRequestDto createPostRequestDto) {
        final PostDetailsParamDto boardGroupPost = createBoardGroupPostAndGetPostDetailsParamDto(createPostRequestDto);
        return new PostDetailsDataResponse()
            .data(postUpdateResponseConverter.convert(boardGroupPost));
    }

    public PostDetailsParamDto createBoardGroupPostAndGetPostDetailsParamDto(CreatePostRequestDto createPostRequestDto) {
        final String stockCode = createPostRequestDto.getStockCode();
        final CreatePostRequest createPostRequest = createPostRequestDto.getCreatePostRequest();
        postCategoryValidator.validateForCreate(createPostRequest.getBoardCategory());
        final Board board = stockBoardGroupPostCreateValidator.validateForCreatePostAndGet(createPostRequestDto);
        final ActUser user = createPostRequestDto.getActUser();
        final ClientType clientType = createPostRequestDto.getClientType();
        final Post savedPost = postService.savePost(makePost(createPostRequest, user, board, clientType));
        final PostUserProfile postUserProfile = savePostUserProfile(stockCode, user, savedPost);
        final List<FileContent> contentImages = savePostImages(savedPost.getId(), createPostRequest.getImageIds());

        createDigitalDocumentIfApplicable(createPostRequest, savedPost);

        notificationService.createNotification(savedPost);

        Push push = createPush(createPostRequest, savedPost.getId(), user, board)
            .map(it -> updatePushIdToPost(savedPost, it))
            .orElse(null);


        return PostDetailsParamDto.builder(savedPost, board, contentImages, postUserProfile)
            .liked(Boolean.FALSE)
            .push(push)
            .build();
    }

    private Push updatePushIdToPost(Post post, Push push) {
        post.setPushId(push.getId());

        return push;
    }

    private Optional<Push> createPush(CreatePostRequest createPostRequest, Long postId, ActUser user, Board board) {
        PushRequest createPushRequest = createPostRequest.getPush();

        boolean isPushCreatableBySolidarityLeader = solidarityLeaderService.isLeader(user.getId(), board.getStockCode())
            && PUSH_APPLICABLE_CATEGORIES_BY_SOLIDARITY_LEADER.contains(board.getCategory())
            && createPostRequest.getIsAnonymous() == Boolean.FALSE
            && createPushRequest == null;

        if (isPushCreatableBySolidarityLeader) {
            final PushRequest pushRequest = new PushRequest()
                .title(SOLIDARITY_LEADER_PUSH_TITLE)
                .content(SOLIDARITY_LEADER_PUSH_CONTENT);

            return pushService.createPushForStock(
                pushRequest,
                postId,
                DateTimeUtil.getTodayInstant(),
                board.getStockCode());
        }

        return pushService.createPushForGlobalEvent(
            createPostRequest.getPush(),
            postId,
            createPostRequest.getActiveStartDate()
        );
    }

    private void createDigitalDocumentIfApplicable(CreatePostRequest createPostRequest, Post savedPost) {
        if (createPostRequest.getDigitalDocument() != null) {
            final DigitalDocument digitalDocument = postService.createDigitalDocument(savedPost, createPostRequest);
            savedPost.setDigitalDocument(digitalDocument);
        }
    }

    private PostUserProfile savePostUserProfile(String stockCode, ActUser user, Post post) {
        final Long userId = user.getId();
        final Boolean isSolidarityLeader = getIsSolidarityLeader(userId);

        PostUserProfile postUserProfile = getPostUserProfile(stockCode, userId, post.getIsAnonymous(), isSolidarityLeader);
        postUserProfile.setPostId(post.getId());
        postUserProfileService.save(postUserProfile);
        return postUserProfile;
    }

    private Boolean getIsSolidarityLeader(Long userId) {
        return userRoleService.isAdmin(userId)
            ? Boolean.FALSE
            : solidarityLeaderService.isLeader(userId);
    }

    private List<FileContent> savePostImages(Long postId, List<Long> imageIds) {
        final List<PostImage> postImages = postImageService.savePostImageList(postId, imageIds);

        return postImageService.getFileContentByImageIds(postImages.stream().map(PostImage::getImageId).toList());
    }

    private PostUserProfile getPostUserProfile(String stockCode, Long userId, Boolean isAnonymous, Boolean isSolidarityLeader) {
        final SimpleUserProfileDto simpleUserProfileDto = userFacade.getSimpleUserProfileDto(userId, stockCode);

        return contentUserProfileConverter.convertPostUserProfile(userId, simpleUserProfileDto, isAnonymous, isSolidarityLeader);
    }

    private Post makePost(CreatePostRequest createPostRequest, ActUser user, Board board, ClientType clientType) {
        return createPostRequestConverter.convert(createPostRequest, user, board, clientType);
    }
}
