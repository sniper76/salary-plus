package ag.act.service;

import ag.act.converter.UnreadPostStatusConverter;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.entity.Stock;
import ag.act.entity.UserHoldingStock;
import ag.act.enums.BoardGroup;
import ag.act.repository.LatestUserPostsViewQueryDslRepository;
import ag.act.service.post.UnreadPostService;
import ag.act.service.stock.StockService;
import ag.act.service.user.UserHoldingStockOnReferenceDateService;
import ag.act.service.user.UserHoldingStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class UnreadPostServiceTest {
    @InjectMocks
    private UnreadPostService unreadPostService;
    @Mock
    private UnreadPostStatusConverter unreadPostStatusConverter;
    @Mock
    private LatestUserPostsViewQueryDslRepository latestUserPostsViewQueryDslRepository;
    @Mock
    private UserHoldingStockService userHoldingStockService;
    @Mock
    private StockService stockService;
    @Mock
    private GlobalBoardManager globalBoardManager;
    @Mock
    private UserHoldingStockOnReferenceDateService userHoldingStockOnReferenceDateService;

    @Nested
    class GetUnreadPostStatus {
        private Long userId;
        private Boolean unreadGlobalBoard;
        private Boolean unreadGlobalCommunity;
        private Boolean unreadDigitalDelegation;
        private Boolean unreadGlobalEvent;
        private String globalBoardStockCode;
        private List<UserHoldingStock> userHoldingStocks;
        @Mock
        private List<Stock> unreadStocks;

        @BeforeEach
        void setUp() {
            userId = someLong();
            unreadGlobalBoard = someBoolean();
            unreadGlobalCommunity = someBoolean();
            unreadDigitalDelegation = someBoolean();
            unreadGlobalEvent = someBoolean();
            globalBoardStockCode = someStockCode();
            UserHoldingStock userHoldingStock = new UserHoldingStock();
            userHoldingStock.setStockCode(someStockCode());
            userHoldingStocks = List.of(userHoldingStock);
        }

        @Test
        void shouldGetUnreadPostStatus() {
            // Given
            given(globalBoardManager.getStockCode()).willReturn(globalBoardStockCode);
            given(userHoldingStockService.findAllByUserIdAndStatusActive(userId))
                .willReturn(userHoldingStocks);
            given(stockService.findAllWithUnreadPosts(userId)).willReturn(unreadStocks);
            given(latestUserPostsViewQueryDslRepository.existsUnreadPost(
                List.of(globalBoardStockCode),
                userId,
                BoardGroup.GLOBALBOARD
            )).willReturn(unreadGlobalBoard);
            given(latestUserPostsViewQueryDslRepository.existsUnreadPost(
                List.of(globalBoardStockCode),
                userId,
                BoardGroup.GLOBALCOMMUNITY
            )).willReturn(unreadGlobalCommunity);
            given(latestUserPostsViewQueryDslRepository.existsUnreadPost(
                List.of(globalBoardStockCode),
                userId,
                BoardGroup.GLOBALEVENT
            )).willReturn(unreadGlobalEvent);
            given(stockService.findAllWithUnreadPosts(userId)).willReturn(unreadStocks);
            given(userHoldingStockOnReferenceDateService.countByPostUserViewDigitalDelegateForReferenceDate(userId))
                .willReturn(unreadDigitalDelegation ? 1L : 0L);

            // When
            unreadPostService.getUnreadPostStatus(userId);

            // Then
            then(latestUserPostsViewQueryDslRepository).should().existsUnreadPost(
                List.of(globalBoardStockCode), userId, BoardGroup.GLOBALBOARD
            );
            then(latestUserPostsViewQueryDslRepository).should().existsUnreadPost(
                List.of(globalBoardStockCode), userId, BoardGroup.GLOBALCOMMUNITY
            );
            then(latestUserPostsViewQueryDslRepository).should().existsUnreadPost(
                List.of(globalBoardStockCode), userId, BoardGroup.GLOBALEVENT
            );
            then(userHoldingStockOnReferenceDateService).should().countByPostUserViewDigitalDelegateForReferenceDate(
                userId
            );
            then(unreadPostStatusConverter).should().convert(
                unreadGlobalBoard, unreadGlobalCommunity, unreadGlobalEvent, unreadDigitalDelegation, unreadStocks
            );
        }
    }

}
