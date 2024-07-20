package ag.act.module.core.configuration;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.core.configuration.DevServerFeatureFlagUtil;
import ag.act.enums.BoardCategory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DevServerFeatureFlagUtilIntegrationTest extends AbstractCommonIntegrationTest {

    @AfterEach
    void tearDown() {
        Mockito.reset(serverEnvironment);
    }

    @Nested
    class WhenServerEnvironmentIsProd {

        @BeforeEach
        void setUp() {
            mockServerEnvironmentIsProd(Boolean.TRUE);
        }

        @Nested
        class BoardCategoryIsFeatureBoardCategory {
            @Test
            void shouldReturnFalseInProduction() {
                assertThat(DevServerFeatureFlagUtil.isDisplayableInApp(BoardCategory.TEST_FOR_DEV), is(Boolean.FALSE));
                assertThat(BoardCategory.TEST_FOR_DEV.isDisplayableInApp(), is(Boolean.FALSE));
            }
        }

        @Nested
        class BoardCategoryIsNotFeatureBoardCategory {
            @Test
            void shouldReturnFalseInProduction() {
                assertThat(DevServerFeatureFlagUtil.isDisplayableInApp(BoardCategory.CO_HOLDING_ARRANGEMENTS), is(Boolean.TRUE));
            }
        }
    }

    @Nested
    class WhenServerEnvironmentIsNotProd {

        @BeforeEach
        void setUp() {
            mockServerEnvironmentIsProd(Boolean.FALSE);
        }

        @Nested
        class BoardCategoryIsFeatureBoardCategory {
            @Test
            void shouldReturnTrueNotInProduction() {
                assertThat(DevServerFeatureFlagUtil.isDisplayableInApp(BoardCategory.TEST_FOR_DEV), is(Boolean.TRUE));
                assertThat(BoardCategory.TEST_FOR_DEV.isDisplayableInApp(), is(Boolean.TRUE));
            }
        }

        @Nested
        class BoardCategoryIsNotFeatureBoardCategory {
            @Test
            void shouldReturnTrueNotInProduction() {
                assertThat(DevServerFeatureFlagUtil.isDisplayableInApp(BoardCategory.CAMPAIGN), is(Boolean.TRUE));
            }
        }
    }
}
