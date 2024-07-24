package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.HolderListReadAndCopyDigitalDocumentResponseConverter;
import ag.act.converter.post.BoardGroupPostResponseConverter;
import ag.act.converter.post.PostDetailResponseConverter;
import ag.act.datacollector.HolderListReadAndCopyDigitalDocumentResponseDataCollector;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.dto.HolderListReadAndCopyDigitalDocumentResponseData;
import ag.act.dto.post.CreatePostRequestDto;
import ag.act.dto.post.DeletePostRequestDto;
import ag.act.dto.post.UpdatePostRequestDto;
import ag.act.entity.FileContent;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.model.HolderListReadAndCopyDigitalDocumentResponse;
import ag.act.model.PostDataResponse;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import ag.act.model.UserDigitalDocumentResponse;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.poll.PollAnswerService;
import ag.act.service.post.PostImageService;
import ag.act.service.post.PostService;
import ag.act.service.post.PostUserViewService;
import ag.act.service.stockboardgrouppost.BlockedUserService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostCreateService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostDeleteService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostUpdateService;
import ag.act.util.StatusUtil;
import ag.act.validator.post.StockBoardGroupPostValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ag.act.TestUtil.someBoardGroupForStock;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@SuppressWarnings("checkstyle:LineLength")
@MockitoSettings(strictness = Strictness.LENIENT)
class StockBoardGroupPostServiceTest {
    @InjectMocks
    private StockBoardGroupPostService service;
    @Mock
    private StockBoardGroupPostCreateService stockBoardGroupPostCreateService;
    @Mock
    private StockBoardGroupPostUpdateService stockBoardGroupPostUpdateService;
    @Mock
    private StockBoardGroupPostDeleteService stockBoardGroupPostDeleteService;
    @Mock
    private PostService postService;
    @Mock
    private PostUserViewService postUserViewService;
    @Mock
    private BoardService boardService;
    @Mock
    private PostImageService postImageService;
    @Mock
    private PostDetailResponseConverter postDetailResponseConverter;
    @Mock
    private BoardGroupPostResponseConverter boardGroupPostResponseConverter;
    @Mock
    private List<FileContent> postImages;
    @Mock
    private ag.act.model.PostResponse postResponse;
    @Mock
    private PollAnswerService pollAnswerService;
    @Mock
    private Post updatedPost;
    @Mock
    private User user;
    @Mock
    private DigitalDocumentService digitalDocumentService;
    @Mock
    private StockBoardGroupPostValidator stockBoardGroupPostValidator;
    @Mock
    private BlockedUserService blockedUserService;
    @Mock
    private HolderListReadAndCopyDigitalDocumentResponseConverter holderListReadAndCopyDigitalDocumentResponseConverter;
    @Mock
    private HolderListReadAndCopyDigitalDocumentResponseDataCollector holderListReadAndCopyDigitalDocumentResponseDataCollector;

    private String stockCode;
    private Long postId;

    private List<MockedStatic<?>> statics;
    private String boardGroupName;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenGetBoardGroupPostDetail {

        @Mock
        private Post post;
        @Mock
        private Poll poll;
        @Mock
        private DigitalDocument digitalDocument;
        @Mock
        private UserDigitalDocumentResponse postResponseDigitalDocument;
        @Mock
        private HolderListReadAndCopyDigitalDocumentResponseData holderListReadAndCopyDigitalDocumentResponseData;
        @Mock
        private HolderListReadAndCopyDigitalDocumentResponse holderListReadAndCopyDigitalDocument;

        @BeforeEach
        void setUp() {
            // Given
            stockCode = someStockCode();
            postId = someLong();
            boardGroupName = someString(5);
            Long pollId = someLong();
            Long userId = someLong();

            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);
            given(postService.getPostNotDeleted(postId)).willReturn(post);
            given(post.getId()).willReturn(postId);
            given(post.getFirstPoll()).willReturn(poll);
            given(poll.getId()).willReturn(pollId);
            given(postImageService.getFileContentsByPostId(postId)).willReturn(postImages);
            given(postService.savePost(post)).willReturn(updatedPost);
            given(updatedPost.getId()).willReturn(postId);
            given(postDetailResponseConverter.convertWithAnswer(updatedPost, Map.of(), Map.of(), postImages)).willReturn(postResponse);
            given(postDetailResponseConverter.convert(postResponse)).willReturn(new PostDataResponse().data(postResponse));
            given(pollAnswerService.getAllByPollIdAndUserId(pollId, userId)).willReturn(List.of());
            given(digitalDocumentService.getPostResponseDigitalDocument(updatedPost, user)).willReturn(postResponseDigitalDocument);
            given(post.isHolderListReadAndCopyDocumentType()).willReturn(Boolean.TRUE);
            given(post.getDigitalDocument()).willReturn(digitalDocument);
            given(holderListReadAndCopyDigitalDocumentResponseDataCollector.collect(digitalDocument))
                .willReturn(Optional.of(holderListReadAndCopyDigitalDocumentResponseData));
            given(holderListReadAndCopyDigitalDocumentResponseConverter.convert(holderListReadAndCopyDigitalDocumentResponseData))
                .willReturn(holderListReadAndCopyDigitalDocument);

            willDoNothing().given(stockBoardGroupPostValidator).validateBoardGroupAndStockCodeOfBoardGroupPost(post, stockCode, boardGroupName);
        }

