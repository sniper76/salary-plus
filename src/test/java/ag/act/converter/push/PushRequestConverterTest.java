package ag.act.converter.push;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.enums.AppLinkType;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
import ag.act.enums.push.PushTopic;
import ag.act.util.AppLinkUrlManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static ag.act.TestUtil.someInstantInTheFuture;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class PushRequestConverterTest {

    @InjectMocks
    private PushRequestConverter converter;
    @Mock
    private AppLinkUrlManager appLinkUrlManager;

    @Nested
    class ConvertCreatePushRequest {
        @Mock
        private ag.act.model.CreatePushRequest createPushRequest;
        @Mock
        private Post post;
        private Push actualPush;
        private String linkUrl;

        @BeforeEach
        void setUp() {
            linkUrl = someString(10);

            given(createPushRequest.getTitle()).willReturn(someString(5));
            given(createPushRequest.getContent()).willReturn(someString(5));
            given(createPushRequest.getSendType()).willReturn(someEnum(PushSendType.class).name());
            given(createPushRequest.getTargetDatetime()).willReturn(someInstantInTheFuture());
            given(createPushRequest.getStockTargetType()).willReturn(someEnum(PushTargetType.class).name());
            given(createPushRequest.getStockCode()).willReturn(someStockCode());
            given(createPushRequest.getLinkType()).willReturn(someEnum(AppLinkType.class).name());
            given(createPushRequest.getPostId()).willReturn(someLong());
            given(appLinkUrlManager.getLinkToSaveByLinkType(any(Push.class), eq(post))).willReturn(linkUrl);
        }

        @Nested
        class WhenPushSendTypeScheduledPush {

            @BeforeEach
            void setUp() {
                given(createPushRequest.getSendType()).willReturn(PushSendType.SCHEDULE.name());
            }

            @Nested
            class AndTargetIsAll extends DefaultTestCases {

                @BeforeEach
                void setUp() {
                    given(createPushRequest.getStockTargetType()).willReturn(PushTargetType.ALL.name());

                    actualPush = converter.convert(createPushRequest, post);
                }

                @Test
                void shouldSetTopicNotice() {
                    assertThat(actualPush.getTopic(), is(PushTopic.NOTICE.name()));
                }

                @Test
                void shouldSetTargetDatetime() {
                    assertThat(actualPush.getTargetDatetime(), is(DateTimeConverter.convert(createPushRequest.getTargetDatetime())));
                }
            }

            @Nested
            class AndTargetIsStock extends DefaultTestCases {

                private String stockCode;

                @BeforeEach
                void setUp() {
                    stockCode = someStockCode();

                    given(createPushRequest.getStockTargetType()).willReturn(PushTargetType.STOCK.name());
                    given(createPushRequest.getStockCode()).willReturn(stockCode);

                    actualPush = converter.convert(createPushRequest, post);
                }

                @Test
                void shouldSetTopicNotice() {
                    assertThat(actualPush.getTopic(), nullValue());
                }

                @Test
                void shouldSetStockCode() {
                    assertThat(actualPush.getStockCode(), is(stockCode));
                }

                @Test
                void shouldSetTargetDatetime() {
                    assertThat(actualPush.getTargetDatetime(), is(DateTimeConverter.convert(createPushRequest.getTargetDatetime())));
                }
            }

            @Nested
            class AndTargetIsStockGroup extends DefaultTestCases {

                private Long stockGroupId;

                @BeforeEach
                void setUp() {
                    stockGroupId = someLong();

                    given(createPushRequest.getStockTargetType()).willReturn(PushTargetType.STOCK_GROUP.name());
                    given(createPushRequest.getStockGroupId()).willReturn(stockGroupId);

                    actualPush = converter.convert(createPushRequest, post);
                }

                @Test
                void shouldSetTopicNotice() {
                    assertThat(actualPush.getTopic(), nullValue());
                }

                @Test
                void shouldSetStockCode() {
                    assertThat(actualPush.getStockGroupId(), is(stockGroupId));
                }

                @Test
                void shouldSetTargetDatetime() {
                    assertThat(actualPush.getTargetDatetime(), is(DateTimeConverter.convert(createPushRequest.getTargetDatetime())));
                }
            }

        }

        @Nested
        class WhenPushSendTypeImmediatelyPush extends DefaultTestCases {

            @BeforeEach
            void setUp() {
                given(createPushRequest.getSendType()).willReturn(PushSendType.IMMEDIATELY.name());

                actualPush = converter.convert(createPushRequest, post);
            }

            @Test
            void shouldSetCurrentLocalDateTime() {
                assertThat(actualPush.getTargetDatetime(), notNullValue());
            }
        }


        @SuppressWarnings("unused")
        class DefaultTestCases {

            @Test
            void shouldSetTitle() {
                assertThat(actualPush.getTitle(), is(createPushRequest.getTitle().trim()));
            }

            @Test
            void shouldSetContent() {
                assertThat(actualPush.getContent(), is(createPushRequest.getContent().trim()));
            }

            @Test
            void shouldSetSendType() {
                assertThat(actualPush.getSendType().name(), is(createPushRequest.getSendType()));
            }

            @Test
            void shouldSetSendStatus() {
                assertThat(actualPush.getSendStatus(), is(PushSendStatus.READY));
            }

            @Test
            void shouldSetLinkType() {
                assertThat(actualPush.getLinkType().name(), is(createPushRequest.getLinkType()));
            }

            @Test
            void shouldSetLinkUrl() {
                assertThat(actualPush.getLinkUrl(), is(linkUrl));
            }
        }
    }
}
