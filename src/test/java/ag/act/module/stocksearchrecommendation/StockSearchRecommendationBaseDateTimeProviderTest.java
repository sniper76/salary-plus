package ag.act.module.stocksearchrecommendation;

import ag.act.entity.admin.StockRanking;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockSearchRecommendationBaseDateTimeProviderTest {

    @InjectMocks
    private StockSearchRecommendationBaseDateTimeProvider provider;
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
    private LocalDate localDate;
    private ZonedDateTime zonedDateTime;

    private List<MockedStatic<?>> statics;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        localDate = LocalDate.now();
        zonedDateTime = ZonedDateTime.now();

        statics = List.of(mockStatic(KoreanDateTimeUtil.class));
    }

    @Nested
    class GetBaseDateTimeForSearchTrend {

        private String expectedBaseDateTime;

        @Nested
        class WithRandomDateTime {

            @BeforeEach
            void setUp() {
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                given(KoreanDateTimeUtil.getNowInKoreanTime()).willReturn(zonedDateTime);

                expectedBaseDateTime = "%s 기준".formatted(formatter.format(zonedDateTime));
            }

            @Test
            void shouldReturnFormattedBaseDateTimeForSearchTrend() {
                // When
                final String actualBaseDateTime = provider.getBaseDateTimeForSearchTrend();

                // Then
                assertThat(actualBaseDateTime, is(expectedBaseDateTime));
            }
        }

        @Nested
        class WithSpecificDateTime {

            @ParameterizedTest(name = "{index} => zonedDateTime=''{0}'', expectedBaseDateTime=''{1}''")
            @MethodSource("valueProvider")
            void shouldReturnFormattedBaseDateTimeForSearchTrend(ZonedDateTime zonedDateTime, String expectedBaseDateTime) {
                // Given
                given(KoreanDateTimeUtil.getNowInKoreanTime()).willReturn(zonedDateTime);

                // When
                final String actualBaseDateTime = provider.getBaseDateTimeForSearchTrend();

                // Then
                assertThat(actualBaseDateTime, is(expectedBaseDateTime));
            }

            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of(ZonedDateTime.of(2024, 10, 10, 23, 23, 23, 0, DEFAULT_ZONE), "2024-10-10 23:23 기준"),
                    Arguments.of(ZonedDateTime.of(2000, 10, 10, 23, 23, 23, 0, DEFAULT_ZONE), "2000-10-10 23:23 기준"),
                    Arguments.of(ZonedDateTime.of(2024, 10, 1, 23, 23, 23, 0, DEFAULT_ZONE), "2024-10-01 23:23 기준"),
                    Arguments.of(ZonedDateTime.of(2024, 10, 10, 23, 59, 59, 0, DEFAULT_ZONE), "2024-10-10 23:59 기준"),
                    Arguments.of(ZonedDateTime.of(2020, 10, 10, 23, 23, 23, 0, DEFAULT_ZONE), "2020-10-10 23:23 기준")
                );
            }
        }
    }

    @Nested
    class GetBaseDateTimeForStockRanking {

        private String expectedBaseDateTime;
        private List<StockRanking> stockRankings;

        @Nested
        class WhenStockRankingsAreNotEmpty {
            @Mock
            private StockRanking stockRanking;

            @BeforeEach
            void setUp() {
                stockRankings = List.of(stockRanking);
                stockRanking.setDate(localDate);
            }

            @Nested
            class WithRandomDateTime {

                @BeforeEach
                void setUp() {
                    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    given(KoreanDateTimeUtil.getYesterdayLocalDate()).willReturn(localDate);

                    expectedBaseDateTime = "%s 00:00 기준".formatted(formatter.format(localDate.plusDays(1)));
                }

                @Test
                void shouldReturnFormattedBaseDateTimeForStockRanking() {
                    // When
                    final String actualBaseDateTime = provider.getBaseDateTimeForStockRanking(stockRankings);

                    // Then
                    assertThat(actualBaseDateTime, is(expectedBaseDateTime));
                }
            }
        }

        @Nested
        class WhenStockRankingsIsEmpty {

            @BeforeEach
            void setUp() {
                stockRankings = List.of();
            }

            @Nested
            class WithRandomDateTime {

                @BeforeEach
                void setUp() {
                    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    given(KoreanDateTimeUtil.getYesterdayLocalDate()).willReturn(localDate);

                    expectedBaseDateTime = "%s 00:00 기준".formatted(formatter.format(localDate.plusDays(1)));
                }

                @Test
                void shouldReturnFormattedBaseDateTimeForStockRanking() {
                    // When
                    final String actualBaseDateTime = provider.getBaseDateTimeForStockRanking(stockRankings);

                    // Then
                    assertThat(actualBaseDateTime, is(expectedBaseDateTime));
                }
            }

            @Nested
            class WithSpecificDateTime {

                @ParameterizedTest(name = "{index} => localDate=''{0}'', expectedBaseDateTime=''{1}''")
                @MethodSource("valueProvider")
                void shouldReturnFormattedBaseDateTimeForStockRanking(LocalDate localDate, String expectedBaseDateTime) {
                    // Given
                    given(KoreanDateTimeUtil.getYesterdayLocalDate()).willReturn(localDate);

                    // When
                    final String actualBaseDateTime = provider.getBaseDateTimeForStockRanking(stockRankings);

                    // Then
                    assertThat(actualBaseDateTime, is(expectedBaseDateTime));
                }

                private static Stream<Arguments> valueProvider() {
                    return Stream.of(
                        Arguments.of(LocalDate.of(2024, 10, 10), "2024-10-11 00:00 기준"),
                        Arguments.of(LocalDate.of(2000, 10, 10), "2000-10-11 00:00 기준"),
                        Arguments.of(LocalDate.of(2024, 10, 1), "2024-10-02 00:00 기준"),
                        Arguments.of(LocalDate.of(2023, 10, 14), "2023-10-15 00:00 기준"),
                        Arguments.of(LocalDate.of(2020, 10, 19), "2020-10-20 00:00 기준")
                    );
                }
            }
        }
    }
}
