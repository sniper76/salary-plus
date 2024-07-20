package ag.act.facade.virtualboard;


import ag.act.converter.post.BoardGroupPostResponseConverter;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.entity.Post;
import ag.act.service.virtualboard.VirtualBoardGroupPostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@MockitoSettings(strictness = Strictness.LENIENT)
public class VirtualBoardGroupPostFacadeTest {
    @InjectMocks
    private VirtualBoardGroupPostFacade facade;
    @Mock
    private VirtualBoardGroupPostService virtualBoardGroupPostService;
    @Mock
    private BoardGroupPostResponseConverter boardGroupPostResponseConverter;

    @Nested
    class WhenGetVirtualBoardGroupsPost {
        @Mock
        private GetBoardGroupPostDto getBoardGroupPostDto;
        @Mock
        private PageRequest pageRequest;
        @Mock
        private Page<Post> expectedPage;
        @Mock
        private ag.act.model.GetBoardGroupPostResponse expected;

        @BeforeEach
        void setUp() {
            given(virtualBoardGroupPostService.getBestPosts(
                getBoardGroupPostDto,
                pageRequest
            )).willReturn(expectedPage);
            given(boardGroupPostResponseConverter.convert(expectedPage)).willReturn(expected);
        }

        @Test
        void shouldReturnBestPosts() {
            // When
            ag.act.model.GetBoardGroupPostResponse actual = facade.getVirtualBoardGroupPosts(getBoardGroupPostDto, pageRequest);

            // Then
            assertThat(actual, is(expected));
            then(virtualBoardGroupPostService)
                .should()
                .getBestPosts(getBoardGroupPostDto, pageRequest);
        }

    }
}
