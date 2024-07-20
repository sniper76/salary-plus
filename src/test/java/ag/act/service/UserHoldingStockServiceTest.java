package ag.act.service;

import ag.act.dto.MySolidarityDto;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.model.Status;
import ag.act.module.solidarity.MySolidarityPageableFactory;
import ag.act.repository.UserHoldingStockRepository;
import ag.act.service.user.UserHoldingStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserHoldingStockServiceTest {
    @InjectMocks
    private UserHoldingStockService service;
    @Mock
    private UserHoldingStockRepository userHoldingStockRepository;
    @Mock
    private MySolidarityPageableFactory mySolidarityPageableFactory;

    private final PageRequest pageable = PageRequest.of(0, 10000);

    @Nested
    class WhenSetActiveStocksDisplayOrderByPurchasedAmountDesc {
        @Mock
        private User user;
        @Mock
        private MySolidarityDto mySolidarityDto1;
        @Mock
        private MySolidarityDto mySolidarityDto2;
        @Mock
        private MySolidarityDto mySolidarityDto3;
        @Mock
        private UserHoldingStock userHoldingStock1;
        @Mock
        private UserHoldingStock userHoldingStock2;
        @Mock
        private UserHoldingStock userHoldingStock3;

        @Mock
        Page<MySolidarityDto> mySolidarityDtoPage;
        @Mock
        private Stock stock;

        @Test
        void shouldSetActiveStocksDisplayOrderByPurchasedAmountDesc() {
            // Given
            final Long userId = somePositiveLong();
            given(user.getId()).willReturn(userId);
            given(userHoldingStockRepository.findSortedMySolidarityList(userId, List.of(Status.ACTIVE), PageRequest.of(0, 10000)))
                .willReturn(mySolidarityDtoPage);
            given(mySolidarityDtoPage.getContent())
                .willReturn(List.of(mySolidarityDto1, mySolidarityDto2, mySolidarityDto3));
            given(mySolidarityDto1.getUserHoldingStock())
                .willReturn(userHoldingStock1);
            given(mySolidarityDto2.getUserHoldingStock())
                .willReturn(userHoldingStock2);
            given(mySolidarityDto3.getUserHoldingStock())
                .willReturn(userHoldingStock3);
            given(userHoldingStock1.getStock()).willReturn(stock);
            given(userHoldingStock2.getStock()).willReturn(stock);
            given(userHoldingStock3.getStock()).willReturn(stock);
            given(userHoldingStock1.getQuantity()).willReturn(1L);
            given(userHoldingStock2.getQuantity()).willReturn(2L);
            given(userHoldingStock3.getQuantity()).willReturn(3L);
            given(stock.getClosingPrice()).willReturn(1);
            given(mySolidarityPageableFactory.getPageable()).willReturn(pageable);

            // When
            service.setActiveStocksDisplayOrderByPurchasedAmountDesc(user);

            // Then
            then(userHoldingStock1).should().setDisplayOrder(2);
            then(userHoldingStock2).should().setDisplayOrder(1);
            then(userHoldingStock3).should().setDisplayOrder(0);
        }
    }

    @Nested
    class GetTotalAssetAmount {

        private Long userId;

        @BeforeEach
        void setUp() {
            userId = someLong();
        }

        @Nested
        class WhenUserHasSomeUserHoldingStocks {

            @Mock
            private UserHoldingStock userHoldingStock1;
            @Mock
            private UserHoldingStock userHoldingStock2;
            @Mock
            private Stock stock1;
            @Mock
            private Stock stock2;
            private long expectedTotalAssetAmount;

            @BeforeEach
            void setUp() {
                final Long quantity1 = someLongBetween(10L, 100L);
                final Long quantity2 = someLongBetween(10L, 100L);
                final Integer closingPrice1 = someIntegerBetween(1000, 10000);
                final Integer closingPrice2 = someIntegerBetween(1000, 10000);

                expectedTotalAssetAmount = (quantity1 * closingPrice1) + (quantity2 * closingPrice2);

                given(userHoldingStock1.getQuantity()).willReturn(quantity1);
                given(userHoldingStock2.getQuantity()).willReturn(quantity2);
                given(userHoldingStock1.getStock()).willReturn(stock1);
                given(userHoldingStock2.getStock()).willReturn(stock2);
                given(stock1.getClosingPrice()).willReturn(closingPrice1);
                given(stock2.getClosingPrice()).willReturn(closingPrice2);
                given(userHoldingStockRepository.findAllByUserIdAndStatus(userId, Status.ACTIVE))
                    .willReturn(List.of(userHoldingStock1, userHoldingStock2));
            }

            @Test
            void shouldReturnTotalAssetAmount() {
                final Long actual = service.getTotalAssetAmount(userId);

                assertThat(actual, is(expectedTotalAssetAmount));
            }
        }

        @Nested
        class WhenUserDoesNotHaveAnyHoldingStocks {
            @BeforeEach
            void setUp() {
                given(userHoldingStockRepository.findAllByUserIdAndStatus(userId, Status.ACTIVE)).willReturn(List.of());
            }

            @Test
            void shouldReturnZero() {
                final Long actual = service.getTotalAssetAmount(userId);

                assertThat(actual, is(0L));
            }
        }
    }
}
