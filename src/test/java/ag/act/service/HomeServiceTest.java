package ag.act.service;

import ag.act.dto.MySolidarityDto;
import ag.act.entity.Solidarity;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.exception.BadRequestException;
import ag.act.model.Status;
import ag.act.module.solidarity.MySolidarityPageableFactory;
import ag.act.service.user.UserHoldingStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static ag.act.TestUtil.assertException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@SuppressWarnings({"AbbreviationAsWordInName", "checkstyle:MemberName"})
@MockitoSettings(strictness = Strictness.LENIENT)
class HomeServiceTest {
    @InjectMocks
    private HomeService service;
    @Mock
    private UserHoldingStockService userHoldingStockService;
    @Mock
    private User user;
    private Long userId;
    @Mock
    private UserHoldingStock userHoldingStock1;
    @Mock
    private UserHoldingStock userHoldingStock2;
    @Mock
    private MySolidarityDto mySolidarityDto1;
    @Mock
    private MySolidarityDto mySolidarityDto2;
    @Mock
    private Solidarity solidarity1;
    @Mock
    private Solidarity solidarity2;
    @Mock
    private MySolidarityPageableFactory mySolidarityPageableFactory;

    private final PageRequest pageable = PageRequest.of(0, 10000);

    @BeforeEach
    void setUp() {
        userId = somePositiveLong();

        given(user.getId())
            .willReturn(userId);
    }

    @Nested
    class WhenUpdateUserHoldingStocks {

        @BeforeEach
        void setUp() {
            given(userHoldingStockService.getAllActiveSortedMySolidarityList(userId, PageRequest.of(0, 10000)))
                .willReturn(List.of(mySolidarityDto1, mySolidarityDto2));
            given(mySolidarityDto1.getUserHoldingStock())
                .willReturn(userHoldingStock1);
            given(mySolidarityDto2.getUserHoldingStock())
                .willReturn(userHoldingStock2);
            given(userHoldingStock1.getStockCode())
                .willReturn("000001");
            given(userHoldingStock2.getStockCode())
                .willReturn("000002");
            given(mySolidarityDto1.getSolidarity())
                .willReturn(solidarity1);
            given(mySolidarityDto2.getSolidarity())
                .willReturn(solidarity2);
            given(solidarity1.getStatus())
                .willReturn(Status.ACTIVE);
            given(solidarity2.getStatus())
                .willReturn(Status.ACTIVE);
            willDoNothing().given(userHoldingStock1).setDisplayOrder(any());
            willDoNothing().given(userHoldingStock2).setDisplayOrder(any());
            given(mySolidarityPageableFactory.getPageable()).willReturn(pageable);
        }

        @Test
        void shouldUpdateUserHoldingStocksDisplayOrder() {

            // When
            service.updateUserHoldingStocks(user, List.of("000001", "000002"), pageable);

            // Then
            then(userHoldingStock1).should().setDisplayOrder(0);
            then(userHoldingStock2).should().setDisplayOrder(1);
            then(userHoldingStockService).should().getAllActiveSortedMySolidarityList(userId, PageRequest.of(0, 10000));
            then(userHoldingStockService).should().getAllSortedMySolidarityList(userId, PageRequest.of(0, 10000));
        }

        @Nested
        class AndNotMyStockIncluded {

            @Test
            void shouldThrowBadRequestException() {

                // When
                assertException(
                    BadRequestException.class,
                    () -> service.updateUserHoldingStocks(
                        user,
                        List.of(someThing("000001", "000002"), "000003"),
                        pageable
                    ),
                    "처리할 수 없는 종목 코드가 존재합니다. (000003)"
                );

                // Then
                then(userHoldingStock1).should(never()).setDisplayOrder(any());
            }
        }

        @Nested
        class AndNotAllStocksIncluded {

            @Test
            void shouldThrowBadRequestNotEnoughStockCodeException() {

                // When
                assertException(
                    BadRequestException.class,
                    () -> service.updateUserHoldingStocks(
                        user,
                        List.of("000001"),
                        pageable
                    ),
                    "활성화된 모든 종목의 코드를 입력해주세요."
                );

                // Then
                then(userHoldingStock1).should(never()).setDisplayOrder(any());
            }
        }

        @Nested
        class AndDuplicatedStocksIncluded {

            @Test
            void shouldThrowBadRequestDuplicatedStockCodeException() {

                // When
                assertException(
                    BadRequestException.class,
                    () -> service.updateUserHoldingStocks(
                        user,
                        List.of("000001", "000001"),
                        pageable
                    ),
                    "중복된 종목 코드가 존재합니다."
                );

                // Then
                then(userHoldingStock1).should(never()).setDisplayOrder(any());
            }
        }
    }
}
