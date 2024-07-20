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
class StockMarketValueDashboardItemTest {
    private StockMarketValueDashboardItem item;
    private DashboardItemResponseVariation variationResponse;

    @Nested
    class WhenVariationIsZero extends DefaultTestCases {
        @BeforeEach
        void setUp() {
            item = new StockMarketValueDashboardItem(1_000_010_000_000L, 1_000_010_000_000L);
            variationResponse = item.getVariationResponse();
        }

        @Test
        void shouldVariationTextColorBeBlackHexCode() {
            assertThat(variationResponse.getColor(), is("#000000"));
        }

        @Test
        void shouldVariationTextBeNoVariation() {
            assertThat(variationResponse.getText(), is("-"));
        }
    }

    @Nested
    class WhenVariationIsNegative {

        @Nested
        class AndDifferenceBiggerThanTenMillions extends DefaultTestCases {
            @BeforeEach
            void setUp() {
                item = new StockMarketValueDashboardItem(1_000_010_000_000L, 1_000_020_000_000L);
                variationResponse = item.getVariationResponse();
            }

            @Test
            void shouldVariationTextColorBeBlueHexCode() {
                assertThat(variationResponse.getColor(), is("#355CE9"));
            }

            @Test
            void shouldVariationTextWithNegativePrefix() {
                assertThat(variationResponse.getText(), is("▼ 0.1억원"));
            }
        }

        @Nested
        class AndDifferenceSmallerThanTenMillions extends DefaultTestCases {
            @BeforeEach
            void setUp() {
                item = new StockMarketValueDashboardItem(1_000_010_000_000L, 1_000_011_000_000L);
                variationResponse = item.getVariationResponse();
            }

            @Test
            void shouldVariationTextColorBeBlueHexCode() {
                assertThat(variationResponse.getColor(), is("#355CE9"));
            }

            @Test
            void shouldVariationTextWithNegativePrefix() {
                assertThat(variationResponse.getText(), is("▼ 0.0억원"));
            }
        }
    }

    @Nested
    class WhenVariationIsPositive {
        @Nested
        class AndDifferenceBiggerThanTenMillions extends DefaultTestCases {
            @BeforeEach
            void setUp() {
                item = new StockMarketValueDashboardItem(1_000_010_000_000L, 1_000_000_000_000L);
                variationResponse = item.getVariationResponse();
            }

            @Test
            void shouldVariationTextColorBeBlueHexCode() {
                assertThat(variationResponse.getColor(), is("#FF0000"));
            }

            @Test
            void shouldVariationTextWithNegativePrefix() {
                assertThat(variationResponse.getText(), is("▲ 0.1억원"));
            }
        }

        @Nested
        class AndDifferenceSmallerThanTenMillions extends DefaultTestCases {
            @BeforeEach
            void setUp() {
                item = new StockMarketValueDashboardItem(1_000_010_000_000L, 1_000_009_000_000L);
                variationResponse = item.getVariationResponse();
            }

            @Test
            void shouldVariationTextColorBeBlueHexCode() {
                assertThat(variationResponse.getColor(), is("#FF0000"));
            }

            @Test
            void shouldVariationTextWithNegativePrefix() {
                assertThat(variationResponse.getText(), is("▲ 0.0억원"));
            }

        }
    }

    @SuppressWarnings("unused")
    class DefaultTestCases {
        @Test
        void shouldReturnTitle() {
            assertThat(item.getTitle(), is("시가액"));
        }

        @Test
        void shouldReturnUnit() {
            assertThat(item.getUnit(), is("억원"));
        }

        @Test
        void shouldOriginValueTextWithUnitFormatted() {
            assertThat(item.getCurrentValueTextWithUnit(), is("10,000.1억원"));
        }
    }
}

