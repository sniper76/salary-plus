package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.PostUserLike;
import ag.act.entity.PostUserProfile;
import ag.act.entity.User;
import ag.act.enums.BoardGroup;
import ag.act.model.PostDataResponse;
import ag.act.service.post.PostService;
import ag.act.service.post.PostUserLikeService;
import ag.act.service.push.AutomatedAuthorPushService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostLikeService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostService;
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

import java.util.List;

import static ag.act.TestUtil.someBoardGroupForStock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockBoardGroupPostLikeServiceTest {
    @InjectMocks
    private StockBoardGroupPostLikeService likeService;

    @Mock
    private PostService postService;
    @Mock
    private StockBoardGroupPostValidator stockBoardGroupPostValidator;
    @Mock
    private PostUserLikeService postUserLikeService;
    @Mock
    private StockBoardGroupPostService stockBoardGroupPostService;
    @Mock
    private AutomatedAuthorPushService automatedAuthorPushService;

    private List<MockedStatic<?>> statics;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));

        willDoNothing().given(automatedAuthorPushService).createAutomatedAuthorPushForPostUserLike(any(Post.class), anyLong());
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenPostUserLike {
        @Mock
        private Post post;
        @Mock
        private User user;
        @Mock
        private List<PostUserLike> postUserLikeDeleteList;
        @Mock
        private PostUserLike postUserLike;
        @Mock
        private PostUserProfile postUserProfile;
        @Mock
        private Board board;
        @Mock
        private PostDataResponse postDetailsDataResponse;

        private String stockCode;
        private BoardGroup boardGroup;
        private Long postId;

        @BeforeEach
        void setUp() {
            stockCode = someString(0);
            boardGroup = someBoardGroupForStock();
            final Long userId = someLong();
            postId = someLong();
            final Long sumCount = someLong();

            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(stockBoardGroupPostValidator.validateBoardGroupPost(
                postId, stockCode, boardGroup.name(), StatusUtil.getDeleteStatuses()
            )).willReturn(post);
            given(user.getId()).willReturn(userId);
            given(postUserLikeService.findAllByPostIdAndUserId(postId, userId))
                .willReturn(postUserLikeDeleteList);
            willDoNothing().given(postUserLikeService).deletePostUserLikeList(postUserLikeDeleteList);
            willDoNothing().given(postUserLikeService).savePostUserLike(postUserLike);
            given(postUserLikeService.countByPostId(postId)).willReturn(sumCount);
            given(postService.savePost(post)).willReturn(post);
            given(post.getId()).willReturn(postId);
            given(post.getBoard()).willReturn(board);
            given(post.getPostUserProfile()).willReturn(postUserProfile);
            given(stockBoardGroupPostService.getBoardGroupPostDetail(post))
                .willReturn(postDetailsDataResponse);
        }

        @Nested
        class WhenLikeFlagIsTrue {
            @Test
            void shouldBeSuccess() {
                PostDataResponse postCreateResponse = likeService.likeBoardGroupPost(
                    stockCode, boardGroup.name(), postId, Boolean.TRUE
                );

                assertThat(postCreateResponse, is(postDetailsDataResponse));
            }
        }

        @Nested
        class WhenLikeFlagIsFalse {
            @Test
            void shouldBeSuccess() {
                PostDataResponse postCreateResponse = likeService.likeBoardGroupPost(
                    stockCode, boardGroup.name(), postId, Boolean.FALSE
                );

                assertThat(postCreateResponse, is(postDetailsDataResponse));
            }
        }
    }
}
