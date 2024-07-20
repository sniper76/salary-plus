package ag.act.service;

import ag.act.dto.PostDuplicateDto;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Stock;
import ag.act.enums.BoardCategory;
import ag.act.service.post.PostImageService;
import ag.act.service.post.duplication.PostDuplicateProcessor;
import ag.act.service.post.duplication.PostDuplicateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class PostDuplicateServiceTest {
    @InjectMocks
    private PostDuplicateService service;
    @Mock
    private BoardService boardService;
    @Mock
    private PostImageService postImageService;
    @Mock
    private PostDuplicateProcessor postDuplicateProcessor;

    @Nested
    class DuplicatePosts {
        @Mock
        private Post post;
        @Mock
        private Post anotherCopiedPost;
        @Mock
        private Board postBoard;
        @Mock
        private Board anotherBoard;
        @Mock
        private PostUserProfile postUserProfile;
        @Mock
        private PostUserProfile anotherCopiedPostUserProfile;
        @Mock
        private Stock stock;
        private BoardCategory category;
        private String postStockCode;
        private String anotherStockCode;
        private int actualDuplicatedCount;
        private List<PostImage> postImages;

        @BeforeEach
        void setUp() {
            postStockCode = someStockCode();
            anotherStockCode = someStockCode();
            List<String> stockCodes = List.of(postStockCode, anotherStockCode);
            postImages = List.of();
            final Long anotherBoardId = someLong();
            Long anotherCopiedPostId = someLong();
            Long postImage1Id = someLong();
            Long postImage2Id = someLong();
            category = someEnum(BoardCategory.class);
            List<PostImage> anotherCopiedPostImages = List.of();
            String stockName = someString(10);

            PostDuplicateDto postDuplicateDtoStub = new PostDuplicateDto(stockCodes, post, postBoard, postUserProfile, postImages);

            given(stock.getName()).willReturn(stockName);

            given(postBoard.getId()).willReturn(someLong());
            given(anotherBoard.getId()).willReturn(anotherBoardId);
            given(postBoard.getCategory()).willReturn(category);
            given(boardService.findBoard(postStockCode, category)).willReturn(Optional.of(postBoard));
            given(boardService.findBoard(anotherStockCode, category)).willReturn(Optional.of(anotherBoard));

            given(post.copyOf(anotherBoardId)).willReturn(anotherCopiedPost);
            given(anotherCopiedPost.getId()).willReturn(anotherCopiedPostId);

            given(postUserProfile.copyOf(anotherCopiedPostId)).willReturn(anotherCopiedPostUserProfile);
            given(postImageService.savePostImageList(anotherCopiedPostId, List.of(postImage1Id, postImage2Id)))
                .willReturn(anotherCopiedPostImages);

            given(postDuplicateProcessor.copyPost(post, postUserProfile, postImages, anotherBoard))
                .willAnswer(invocation -> invocation.getArgument(0));

            actualDuplicatedCount = service.duplicatePosts(postDuplicateDtoStub);
        }

        @Test
        void shouldReturnDuplicatedCount() {
            assertThat(actualDuplicatedCount, is(1));
        }

        @Test
        void shouldGetBoardWithPostStockCodeAndCategory() {
            then(boardService).should().findBoard(postStockCode, category);
        }

        @Test
        void shouldGetBoardWithAnotherStockCodeAndCategory() {
            then(boardService).should().findBoard(anotherStockCode, category);
        }

        @Test
        void shouldCopyPost() {
            then(postDuplicateProcessor).should().copyPost(post, postUserProfile, postImages, anotherBoard);
        }
    }
}
