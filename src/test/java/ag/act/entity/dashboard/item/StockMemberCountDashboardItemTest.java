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
class StockMemberCountDashboardItemTest {
    private StockMemeberCountDashboardItem item;
    private DashboardItemResponseVariation variationResponse;

    @Nested
    class WhenVariationIsZero extends DefaultTestCases {
        @BeforeEach
        void setUp() {
            item = new StockMemeberCountDashboardItem(10_000, 10_000);
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
            item = new StockMemeberCountDashboardItem(10_000, 10_001);
            variationResponse = item.getVariationResponse();
        }

        @Test
        void shouldVariationTextColorBeBlueHexCode() {
            assertThat(variationResponse.getColor(), is("#355CE9"));
        }

        @Test
        void shouldVariationTextWithNegativePrefix() {
            assertThat(variationResponse.getText(), is("▼ 1명"));
        }
    }

    @Nested
    class WhenVariationIsPositive extends DefaultTestCases {
        @BeforeEach
        void setUp() {
            item = new StockMemeberCountDashboardItem(10_000, 9_999);
            variationResponse = item.getVariationResponse();
        }

        @Test
        void shouldVariationTextColorBeBlueHexCode() {
            assertThat(variationResponse.getColor(), is("#FF0000"));
        }

        @Test
        void shouldVariationTextWithNegativePrefix() {
            assertThat(variationResponse.getText(), is("▲ 1명"));
        }
    }


    @SuppressWarnings("unused")
    class DefaultTestCases {
        @Test
        void shouldReturnTitle() {
            assertThat(item.getTitle(), is("주주수"));
        }

        @Test
        void shouldReturnUnit() {
            assertThat(item.getUnit(), is("명"));
        }

        @Test
        void shouldOriginValueTextWithUnitFormatted() {
            assertThat(item.getCurrentValueTextWithUnit(), is("10,000명"));
        }
    }
}