        @Nested
        class WhenGetBoardGroupPostDetailSuccess extends DefaultTestCases {
            // success case
        }

        @Nested
        class WhenExceptionOccursWhenUpdatePostUserViewCount extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                willThrow(RuntimeException.class).given(postUserViewService).createOrUpdatePostUserViewCount(postId);
            }
        }

        @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
        class DefaultTestCases {

            @Test
            void shouldGetBoardGroupPostDetail() {

                // When
                PostDataResponse actual = service.getBoardGroupPostDetailWithUpdateViewCount(postId, stockCode, boardGroupName);

                // Then
                assertThat(actual, notNullValue());
                assertThat(actual.getData(), is(postResponse));
                then(postService).should().getPostNotDeleted(postId);
                then(postImageService).should().getFileContentsByPostId(postId);
                then(postUserViewService).should().createOrUpdatePostUserViewCount(postId);
                then(postUserViewService).should().sumViewCountByPostId(postId);
                then(postUserViewService).should().countByPostId(postId);
                then(postService).should().savePost(post);
                then(postDetailResponseConverter).should().convertWithAnswer(updatedPost, Map.of(), Map.of(), postImages);
                then(postDetailResponseConverter).should().convert(postResponse);
            }
        }
    }

    @Nested
    class WhenGetBoardGroupPosts {
        @Mock
        private PageRequest pageRequest;
        @Mock
        private Page<Post> boardPosts;
        @Mock
        private ag.act.model.GetBoardGroupPostResponse getBoardGroupPostResponse;
        @Mock
        private GetBoardGroupPostDto getBoardGroupPostDto;
        private List<Status> statusList;

        @BeforeEach
        void setUp() {
            // Given
            statusList = StatusUtil.getPostStatusesVisibleToUsers();
            stockCode = someStockCode();
            given(getBoardGroupPostDto.getStockCode()).willReturn(stockCode);
        }

        @Test
        void shouldGetBoardGroupPostsByAllCategories() {
            // Given
            final BoardGroup boardGroup = someBoardGroupForStock();
            given(getBoardGroupPostDto.getBoardGroupName()).willReturn(boardGroup.name());
            given(getBoardGroupPostDto.getBoardGroup()).willReturn(boardGroup);
            given(getBoardGroupPostDto.isValidBoardCategory()).willReturn(Boolean.FALSE);
            given(postService.getBoardPostsByStockCodeAndBoardGroup(stockCode, boardGroup, List.of(), statusList, pageRequest))
                .willReturn(boardPosts);
            given(boardGroupPostResponseConverter.convert(boardPosts))
                .willReturn(getBoardGroupPostResponse);

            // When
            GetBoardGroupPostResponse actual = service.getBoardGroupPosts(getBoardGroupPostDto, pageRequest);

            // Then
            assertThat(actual, is(notNullValue()));
            assertThat(actual, is(getBoardGroupPostResponse));
            then(postService).should().getBoardPostsByStockCodeAndBoardGroup(stockCode, boardGroup, List.of(), statusList, pageRequest);
            then(boardGroupPostResponseConverter).should().convert(boardPosts);
        }

        @Test
        void shouldGetBoardGroupPostsByCategory() {
            // Given
            final Long boardId = someLong();
            final BoardCategory boardCategory = someThing(BoardCategory.activeBoardCategoriesForStocks());
            final List<BoardCategory> boardCategories = List.of(boardCategory);

            given(getBoardGroupPostDto.isValidBoardCategory()).willReturn(Boolean.TRUE);
            given(getBoardGroupPostDto.getBoardCategories()).willReturn(boardCategories);
            given(getBoardGroupPostDto.getStockCode()).willReturn(stockCode);

            final List<Long> boardIds = List.of(boardId);
            given(boardService.getBoardIdsByStockCodeAndCategoryIn(stockCode, boardCategories))
                .willReturn(boardIds);
            given(postService.getBoardPosts(boardIds, List.of(), statusList, pageRequest))
                .willReturn(boardPosts);
            given(boardGroupPostResponseConverter.convert(boardPosts))
                .willReturn(getBoardGroupPostResponse);

            // When
            GetBoardGroupPostResponse actual = service.getBoardGroupPosts(getBoardGroupPostDto, pageRequest);

            // Then
            assertThat(actual, is(notNullValue()));
            assertThat(actual, is(getBoardGroupPostResponse));
            then(boardService).should().getBoardIdsByStockCodeAndCategoryIn(stockCode, boardCategories);
            then(postService).should().getBoardPosts(boardIds, List.of(), statusList, pageRequest);
            then(boardGroupPostResponseConverter).should().convert(boardPosts);
        }
    }

    @Nested
    class WhenCreateBoardGroupPost {

        @Mock
        private CreatePostRequestDto createPostRequestDto;
        @Mock
        private ag.act.model.PostDetailsDataResponse postCreateResponse;

        @BeforeEach
        void setUp() {
            given(stockBoardGroupPostCreateService.createBoardGroupPost(createPostRequestDto))
                .willReturn(postCreateResponse);
        }

        @Nested
        class WhenCreatePostSuccess {
            @Test
            void shouldCreatePostWithoutPollAndDigitalProxy() {
                // When
                PostDetailsDataResponse actual = service.createBoardGroupPost(createPostRequestDto);

                // Then
                assertThat(actual, is(postCreateResponse));
                then(stockBoardGroupPostCreateService).should().createBoardGroupPost(createPostRequestDto);
            }
        }
    }

    @Nested
    class WhenUpdateBoardGroupPost {

        @Mock
        private UpdatePostRequestDto updatePostRequestDto;
        @Mock
        private ag.act.model.PostDetailsDataResponse postUpdateResponse;

        @BeforeEach
        void setUp() {
            given(stockBoardGroupPostUpdateService.updateBoardGroupPost(updatePostRequestDto))
                .willReturn(postUpdateResponse);
        }

        @Nested
        class WhenCreatePostSuccess {
            @Test
            void shouldCreatePostWithoutPollAndDigitalProxy() {
                // When
                ag.act.model.PostDetailsDataResponse actual = service.updateBoardGroupPost(updatePostRequestDto);

                // Then
                assertThat(actual, is(postUpdateResponse));
                then(stockBoardGroupPostUpdateService).should().updateBoardGroupPost(updatePostRequestDto);
            }
        }
    }

    @Nested
    class DeleteBoardGroupPost {

        @Mock
        private DeletePostRequestDto deletePostRequestDto;
        @Mock
        private SimpleStringResponse simpleOkayResponse;

        @BeforeEach
        void setUp() {
            given(stockBoardGroupPostDeleteService.deleteBoardGroupPost(deletePostRequestDto)).willReturn(simpleOkayResponse);
        }

        @Test
        void shouldDeletePostAndRelatedContents() {

            // When
            final SimpleStringResponse actual = service.deleteBoardGroupPost(deletePostRequestDto);

            // Then
            assertThat(actual, is(simpleOkayResponse));
            then(stockBoardGroupPostDeleteService).should().deleteBoardGroupPost(deletePostRequestDto);
        }
    }
}
