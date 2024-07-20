package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.DateTimeConverter;
import ag.act.converter.post.PostUpdateResponseConverter;
import ag.act.dto.post.UpdatePostRequestDto;
import ag.act.entity.Board;
import ag.act.entity.DigitalProxy;
import ag.act.entity.FileContent;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Push;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.Status;
import ag.act.model.UpdatePollRequest;
import ag.act.service.image.ThumbnailImageService;
import ag.act.service.notification.NotificationService;
import ag.act.service.post.PostImageService;
import ag.act.service.post.PostIsActiveDecisionMaker;
import ag.act.service.post.PostService;
import ag.act.service.post.PostUserLikeService;
import ag.act.service.push.PushService;
import ag.act.service.stockboardgrouppost.PostUpdatePreProcessor;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostUpdateService;
import ag.act.service.stockboardgrouppost.postpush.PostPushUpdaterInput;
import ag.act.service.stockboardgrouppost.postpush.PostPushUpdaterResolver;
import ag.act.validator.post.PostCategoryValidator;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someBoardGroupForStock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;
import static shiver.me.timbers.data.random.RandomTimes.someTime;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockBoardGroupPostUpdateServiceTest {
    @InjectMocks
    private StockBoardGroupPostUpdateService service;
    private List<MockedStatic<?>> statics;
    @Mock
    private PostService postService;
    @Mock
    private PostIsActiveDecisionMaker postIsActiveDecisionMaker;
    @Mock
    private PostImageService postImageService;
    @Mock
    private PostUpdateResponseConverter postUpdateResponseConverter;
    @Mock
    private PostUpdatePreProcessor postUpdatePreProcessor;
    @Mock
    private PostUserLikeService postUserLikeService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ThumbnailImageService thumbnailImageService;
    @Mock
    private PushService pushService;
    @Mock
    private Push push;
    @Mock
    private Post post;
    private Long postId;
    @Mock
    private ag.act.model.UpdatePostRequest updatePostRequest;
    @Mock
    private UpdatePostRequestDto updatePostRequestDto;
    @Mock
    private ag.act.model.PostDetailsResponse postCreateUpdateResponse;
    private LocalDateTime updateTime;
    private Instant updateTimeInstant;
    @Mock
    private List<PostImage> postImages;
    @Mock
    private List<Long> imageIds;
    @Mock
    private Poll poll;
    @Mock
    private User user;
    @Mock
    private Board board;
    @Mock
    private PostUserProfile postUserProfile;
    @Mock
    private DigitalProxy digitalProxy;
    @Mock
    private ag.act.model.TargetDateRequest targetDateRequest;
    @Mock
    private List<FileContent> fileContents;
    @Mock
    private UpdatePollRequest updatePollRequest;
    @Mock
    private PostPushUpdaterResolver postPushUpdaterResolver;
    private Boolean isActive;
    private boolean isUserLikedPost;
    private PostDetailsDataResponse actualResponse;
    private Long pushId;
    @Mock
    private ag.act.model.PushRequest pushRequest;
    @Mock
    private PostCategoryValidator postCategoryValidator;

    @BeforeEach
    void setUp() {

        // before mock statics
        updateTime = LocalDateTime.now();
        updateTimeInstant = DateTimeConverter.convert(updateTime);

        statics = List.of(
            mockStatic(ActUserProvider.class),
            mockStatic(LocalDateTime.class),
            mockStatic(DateTimeConverter.class)
        );

        final Instant updateTargetDate = someTime().toInstant();
        final String stockCode = someString(6);
        final BoardGroup boardGroup = someBoardGroupForStock();
        final BoardCategory boardCategory = someThing(BoardCategory.activeBoardCategoriesForStocks());
        postId = someLong();
        pushId = someLong();
        isActive = someBoolean();
        isUserLikedPost = someBoolean();
        final String content = someString(100);
        final String existingThumbnailUrl = someString(100);
        final String thumbnailImageUrl = someString(100);

        given(updatePostRequest.getActiveStartDate()).willReturn(updateTimeInstant);
        given(updatePostRequestDto.getUpdatePostRequest()).willReturn(updatePostRequest);
        given(updatePostRequestDto.getPostId()).willReturn(postId);
        given(updatePostRequestDto.getStockCode()).willReturn(stockCode);
        given(updatePostRequestDto.getBoardGroupName()).willReturn(boardGroup.name());
        given(DateTimeConverter.convert(updateTargetDate)).willReturn(updateTime);
        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(postService.savePost(post)).willReturn(post);
        given(post.getStatus()).willReturn(Status.ACTIVE);
        given(post.getId()).willReturn(postId);
        given(post.getBoard()).willReturn(board);
        given(post.getPostUserProfile()).willReturn(postUserProfile);
        given(post.getContent()).willReturn(content);
        given(post.getThumbnailImageUrl()).willReturn(existingThumbnailUrl);
        given(post.getPushId()).willReturn(pushId);
        given(LocalDateTime.now()).willReturn(updateTime);
        given(updatePostRequest.getImageIds()).willReturn(imageIds);
        given(updatePostRequest.getIsActive()).willReturn(isActive);
        given(updatePostRequest.getUpdateTargetDate()).willReturn(targetDateRequest);
        given(targetDateRequest.getTargetEndDate()).willReturn(updateTargetDate);
        given(postUpdatePreProcessor.proceed(updatePostRequestDto)).willReturn(post);
        willDoNothing().given(postImageService).deleteAll(postId, updateTime);
        given(postImageService.savePostImageList(postId, imageIds)).willReturn(postImages);
        willDoNothing().given(postImageService).deleteAll(postId, updateTime);

        given(postUserLikeService.isUserLikedPost(postId)).willReturn(isUserLikedPost);
        given(postImageService.getFileContentByImageIds(updatePostRequest.getImageIds())).willReturn(fileContents);
        given(pushService.findPush(pushId)).willReturn(Optional.of(push));
        given(postUpdateResponseConverter.convert(
            post,
            push,
            board,
            isUserLikedPost,
            fileContents,
            postUserProfile
        )).willReturn(postCreateUpdateResponse);
        given(thumbnailImageService.generate(imageIds, content, existingThumbnailUrl))
            .willReturn(thumbnailImageUrl);
        given(updatePostRequest.getPush()).willReturn(pushRequest);
        willDoNothing().given(pushService).updatePush(push, pushRequest, updateTimeInstant);
        given(postUpdatePreProcessor.isActiveStartDateInFuture(eq(post), any(LocalDateTime.class))).willReturn(false);
        willDoNothing().given(postPushUpdaterResolver).update(any(PostPushUpdaterInput.class));
        given(updatePostRequest.getBoardCategory()).willReturn(boardCategory.name());
        willDoNothing().given(postCategoryValidator).validateForUpdate(boardCategory);
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenUpdateBoardGroupPostWithPollSuccess extends DefaultTestCases {

        @BeforeEach
        void setUp() {
            given(post.getPolls()).willReturn(List.of(poll));
            given(updatePostRequest.getPolls()).willReturn(List.of(updatePollRequest));
            given(DateTimeConverter.convert(updatePollRequest.getTargetEndDate())).willReturn(updateTime);
            given(post.getDigitalProxy()).willReturn(null);
            given(post.getIsNotification()).willReturn(false);
            willDoNothing().given(notificationService).updateNotification(post, false);

            actualResponse = service.updateBoardGroupPost(updatePostRequestDto);
        }

        @Test
        void shouldSetTargetEndDate() {
            then(poll).should().setTargetEndDate(updateTime);
        }
    }

    @Nested
    class WhenUpdateBoardGroupPostWithDigitalProxySuccess extends DefaultTestCases {

        @BeforeEach
        void setUp() {
            given(post.getFirstPoll()).willReturn(null);
            given(post.getDigitalProxy()).willReturn(digitalProxy);
            given(post.getIsNotification()).willReturn(false);
            willDoNothing().given(notificationService).updateNotification(post, false);

            actualResponse = service.updateBoardGroupPost(updatePostRequestDto);
        }

        @Test
        void shouldSetTargetEndDate() {
            then(digitalProxy).should().setTargetEndDate(updateTime);
        }
    }

    @Nested
    class WhenUpdateBoardGroupPostWithPush extends DefaultTestCases {

        @BeforeEach
        void setUp() {
            given(post.getFirstPoll()).willReturn(null);
            given(post.getDigitalProxy()).willReturn(digitalProxy);
            given(post.getIsNotification()).willReturn(false);
            willDoNothing().given(notificationService).updateNotification(post, false);
            given(postUpdatePreProcessor.isActiveStartDateInFuture(eq(post), any(LocalDateTime.class))).willReturn(true);

            actualResponse = service.updateBoardGroupPost(updatePostRequestDto);
        }

        @Test
        void shouldSetTargetEndDate() {
            then(digitalProxy).should().setTargetEndDate(updateTime);
        }

        @Test
        void shouldCallPostUpdaterResolver() {
            then(postPushUpdaterResolver).should().update(any(PostPushUpdaterInput.class));
        }
    }

    @Nested
    class WhenUpdateBoardGroupPostWithPushIsNull extends DefaultTestCases {

        @BeforeEach
        void setUp() {
            given(post.getFirstPoll()).willReturn(null);
            given(post.getDigitalProxy()).willReturn(digitalProxy);
            given(post.getIsNotification()).willReturn(false);
            given(post.getPushId()).willReturn(null);
            willDoNothing().given(notificationService).updateNotification(post, false);
            given(postUpdatePreProcessor.isActiveStartDateInFuture(eq(post), any(LocalDateTime.class))).willReturn(true);
            push = null;//shouldConvertResponse 에서 공통으로 사용되서 체크시 null 이여야 한다.
            given(postUpdateResponseConverter.convert(
                post,
                push,
                board,
                isUserLikedPost,
                fileContents,
                postUserProfile
            )).willReturn(postCreateUpdateResponse);
            actualResponse = service.updateBoardGroupPost(updatePostRequestDto);
        }

        @Test
        void shouldCallPostUpdaterResolver() {
            then(postPushUpdaterResolver).should().update(any(PostPushUpdaterInput.class));
        }
    }

    @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
    class DefaultTestCases {

        @Test
        void shouldReturnPostUpdateResponse() {
            assertThat(actualResponse.getData(), is(postCreateUpdateResponse));
        }

        @Test
        void shouldCallDeleteAllImages() {
            then(postImageService).should().deleteAll(postId, updateTime);
        }

        @Test
        void shouldCallSavePostImageList() {
            then(postImageService).should().savePostImageList(postId, imageIds);
        }

        @Test
        void shouldCallSavePost() {
            then(postService).should().savePost(post);
        }

        @Test
        void shouldCallGetIsActiveStatus() {
            then(postIsActiveDecisionMaker).should().getIsActiveStatus(user, isActive);
        }

        @Test
        void shouldConvertResponse() {
            then(postUpdateResponseConverter).should().convert(
                post,
                push,
                board,
                isUserLikedPost,
                fileContents,
                postUserProfile
            );
        }
    }
}
