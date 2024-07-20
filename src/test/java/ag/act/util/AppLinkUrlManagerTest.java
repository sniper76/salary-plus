package ag.act.util;

import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.enums.AppLinkType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
public class AppLinkUrlManagerTest {
    @InjectMocks
    private AppLinkUrlManager manager;

    @Mock
    private AppLinkUrlGenerator appLinkUrlGenerator;

    private static final String NOTIFICATION_URL = "/notification";
    private static final String NEWS_HOME_URL = "/globalboard";
    private static final String MAIN_HOME_URL = "/main";

    @Nested
    class TestGetPushLinkByLinkType {
        @Mock
        private Push push;
        @Mock
        private Post post;

        @Nested
        class WhenLinkTypeNone {
            @Test
            void shouldReturnNull() {
                // Given
                given(push.getLinkType()).willReturn(AppLinkType.NONE);

                // When
                String linkUrl = manager.getLinkToSaveByLinkType(push, post);

                // Then
                assertThat(linkUrl, nullValue());
            }
        }

        @Nested
        class WhenLinkTypeLink {
            @Test
            void shouldEqualExpected() {
                // Given
                String expected = someString(10);
                given(push.getLinkType()).willReturn(AppLinkType.LINK);
                given(appLinkUrlGenerator.generateBoardGroupPostLinkUrl(post)).willReturn(expected);

                // When
                String actual = manager.getLinkToSaveByLinkType(push, post);

                // Then
                assertThat(actual, is(expected));
            }
        }

        @Nested
        class WhenLinkTypeStockHome {
            @Test
            void shouldEqualStockHome() {
                // Given
                String stockCode = someAlphanumericString(10);
                String expected = someString(10);

                given(push.getLinkType()).willReturn(AppLinkType.STOCK_HOME);
                given(push.getStockCode()).willReturn(stockCode);
                given(appLinkUrlGenerator.generateStockHomeLinkUrl(stockCode)).willReturn(expected);

                // When
                String actual = manager.getLinkToSaveByLinkType(push, post);

                // Then
                assertThat(actual, is(expected));
            }
        }

        @Nested
        class WhenLinkTypeNewsHome {
            @Test
            void shouldEqualNewsHome() {
                // Given
                given(push.getLinkType()).willReturn(AppLinkType.NEWS_HOME);

                // When
                String linkUrl = manager.getLinkToSaveByLinkType(push, post);

                // Then
                assertThat(linkUrl, is(NEWS_HOME_URL));
            }
        }

        @Nested
        class WhenLinkTypeMainHome {
            @Test
            void shouldReturnMainHome() {
                // Given
                given(push.getLinkType()).willReturn(AppLinkType.MAIN_HOME);

                // When
                String linkUrl = manager.getLinkToSaveByLinkType(push, post);

                // Then
                assertThat(linkUrl, is(MAIN_HOME_URL));
            }
        }

        @Nested
        class WhenLinkTypeNotification {
            @Test
            void shouldReturnNotification() {
                // Given
                given(push.getLinkType()).willReturn(AppLinkType.NOTIFICATION);

                // When
                String linkUrl = manager.getLinkToSaveByLinkType(push, post);

                // Then
                assertThat(linkUrl, is(NOTIFICATION_URL));
            }
        }
    }
}
