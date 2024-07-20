package ag.act.service;

import ag.act.dto.CreateLatestUserPostsViewDto;
import ag.act.entity.LatestUserPostsView;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.PostsViewType;
import ag.act.repository.LatestUserPostsViewRepository;
import ag.act.service.notification.LatestUserPostsViewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;

@MockitoSettings(strictness = Strictness.LENIENT)
public class LatestUserPostsViewServiceTest {
    @InjectMocks
    private LatestUserPostsViewService latestUserPostsViewService;
    @Mock
    private LatestUserPostsViewRepository latestUserPostsViewRepository;
    @Mock
    private CreateLatestUserPostsViewDto createLatestUserPostsViewDto;

    @Nested
    class CreateOrUpdate {
        @Mock
        private Stock stock;
        private String stockCode;
        @Mock
        private User user;
        private Long userId;
        private final BoardGroup boardGroup = someEnum(BoardGroup.class);
        private final BoardCategory boardCategory = someEnum(BoardCategory.class);
        private final PostsViewType postsViewType = someEnum(PostsViewType.class);

        @BeforeEach
        void setUp() {
            stockCode = someStockCode();
            userId = someLongBetween(1L, 100L);
            given(createLatestUserPostsViewDto.getStock()).willReturn(stock);
            given(stock.getCode()).willReturn(stockCode);
            given(createLatestUserPostsViewDto.getUser()).willReturn(user);
            given(user.getId()).willReturn(userId);
            given(createLatestUserPostsViewDto.getBoardGroup()).willReturn(boardGroup);
            given(createLatestUserPostsViewDto.getBoardCategory()).willReturn(boardCategory);
            given(createLatestUserPostsViewDto.getPostsViewType()).willReturn(postsViewType);
        }

        @Nested
        class WhenAlreadyExist {
            @Mock
            private LatestUserPostsView latestUserPostsView;

            @Test
            void shouldGet() {
                // Given
                given(latestUserPostsViewRepository.findByStockCodeAndUserIdAndBoardGroupAndBoardCategoryAndPostsViewType(
                    stockCode,
                    userId,
                    boardGroup,
                    boardCategory,
                    postsViewType
                )).willReturn(Optional.of(latestUserPostsView));

                // When
                latestUserPostsViewService.createOrUpdate(createLatestUserPostsViewDto);

                // Then
                then(latestUserPostsViewRepository).should().save(latestUserPostsView);
            }
        }

        @Nested
        class WhenNotExist {
            private final PostsViewType postsViewType = someEnum(PostsViewType.class);

            @Test
            void shouldInitialize() {
                // Given
                given(createLatestUserPostsViewDto.getPostsViewType()).willReturn(postsViewType);
                given(latestUserPostsViewRepository.findByStockCodeAndUserIdAndBoardGroupAndBoardCategoryAndPostsViewType(
                    stockCode,
                    userId,
                    boardGroup,
                    boardCategory,
                    postsViewType
                )).willReturn(Optional.empty());

                // ArgumentCaptor to capture the argument passed to save method
                ArgumentCaptor<LatestUserPostsView> captor = ArgumentCaptor.forClass(LatestUserPostsView.class);

                // When
                latestUserPostsViewService.createOrUpdate(createLatestUserPostsViewDto);

                // Then
                then(latestUserPostsViewRepository).should().save(captor.capture());

                LatestUserPostsView saved = captor.getValue();
                assertThat(saved.getStock(), is(stock));
                assertThat(saved.getUser(), is(user));
                assertThat(saved.getBoardGroup(), is(boardGroup));
                assertThat(saved.getBoardCategory(), is(boardCategory));
                assertThat(saved.getPostsViewType(), is(postsViewType));
            }
        }
    }
}
