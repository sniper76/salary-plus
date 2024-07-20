package ag.act.service.stock;

import ag.act.entity.StockGroup;
import ag.act.exception.BadRequestException;
import ag.act.repository.StockGroupRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings("checkstyle:LineLength")
@MockitoSettings(strictness = Strictness.LENIENT)
class StockGroupServiceTest {
    @InjectMocks
    private StockGroupService service;
    @Mock
    private StockGroupRepository stockGroupRepository;

    @Nested
    class Save {

        @Mock
        private StockGroup stockGroup;

        @Test
        void shouldThrowBadRequestException() {

            // Given
            given(stockGroupRepository.saveAndFlush(stockGroup)).willThrow(DataIntegrityViolationException.class);

            // When / then
            assertException(
                BadRequestException.class,
                () -> service.save(stockGroup),
                "동일한 종목그룹이 존재합니다."
            );
        }
    }

    @Nested
    class GetMostRelatedTopTenStockGroupsBySearchKeyword {

        @Nested
        class WhenSearchKeywordIsBlank {

            @ParameterizedTest(name = "{index} => searchKeyword=''{0}''")
            @MethodSource("valueProvider")
            void shouldReturnStockGroupListWithoutKeyword(String searchKeyword) {

                // Given
                final List<StockGroup> expected = List.of();
                given(stockGroupRepository.findTop10ByStatusInOrderByNameAsc(List.of(ag.act.model.Status.ACTIVE))).willReturn(expected);

                // When
                final List<StockGroup> actual = service.getMostRelatedTopTenStockGroupsBySearchKeyword(searchKeyword);

                // Then
                assertThat(actual, is(expected));
            }

            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of(""),
                    Arguments.of("  "),
                    Arguments.of("       "),
                    Arguments.of((String) null)
                );
            }
        }

        @Nested
        class WhenSearchKeywordIsNotEmpty {

            @Test
            void shouldReturnStockGroupListWithKeyword() {

                // Given
                final String searchKeyword = someString(5);

                final List<StockGroup> expected = List.of();
                given(stockGroupRepository.findTop10ByNameContainingAndStatusInOrderByNameAsc(searchKeyword, List.of(ag.act.model.Status.ACTIVE))).willReturn(expected);

                // When
                final List<StockGroup> actual = service.getMostRelatedTopTenStockGroupsBySearchKeyword(searchKeyword);

                // Then
                assertThat(actual, is(expected));
            }
        }

    }
}