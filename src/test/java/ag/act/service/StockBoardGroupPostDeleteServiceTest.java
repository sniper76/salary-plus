package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.post.DeletePostRequestDto;
import ag.act.entity.Board;
import ag.act.entity.DigitalProxy;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.BoardCategory;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.digitaldocument.modusign.DigitalProxyModuSignService;
import ag.act.service.notification.NotificationService;
import ag.act.service.poll.PollService;
import ag.act.service.post.PostImageService;
import ag.act.service.post.PostService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostDeleteService;
import ag.act.service.user.UserRoleService;
import ag.act.util.StatusUtil;
import ag.act.validator.post.PostCategoryValidator;
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

import java.time.LocalDateTime;
import java.util.List;

import static ag.act.TestUtil.someBoardGroupForStock;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockBoardGroupPostDeleteServiceTest {
    @InjectMocks
    private StockBoardGroupPostDeleteService service;
    @Mock
    private PollService pollService;
    @Mock
    private PostService postService;
    @Mock
    private PostImageService postImageService;
    @Mock
    private StockBoardGroupPostValidator stockBoardGroupPostValidator;
    @Mock
    private DigitalDocumentService digitalDocumentService;
    @Mock
    private DigitalProxyModuSignService digitalProxyModuSignService;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private PostCategoryValidator postCategoryValidator;

    private Long postId;

    private List<MockedStatic<?>> statics;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class), mockStatic(LocalDateTime.class));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class DeleteBoardGroupPost {

        @Mock
        private DeletePostRequestDto deletePostRequestDto;
        @Mock
        private Post post;
        @Mock
        private Board board;
        @Mock
        private Poll poll;
        @Mock
        private List<Poll> polls;
        @Mock
        private User currentUser;
        @Mock
        private LocalDateTime deleteTime;
        @Mock
        private DigitalDocument digitalDocument;
        @Mock
        private DigitalProxy digitalProxy;

        private Long writerUserId;
        private Long currentUserId;
        private SimpleStringResponse actualResponse;

        private Status expectdStatus;

        @BeforeEach
        void setUp() {
            // Given
            writerUserId = someLong();
            currentUserId = someLong();
            postId = someLong();
            final String stockCode = someStockCode();
            final String boardGroupName = someBoardGroupForStock().name();
            final BoardCategory boardCategory = someThing(BoardCategory.activeBoardCategoriesForStocks());

            given(LocalDateTime.now()).willReturn(deleteTime);
            given(deletePostRequestDto.getPostId()).willReturn(postId);
            given(deletePostRequestDto.getStockCode()).willReturn(stockCode);
            given(deletePostRequestDto.getBoardGroupName()).willReturn(boardGroupName);
            given(ActUserProvider.getNoneNull()).willReturn(currentUser);
            given(currentUser.isAdmin()).willReturn(Boolean.FALSE);
            given(currentUser.getId()).willReturn(currentUserId);
            given(stockBoardGroupPostValidator.validateBoardGroupPost(deletePostRequestDto, StatusUtil.getDeleteStatuses())).willReturn(post);
            given(post.getId()).willReturn(postId);
            given(post.getUserId()).willReturn(writerUserId);
            given(post.getFirstPoll()).willReturn(poll);
            given(post.getDigitalDocument()).willReturn(digitalDocument);
            given(post.getDigitalProxy()).willReturn(digitalProxy);
            given(userRoleService.isAdmin(writerUserId)).willReturn(Boolean.FALSE);
            given(post.getPolls()).willReturn(polls);
            willDoNothing().given(stockBoardGroupPostValidator).validateAuthor(currentUser, writerUserId, "게시글");
            willDoNothing().given(postImageService).deleteAll(postId, deleteTime);
            willDoNothing().given(pollService).deletePolls(eq(polls), eq(Status.DELETED_BY_USER), any(LocalDateTime.class));
            given(postService.deletePost(eq(post), eq(Status.DELETED_BY_USER), any(LocalDateTime.class))).willReturn(post);
            willDoNothing().given(notificationService).updateNotificationStatusByPostIdIfExist(anyLong(), any(Status.class));
            given(post.getBoard()).willReturn(board);
            given(board.getCategory()).willReturn(boardCategory);
            willDoNothing().given(postCategoryValidator).validateForDelete(boardCategory);
        }

        @Nested
        class WhenAdminDeleteUserWritePost extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                expectdStatus = Status.DELETED_BY_ADMIN;

                given(currentUser.isAdmin()).willReturn(Boolean.TRUE);
                given(currentUser.getId()).willReturn(currentUserId);
                given(post.getUserId()).willReturn(currentUserId);

                actualResponse = service.deleteBoardGroupPost(deletePostRequestDto);
            }

            @Test
            void shouldValidateWriterUser() {
                then(stockBoardGroupPostValidator).should(never()).validateAuthor(currentUser, post.getUserId(), "게시글");
            }
        }

        @Nested
        class WhenAdminDeleteAdminPost extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                expectdStatus = Status.DELETED;

                given(currentUser.isAdmin()).willReturn(Boolean.TRUE);
                given(currentUser.getId()).willReturn(currentUserId);
                given(post.getUserId()).willReturn(writerUserId);
                given(userRoleService.isAdmin(writerUserId)).willReturn(Boolean.TRUE);

                actualResponse = service.deleteBoardGroupPost(deletePostRequestDto);
            }

            @Test
            void shouldValidateWriterUser() {
                then(stockBoardGroupPostValidator).should(never()).validateAuthor(currentUser, post.getUserId(), "게시글");
            }
        }

        @Nested
        class WhenAdminDeleteUserPost extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                expectdStatus = Status.DELETED_BY_ADMIN;

                given(currentUser.isAdmin()).willReturn(Boolean.TRUE);
                given(currentUser.getId()).willReturn(currentUserId);
                given(post.getUserId()).willReturn(writerUserId);

                actualResponse = service.deleteBoardGroupPost(deletePostRequestDto);
            }

            @Test
            void shouldValidateWriterUser() {
                then(stockBoardGroupPostValidator).should(never()).validateAuthor(currentUser, post.getUserId(), "게시글");
            }
        }

        @Nested
        class WhenUserDeleteHisOwnPost extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                expectdStatus = Status.DELETED_BY_USER;

                given(currentUser.isAdmin()).willReturn(Boolean.FALSE);
                given(currentUser.getId()).willReturn(writerUserId);
                given(post.getUserId()).willReturn(writerUserId);

                actualResponse = service.deleteBoardGroupPost(deletePostRequestDto);
            }

            @Test
            void shouldValidateWriterUser() {
                then(stockBoardGroupPostValidator).should().validateAuthor(currentUser, post.getUserId(), "게시글");
            }
        }

        @SuppressWarnings({"unused", "JUnitMalformedDeclaration"})
        class DefaultTestCases {

            @Test
            void shouldResultOkay() {
                assertThat(actualResponse.getStatus(), is("ok"));
            }

            @Test
            void shouldDeleteAllImages() {
                then(postImageService).should().deleteAll(postId, deleteTime);
            }

            @Test
            void shouldDeleteDigitalDocument() {
                then(digitalDocumentService).should().deleteDigitalDocument(digitalDocument, expectdStatus, deleteTime);
            }

            @Test
            void shouldDeleteDigitalProxy() {
                then(digitalProxyModuSignService).should().deleteDigitalProxyModuSign(digitalProxy, expectdStatus, deleteTime);
            }

            @Test
            void shouldDeletePolls() {
                then(pollService).should().deletePolls(polls, expectdStatus, deleteTime);
            }

            @Test
            void shouldDeletePost() {
                then(postService).should().deletePost(post, expectdStatus, deleteTime);
            }
        }
    }
}
