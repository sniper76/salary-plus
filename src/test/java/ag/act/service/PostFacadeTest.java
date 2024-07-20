package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.dto.PostDuplicateDto;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Stock;
import ag.act.facade.post.PostFacade;
import ag.act.service.post.PostImageService;
import ag.act.service.post.PostService;
import ag.act.service.post.PostUserProfileService;
import ag.act.service.post.duplication.PostDuplicateService;
import ag.act.service.stock.StockGroupMappingService;
import ag.act.validator.post.PostDuplicateValidator;
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

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class PostFacadeTest {
    @InjectMocks
    private PostFacade facade;

    @Mock
    private PostService postService;
    @Mock
    private PostDuplicateService postDuplicateService;
    @Mock
    private StockGroupMappingService stockGroupMappingService;
    @Mock
    private PostDuplicateValidator postDuplicateValidator;
    @Mock
    private PostUserProfileService postUserProfileService;
    @Mock
    private PostImageService postImageService;

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
    class DuplicatePosts {

        @Mock
        private Post post;
        @Mock
        private Board board;
        @Mock
        private Stock stock;
        @Mock
        private PostUserProfile postUserProfile;
        private Long postId;
        private Long stockGroupId;
        private List<String> stockCodes;
        private Integer duplicatedPostCount;
        private int actualDuplicatedPostCount;

        @BeforeEach
        void setUp() {
            postId = someLong();
            stockGroupId = someLong();
            final String stockCode1 = someStockCode();
            final String stockCode2 = someStockCode();
            final List<PostImage> postImages = List.of();
            stockCodes = List.of(stockCode1, stockCode2);
            duplicatedPostCount = someIntegerBetween(1, 10);

            given(stockGroupMappingService.getAllStockCodes(stockGroupId)).willReturn(stockCodes);
            given(postService.getPost(postId)).willReturn(post);
            willDoNothing().given(postDuplicateValidator).validateStockCodes(stockCodes);
            given(postDuplicateValidator.validatePostAndGet(post)).willReturn(post);
            given(post.getBoard()).willReturn(board);
            given(board.getStock()).willReturn(stock);
            given(stock.getCode()).willReturn(stockCode1);
            given(postUserProfileService.getPostUserProfileNotNull(postId)).willReturn(postUserProfile);
            given(postImageService.findNotDeletedAllByPostId(postId)).willReturn(postImages);
            given(postDuplicateService.duplicatePosts(any(PostDuplicateDto.class))).willReturn(duplicatedPostCount);

            actualDuplicatedPostCount = facade.duplicatePosts(postId, stockGroupId);
        }

        @Test
        void shouldBeDuplicatedPostCount() {
            assertThat(actualDuplicatedPostCount, is(duplicatedPostCount));
        }

        @Test
        void shouldBeCalledStockGroupMappingServiceGetUniqueStockCodes() {
            then(stockGroupMappingService).should().getAllStockCodes(stockGroupId);
        }

        @Test
        void shouldBeCalledPostServiceGetPostNotNull() {
            then(postService).should().getPost(postId);
        }

        @Test
        void shouldBeCalledPostDuplicateValidatorValidateStockCodes() {
            then(postDuplicateValidator).should().validateStockCodes(stockCodes);
        }

        @Test
        void shouldBeCalledPostDuplicateValidatorValidatePostAndGet() {
            then(postDuplicateValidator).should().validatePostAndGet(post);
        }

        @Test
        void shouldBeCalledPostUserProfileServiceGetPostUserProfileNotNull() {
            then(postUserProfileService).should().getPostUserProfileNotNull(postId);
        }

        @Test
        void shouldBeCalledPostImageServiceFindNotDeletedAllByPostId() {
            then(postImageService).should().findNotDeletedAllByPostId(postId);
        }

        @Test
        void shouldBeCalledPostDuplicateServiceDuplicatePosts() {
            then(postDuplicateService).should().duplicatePosts(any(PostDuplicateDto.class));
        }

    }
}