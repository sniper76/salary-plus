package ag.act.util;

import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@MockitoSettings(strictness = Strictness.LENIENT)
class AppLinkUrlGeneratorTest {
    @InjectMocks
    private AppLinkUrlGenerator generator;

    @Nested
    class WhenGenerateBoardGroupLinkUrl {
        @Test
        void shouldGenerateBoardGroupLinkUrl() {

            // Given
            final String stockCode = "012345";
            final BoardGroup boardGroup = BoardGroup.ACTION;

            // When
            final String actual = generator.generateBoardGroupLinkUrl(stockCode, boardGroup);

            // Then
            assertThat(actual, is("/stock/012345/action"));
        }

        @Test
        void shouldGenerateBoardGroupCategoryLinkUrl() {

            // Given
            final String stockCode = "012345";
            final BoardCategory boardCategory = BoardCategory.SURVEYS;

            // When
            final String actual = generator.generateBoardGroupCategoryLinkUrl(stockCode, boardCategory);

            // Then
            assertThat(actual, is("/stock/012345/action?boardCategory=surveys"));
        }

        @Test
        void shouldGenerateBoardGroupPostLinkUrl() {

            // Given
            final Board board = new Board();
            board.setGroup(BoardGroup.ACTION);
            board.setStockCode("012345");

            final Post post = new Post();
            post.setId(1L);
            post.setBoard(board);

            // When
            final String actual = generator.generateBoardGroupPostLinkUrl(post);

            // Then
            assertThat(actual, is("/stock/012345/board/action/post/1"));
        }
    }
}
