package ag.act.service.stockboardgrouppost;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.post.UpdatePostRequestDto;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.validator.post.StockBoardGroupPostCategoryChangeValidator;
import ag.act.validator.post.StockBoardGroupPostValidator;
import org.hamcrest.Matchers;
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

import static ag.act.TestUtil.someBoardGroupForStock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class PostUpdatePreProcessorTest {

    @InjectMocks
    private PostUpdatePreProcessor processor;
    private List<MockedStatic<?>> statics;
    @Mock
    private StockBoardGroupPostValidator stockBoardGroupPostValidator;
    @Mock
    private StockBoardGroupPostCategoryChangeValidator stockBoardGroupPostCategoryChangeValidator;
    @Mock
    private User user;
    private Long userId;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));

        userId = someLong();

        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(user.getId()).willReturn(userId);
    }

    @Nested
    class ValidatePostForUpdate {

        @Mock
        private UpdatePostRequestDto updatePostRequestDto;
        @Mock
        private ag.act.model.UpdatePostRequest updatePostRequest;
        @Mock
        private ag.act.model.TargetDateRequest targetDateRequest;
        @Mock
        private Post post;
        @Mock
        private Board board;
        private Long postId;
        private String stockCode;
        private String boardGroupName;
        private String boardCategoryName;
        private Post actualPost;
        private static final Boolean isAnonymousTrue = Boolean.TRUE;

        @BeforeEach
        void setUp() {
            postId = someLong();
            stockCode = someString(5);
            boardGroupName = someBoardGroupForStock().name();
            boardCategoryName = someEnum(BoardCategory.class).name();

            given(post.getUserId()).willReturn(userId);
            given(post.getIsAnonymous()).willReturn(isAnonymousTrue);
            given(post.getBoard()).willReturn(board);
            given(updatePostRequest.getUpdateTargetDate()).willReturn(targetDateRequest);
            given(updatePostRequest.getIsAnonymous()).willReturn(isAnonymousTrue);
            given(updatePostRequestDto.getUpdatePostRequest()).willReturn(updatePostRequest);
            given(updatePostRequestDto.getUpdatePostRequest().getBoardCategory()).willReturn(boardCategoryName);
            given(updatePostRequestDto.getStockCode()).willReturn(stockCode);
            given(updatePostRequestDto.getBoardGroupName()).willReturn(boardGroupName);
            given(updatePostRequestDto.getPostId()).willReturn(postId);

            willReturn(post).given(stockBoardGroupPostCategoryChangeValidator)
                .validate(postId, stockCode, boardGroupName, boardCategoryName);
            willDoNothing().given(stockBoardGroupPostValidator).validateAuthor(user, userId, "게시글");
            willDoNothing().given(stockBoardGroupPostValidator).validateUpdateTargetDate(eq(post), any(Instant.class), any(Instant.class));
            willDoNothing().given(stockBoardGroupPostValidator).validateUpdateTargetDateWhenPollsAreNull(post, updatePostRequestDto);
            willDoNothing().given(stockBoardGroupPostValidator).validateUpdateTargetDateForDigitalProxy(post, updatePostRequestDto);
            willDoNothing().given(stockBoardGroupPostValidator).validateUpdateTargetDateForDigitalDocument(post, updatePostRequestDto);
        }

        @Nested
        class WhenValidatePostForUpdateSuccess {

            @BeforeEach
            void setUp() {
                actualPost = processor.proceed(updatePostRequestDto);
            }

            @Test
            void shouldReturnValidPost() {
                assertThat(actualPost, Matchers.is(post));
            }

            @Test
            void shouldValidateAuthor() {
                then(stockBoardGroupPostValidator).should().validateAuthor(user, userId, "게시글");
            }

            @Test
            void shouldValidateForChangeCategoryPost() {
                then(stockBoardGroupPostCategoryChangeValidator).should().validate(postId, stockCode, boardGroupName, boardCategoryName);
            }
        }
    }
}
