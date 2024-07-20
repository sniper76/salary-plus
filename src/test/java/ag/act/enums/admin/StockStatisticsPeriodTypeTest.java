package ag.act.enums.admin;

import ag.act.TestUtil;
import ag.act.dto.stock.GetStockStatisticsSearchPeriodDto;
import ag.act.exception.BadRequestException;
import ag.act.parser.DateTimeParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockStatisticsPeriodTypeTest {

    private List<MockedStatic<?>> statics;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeParser.class));
    }

    @Nested
    class Daily {

        private final String period = someString(10);

        @Nested
        class PeriodIsInvalid {

            @BeforeEach
            void setUp() {
                given(DateTimeParser.parseDate(period + "01")).willThrow(DateTimeParseException.class);
            }

            @Test
            void shouldTest() {
                TestUtil.assertException(
                    BadRequestException.class,
                    () -> StockStatisticsPeriodType.DAILY.getSearchPeriod(period),
                    "검색할 period 를 'YYYYMM' 형식으로 입력해주세요."
                );
            }
        }

        @Nested
        class PeriodIsValid {

            private final LocalDateTime localDateTime = LocalDateTime.now();
            private GetStockStatisticsSearchPeriodDto actual;
            private LocalDate periodFrom;
            private LocalDate periodTo;

            @BeforeEach
            void setUp() {
                periodFrom = localDateTime.toLocalDate();
                periodTo = periodFrom.plusMonths(1).minusDays(1);
                given(DateTimeParser.parseDate(period + "01")).willReturn(localDateTime);

                actual = StockStatisticsPeriodType.DAILY.getSearchPeriod(period);
            }

            @Test
            void shouldReturnSearchPeriodFrom() {
                assertThat(actual.getFrom(), is(periodFrom));
            }

            @Test
            void shouldReturnSearchPeriodTo() {
                assertThat(actual.getTo(), is(periodTo));
            }
        }

    }

    @Nested
    class Monthly {

        private final String period = someString(10);

        @Nested
        class PeriodIsInvalid {

            @BeforeEach
            void setUp() {
                given(DateTimeParser.parseDate(period + "0101")).willThrow(DateTimeParseException.class);
            }

            @Test
            void shouldTest() {
                TestUtil.assertException(
                    BadRequestException.class,
                    () -> StockStatisticsPeriodType.MONTHLY.getSearchPeriod(period),
                    "검색할 period 를 'YYYY' 형식으로 입력해주세요."
                );
            }
        }

        @Nested
        class PeriodIsValid {

            private final LocalDateTime localDateTime = LocalDateTime.now();
            private GetStockStatisticsSearchPeriodDto actual;
            private LocalDate periodFrom;
            private LocalDate periodTo;

            @BeforeEach
            void setUp() {
                periodFrom = localDateTime.toLocalDate();
                periodTo = periodFrom.plusYears(1).minusDays(1);
                given(DateTimeParser.parseDate(period + "0101")).willReturn(localDateTime);

                actual = StockStatisticsPeriodType.MONTHLY.getSearchPeriod(period);
            }

            @Test
            void shouldReturnSearchPeriodFrom() {
                assertThat(actual.getFrom(), is(periodFrom));
            }

            @Test
            void shouldReturnSearchPeriodTo() {
                assertThat(actual.getTo(), is(periodTo));
            }
        }

    }
}