package ag.act.aspect;

import ag.act.configuration.security.ActUserProvider;
import ag.act.core.aop.LatestUserPostsViewAspect;
import ag.act.dto.CreateLatestUserPostsViewDto;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.PostsViewType;
import ag.act.service.notification.LatestUserPostsViewService;
import ag.act.service.stock.StockService;
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
import java.util.Optional;

import static ag.act.TestUtil.someBoardCategory;
import static ag.act.TestUtil.someBoardGroupForGlobal;
import static ag.act.TestUtil.someBoardGroupForStock;
import static ag.act.TestUtil.someStockCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

@MockitoSettings(strictness = Strictness.LENIENT)
public class LatestUserPostsViewAspectTest {
    @InjectMocks
    private LatestUserPostsViewAspect latestUserPostsViewAspect;
    @Mock
    private StockService stockService;
    @Mock
    private LatestUserPostsViewService latestUserPostsViewService;
    @Mock
    private Stock stock;
    private String stockCode;
    @Mock
    private User user;
    private List<MockedStatic<?>> statics;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));
        stockCode = someStockCode();

        given(ActUserProvider.get()).willReturn(Optional.of(user));
        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(ActUserProvider.getActUser()).willReturn(user);
        given(stockService.getStock(stockCode)).willReturn(stock);
        willDoNothing().given(latestUserPostsViewService).createOrUpdate(any(CreateLatestUserPostsViewDto.class));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    private void assertCreateOrUpdate(
        BoardGroup boardGroup,
        BoardCategory boardCategory,
        PostsViewType postsViewType
    ) {
        CreateLatestUserPostsViewDto dto = CreateLatestUserPostsViewDto.builder()
            .stock(stock)
            .user(user)
            .boardGroup(boardGroup)
            .boardCategory(boardCategory)
            .postsViewType(postsViewType)
            .build();

        then(latestUserPostsViewService).should().createOrUpdate(dto);
    }

    @Nested
    class WhenUserViewStockHome {

        @Test
        void shouldCreateOrUpdate() {
            // When
            latestUserPostsViewAspect.createLatestUserPostsView(stockCode);

            // Then
            assertCreateOrUpdate(null, null, PostsViewType.STOCK_HOME);
        }
    }

    @Nested
    class WhenUserViewGlobalBoardGroupPosts {
        @Mock
        private GetBoardGroupPostDto getBoardGroupPostDto;
        private BoardGroup globalBoardGroup;

        @BeforeEach
        void setUp() {
            globalBoardGroup = someBoardGroupForGlobal();

            given(getBoardGroupPostDto.getStockCode()).willReturn(stockCode);
            given(getBoardGroupPostDto.getBoardGroup()).willReturn(globalBoardGroup);
        }

        @Nested
        class AndBoardCategoryNotExists {

            @BeforeEach
            void setUp() {
                given(getBoardGroupPostDto.getBoardCategories()).willReturn(null);
            }

            @Test
            void shouldCreateOrUpdate() {

                // When
                latestUserPostsViewAspect.createLatestUserPostsView(getBoardGroupPostDto);

                // Then
                assertCreateOrUpdate(globalBoardGroup, null, PostsViewType.BOARD_GROUP);
            }
        }

        @Nested
        class AndBoardCategoryExists {

            @BeforeEach
            void setUp() {
                given(getBoardGroupPostDto.getBoardCategories()).willReturn(globalBoardGroup.getCategories());
            }

            @Test
            void shouldCreateOrUpdate() {

                // When
                latestUserPostsViewAspect.createLatestUserPostsView(getBoardGroupPostDto);

                // Then
                assertCreateOrUpdate(globalBoardGroup, null, PostsViewType.BOARD_GROUP);
            }
        }
    }

    @Nested
    class WhenUserViewStockBoardGroupPosts {
        @Mock
        private GetBoardGroupPostDto getBoardGroupPostDto;
        private BoardGroup stockBoardGroup;

        @BeforeEach
        void setUp() {
            stockBoardGroup = someBoardGroupForStock();

            given(getBoardGroupPostDto.getStockCode()).willReturn(stockCode);
            given(getBoardGroupPostDto.getBoardGroup()).willReturn(stockBoardGroup);
        }

        @Nested
        class AndBoardCategoryNotExists {

            @BeforeEach
            void setUp() {
                given(getBoardGroupPostDto.getBoardCategories()).willReturn(null);
            }

            @Test
            void shouldCreateOrUpdate() {

                // When
                latestUserPostsViewAspect.createLatestUserPostsView(getBoardGroupPostDto);

                // Then
                assertCreateOrUpdate(stockBoardGroup, null, PostsViewType.BOARD_GROUP);
            }
        }

        @Nested
        class AndBoardCategoryExists {

            @BeforeEach
            void setUp() {
                given(getBoardGroupPostDto.getBoardCategories()).willReturn(List.of(someBoardCategory(stockBoardGroup)));
            }

            @Test
            void shouldNotCreateOrUpdate() {

                // When
                latestUserPostsViewAspect.createLatestUserPostsView(getBoardGroupPostDto);

                // Then
                then(latestUserPostsViewService).should(never()).createOrUpdate(any(CreateLatestUserPostsViewDto.class));
            }
        }
    }
}
