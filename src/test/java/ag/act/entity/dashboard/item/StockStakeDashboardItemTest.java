package ag.act.entity.dashboard.item;

import ag.act.model.DashboardItemResponseVariation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockStakeDashboardItemTest {
    private StockStakeDashboardItem item;
    private DashboardItemResponseVariation variationResponse;

    @Nested
    class WhenVariationIsZero extends DefaultTestCases {
        @BeforeEach
        void setUp() {
            item = new StockStakeDashboardItem(13.335, 13.335);
            variationResponse = item.getVariationResponse();
        }

        @Test
        void shouldVariationTextColorBeBlackHexCode() {
            assertThat(variationResponse.getColor(), is("#000000"));
        }

        @Test
        void shouldVariationTextBeZero() {
            assertThat(variationResponse.getText(), is("-"));
        }
    }

    @Nested
    class WhenVariationIsNegative {

        @Nested
        class AndDifferenceBiggerThanTwoDecimalPlaces extends DefaultTestCases {
            @BeforeEach
            void setUp() {
                item = new StockStakeDashboardItem(13.335, 13.345);
                variationResponse = item.getVariationResponse();
            }

            @Test
            void shouldVariationTextColorBeBlueHexCode() {
                assertThat(variationResponse.getColor(), is("#355CE9"));
            }

            @Test
            void shouldVariationTextWithNegativePrefix() {
                assertThat(variationResponse.getText(), is("▼ 0.01%"));
            }
        }

        @Nested
        class AndDifferenceSmallerThanTwoDecimalPlaces extends DefaultTestCases {
            @BeforeEach
            void setUp() {
                item = new StockStakeDashboardItem(13.335_335, 13.335_345);
                variationResponse = item.getVariationResponse();
            }

            @Test
            void shouldVariationTextColorBeBlueHexCode() {
                assertThat(variationResponse.getColor(), is("#355CE9"));
            }

            @Test
            void shouldVariationTextWithNegativePrefix() {
                assertThat(variationResponse.getText(), is("▼ 0.00%"));
            }
        }
    }

    @Nested
    class WhenVariationIsPositive  {

        @Nested
        class AndDifferenceBiggerThanTwoDecimalPlaces extends DefaultTestCases {
            @BeforeEach
            void setUp() {
                item = new StockStakeDashboardItem(13.335, 13.325);
                variationResponse = item.getVariationResponse();
            }

            @Test
            void shouldVariationTextColorBeBlueHexCode() {
                assertThat(variationResponse.getColor(), is("#FF0000"));
            }

            @Test
            void shouldVariationTextWithNegativePrefix() {
                assertThat(variationResponse.getText(), is("▲ 0.01%"));
            }
        }

        @Nested
        class AndDifferenceSmallerThanTwoDecimalPlaces extends DefaultTestCases {
            @BeforeEach
            void setUp() {
                item = new StockStakeDashboardItem(13.335_335, 13.335_325);
                variationResponse = item.getVariationResponse();
            }

            @Test
            void shouldVariationTextColorBeBlueHexCode() {
                assertThat(variationResponse.getColor(), is("#FF0000"));
            }

            @Test
            void shouldVariationTextWithNegativePrefix() {
                assertThat(variationResponse.getText(), is("▲ 0.00%"));
            }
        }
    }

    @SuppressWarnings("unused")
    class DefaultTestCases {
        @Test
        void shouldReturnTitle() {
            assertThat(item.getTitle(), is("지분율"));
        }

        @Test
        void shouldReturnUnit() {
            assertThat(item.getUnit(), is("%"));
        }

        @Test
        void shouldOriginValueTextWithUnitFormatted() {
            assertThat(item.getCurrentValueTextWithUnit(), is("13.34%"));
        }
    }
}

