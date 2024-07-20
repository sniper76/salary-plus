package ag.act.service;

import ag.act.converter.post.BoardCategoryResponseConverter;
import ag.act.dto.boardcategory.BoardCategoryGroups;
import ag.act.enums.BoardGroup;
import ag.act.model.BoardCategoryDataArrayResponse;
import ag.act.model.BoardCategoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static ag.act.TestUtil.someBoardGroupForStock;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@MockitoSettings(strictness = Strictness.LENIENT)
class BoardServiceTest {
    @InjectMocks
    private BoardService boardService;
    @Mock
    private BoardCategoryResponseConverter boardCategoryResponseConverter;
    @Mock
    private List<BoardCategoryResponse> boardCategoryResponses;
    private BoardGroup boardGroup;

    @BeforeEach
    void setUp() {
        boardGroup = someBoardGroupForStock();
    }

    @Nested
    class WhenGetBoardGroupCategories {
        @Test
        void shouldReturnBoardCategoryDataArrayResponse() {
            // Given
            given(boardCategoryResponseConverter.convert(any(BoardCategoryGroups.class)))
                .willReturn(boardCategoryResponses);

            // When
            BoardCategoryDataArrayResponse actual = boardService.getBoardGroupCategories(boardGroup);

            // Then
            assertThat(actual.getData(), is(boardCategoryResponses));
            then(boardCategoryResponseConverter).should().convert(any(BoardCategoryGroups.class));
        }
    }
}
