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
class StockQuantityDashboardItemTest {
    private StockQuantityDashboardItem item;
    private DashboardItemResponseVariation variationResponse;

    @Nested
    class WhenVariationIsZero extends DefaultTestCases {
        @BeforeEach
        void setUp() {
            item = new StockQuantityDashboardItem(10_000_000_000L, 10_000_000_000L);
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
    class WhenVariationIsNegative extends DefaultTestCases {
        @BeforeEach
        void setUp() {
            item = new StockQuantityDashboardItem(10_000_000_000L, 10_000_000_001L);
            variationResponse = item.getVariationResponse();
        }

        @Test
        void shouldVariationTextColorBeBlueHexCode() {
            assertThat(variationResponse.getColor(), is("#355CE9"));
        }

        @Test
        void shouldVariationTextWithNegativePrefix() {
            assertThat(variationResponse.getText(), is("▼ 1주"));
        }
    }

    @Nested
    class WhenVariationIsPositive extends DefaultTestCases {
        @BeforeEach
        void setUp() {
            item = new StockQuantityDashboardItem(10_000_000_000L, 9_999_999_999L);
            variationResponse = item.getVariationResponse();
        }

        @Test
        void shouldVariationTextColorBeBlueHexCode() {
            assertThat(variationResponse.getColor(), is("#FF0000"));
        }

        @Test
        void shouldVariationTextWithNegativePrefix() {
            assertThat(variationResponse.getText(), is("▲ 1주"));
        }
    }


    @SuppressWarnings("unused")
    class DefaultTestCases {
        @Test
        void shouldReturnTitle() {
            assertThat(item.getTitle(), is("주식수"));
        }

        @Test
        void shouldReturnUnit() {
            assertThat(item.getUnit(), is("주"));
        }

        @Test
        void shouldOriginValueTextWithUnitFormatted() {
            assertThat(item.getCurrentValueTextWithUnit(), is("10,000,000,000주"));
        }
    }
}

