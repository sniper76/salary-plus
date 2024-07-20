package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.ContentUserProfileConverter;
import ag.act.converter.post.CreatePostRequestConverter;
import ag.act.converter.post.PostUpdateResponseConverter;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.dto.PostDetailsParamDto;
import ag.act.dto.post.CreatePostRequestDto;
import ag.act.dto.user.SimpleUserProfileDto;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Push;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.ClientType;
import ag.act.exception.BadRequestException;
import ag.act.facade.user.UserFacade;
import ag.act.model.CreatePostRequest;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.PushRequest;
import ag.act.service.notification.NotificationService;
import ag.act.service.post.PostImageService;
import ag.act.service.post.PostService;
import ag.act.service.post.PostUserProfileService;
import ag.act.service.push.PushService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import ag.act.service.stockboardgrouppost.PostPushTargetDateTimeManager;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostCreateService;
import ag.act.service.user.UserRoleService;
import ag.act.validator.post.PostCategoryValidator;
import ag.act.validator.post.StockBoardGroupPostCreateValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someBoardGroupForStock;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockBoardGroupPostCreateServiceTest {
    @InjectMocks
    private StockBoardGroupPostCreateService service;
    @Mock
    private UserFacade userFacade;
    @Mock
    private PostService postService;
    @Mock
    private PostImageService postImageService;
    @Mock
    private PostUserProfileService postUserProfileService;
    @Mock
    private StockBoardGroupPostCreateValidator stockBoardGroupPostCreateValidator;
    @Mock
    private PostUpdateResponseConverter postUpdateResponseConverter;
    @Mock
    private ContentUserProfileConverter contentUserProfileConverter;
    @Mock
    private SolidarityLeaderService solidarityLeaderService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private PostCategoryValidator postCategoryValidator;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private PushService pushService;
    @Mock
    private PostPushTargetDateTimeManager postPushTargetDateTimeManager;
    @Mock
    private CreatePostRequestConverter createPostRequestConverter;

    private List<MockedStatic<?>> statics;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenCreateBoardGroupPost {

        @Mock
        private SimpleUserProfileDto simpleUserProfileDto;
        @Mock
        private CreatePostRequest createPostRequest;
        @Mock
        private User user;
        @Mock
        private Board board;
        @Mock
        private BoardCategory boardCategory;
        @Mock
        private PostUserProfile postUserProfile;
        @Mock
        private Post post;
        @Mock
        private Post savedPost;
        @Mock
        private Push savedPush;
        @Mock
        private CreatePostRequestDto createPostRequestDto;
        @Mock
        private GetBoardGroupPostDto getBoardGroupPostDto;
        @Mock
        private PostDetailsResponse postCreateUpdateResponse;
        @Mock
        private PushRequest pushRequest;
        private Long newPushId;
        private ClientType clientType;

        @BeforeEach
        void setUp() {
            final String stockCode = someString(0);
            final BoardGroup boardGroup = someBoardGroupForStock();
            final BoardCategory boardCategory = someThing(BoardCategory.activeBoardCategoriesForStocks());
            final Long userId = someLong();
            final Boolean isAnonymous = Boolean.FALSE;
            final Boolean isAdmin = Boolean.FALSE;
            final Boolean isSolidarityLeader = Boolean.TRUE;
            final Long postId = someLong();
            final Instant activeStartDate = Instant.now();
            newPushId = someLong();
            clientType = someEnum(ClientType.class);

            given(createPostRequestDto.getCreatePostRequest()).willReturn(createPostRequest);
            given(createPostRequestDto.getStockCode()).willReturn(stockCode);
            given(createPostRequestDto.getBoardGroupName()).willReturn(boardGroup.name());
            given(createPostRequestDto.getBoardGroupPostDto()).willReturn(getBoardGroupPostDto);
            given(createPostRequestDto.getActUser()).willReturn(null);
            given(createPostRequest.getPolls()).willReturn(null);
            given(createPostRequest.getDigitalProxy()).willReturn(null);
            given(createPostRequest.getBoardCategory()).willReturn(boardCategory.name());
            given(createPostRequest.getIsAnonymous()).willReturn(isAnonymous);
            given(createPostRequest.getImageIds()).willReturn(List.of());

            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);
            given(board.getCategory()).willReturn(boardCategory);

            given(stockBoardGroupPostCreateValidator.validateForCreatePostAndGet(createPostRequestDto)).willReturn(board);

            given(userRoleService.isAdmin(userId)).willReturn(isAdmin);
            given(solidarityLeaderService.isLeader(userId)).willReturn(isSolidarityLeader);
            given(userFacade.getSimpleUserProfileDto(userId, stockCode)).willReturn(simpleUserProfileDto);
            given(contentUserProfileConverter.convertPostUserProfile(userId, simpleUserProfileDto, isAnonymous, isSolidarityLeader))
                .willReturn(postUserProfile);
            given(createPostRequestConverter.convert(createPostRequest, user, board, clientType)).willReturn(post);
            given(postService.savePost(post)).willReturn(savedPost);
            given(savedPost.getId()).willReturn(postId);
            given(postUserProfileService.save(postUserProfile)).willReturn(postUserProfile);
            given(postImageService.savePostImageList(postId, List.of())).willReturn(List.of());
            given(postImageService.getFileContentByImageIds(List.of())).willReturn(List.of());
            willDoNothing().given(notificationService).createNotification(post);
            given(solidarityLeaderService.isLeader(userId, stockCode)).willReturn(true);
            given(createPostRequestDto.getCreatePostRequest()).willReturn(createPostRequest);
            given(createPostRequestDto.getActUser()).willReturn(user);
            given(createPostRequestDto.getClientType()).willReturn(clientType);

            given(createPostRequest.getActiveStartDate()).willReturn(activeStartDate);
            given(createPostRequest.getPush()).willReturn(pushRequest);
            given(postPushTargetDateTimeManager.generatePushTargetDateTime(activeStartDate)).willReturn(activeStartDate);
            given(pushService.createPushForGlobalEvent(pushRequest, postId, activeStartDate)).willReturn(Optional.of(savedPush));
            given(savedPush.getId()).willReturn(newPushId);
            given(postUpdateResponseConverter.convert(any(PostDetailsParamDto.class))).willReturn(postCreateUpdateResponse);
            given(createPostRequest.getBoardCategory()).willReturn(boardCategory.name());
            willDoNothing().given(postCategoryValidator).validateForUpdate(boardCategory);
        }

        @Nested
        class WhenSuccess {

            private PostDetailsDataResponse actualPostCreateResponse;

            @BeforeEach
            void setUp() {
                actualPostCreateResponse = service.createBoardGroupPost(createPostRequestDto);
            }

            @Test
            void shouldCreatePostWithoutPollAndDigitalProxy() {
                assertThat(actualPostCreateResponse.getData(), is(postCreateUpdateResponse));
            }

            @Test
            void shouldCallSavePostWithPushId() {
                then(savedPost).should().setPushId(newPushId);
            }
        }

        @Nested
        class WhenFailToValidateUerPermission {
            @Test
            void shouldThrowBadRequestException() {

                // Given
                willThrow(BadRequestException.class)
                    .given(stockBoardGroupPostCreateValidator).validateForCreatePostAndGet(createPostRequestDto);

                // When // Then
                assertThrows(
                    BadRequestException.class,
                    () -> service.createBoardGroupPost(createPostRequestDto)
                );
            }
        }
    }
}
