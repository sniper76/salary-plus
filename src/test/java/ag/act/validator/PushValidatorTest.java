package ag.act.validator;

import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.enums.AppLinkType;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.service.post.PostService;
import ag.act.service.stock.StockGroupService;
import ag.act.service.stock.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;
import java.util.stream.Stream;

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.someInstantInTheFuture;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;
import static shiver.me.timbers.data.random.RandomTimes.someTimeInThePast;

@MockitoSettings(strictness = Strictness.LENIENT)
class PushValidatorTest {

    @InjectMocks
    private PushValidator validator;
    @Mock
    private StockService stockService;
    @Mock
    private StockGroupService stockGroupService;
    @Mock
    private PostService postService;
    @Mock
    private ag.act.model.CreatePushRequest createPushRequest;

    @Nested
    class ValidateCreatePushRequest {

        @Mock
        private Stock stock;
        @Mock
        private StockGroup stockGroup;
        @Mock
        private Post post;

        @BeforeEach
        void setUp() {
            given(stockService.findByCode(anyString())).willReturn(Optional.of(stock));
            given(stockGroupService.findById(anyLong())).willReturn(Optional.of(stockGroup));
            given(createPushRequest.getTitle()).willReturn(someString(5));
            given(createPushRequest.getContent()).willReturn(someString(5));
            given(createPushRequest.getSendType()).willReturn(someEnum(PushSendType.class).name());
            given(createPushRequest.getStockTargetType()).willReturn(someEnum(PushTargetType.class).name());
            given(createPushRequest.getTargetDatetime()).willReturn(someInstantInTheFuture());
            given(createPushRequest.getStockCode()).willReturn(someStockCode());
            given(createPushRequest.getStockGroupId()).willReturn(someLong());
            given(createPushRequest.getLinkType()).willReturn(someEnum(AppLinkType.class).name());
            given(createPushRequest.getPostId()).willReturn(someLong());
            given(postService.findById(createPushRequest.getPostId())).willReturn(Optional.of(post));
        }

        @Nested
        class WhenStockTargetTypeIsStock {
            @BeforeEach
            void setUp() {
                given(createPushRequest.getStockTargetType()).willReturn(PushTargetType.STOCK.name());
            }

            @Nested
            class AndStockCodeIsMissing {
                @Test
                void shouldThrowBadRequestException() {

                    // Given
                    given(createPushRequest.getStockCode()).willReturn(null);

                    // When // Then
                    assertException(
                        BadRequestException.class,
                        () -> validator.validate(createPushRequest),
                        "종목코드를 확인해주세요."
                    );
                }
            }
        }

        @Nested
        class WhenStockTargetTypeIsStockGroup {
            @BeforeEach
            void setUp() {
                given(createPushRequest.getStockTargetType()).willReturn(PushTargetType.STOCK_GROUP.name());
            }

            @Nested
            class AndStockGroupIdIsMissing {
                @Test
                void shouldThrowBadRequestException() {

                    // Given
                    given(createPushRequest.getStockGroupId()).willReturn(null);

                    // When // Then
                    assertException(
                        BadRequestException.class,
                        () -> validator.validate(createPushRequest),
                        "종목그룹 아이디를 확인해주세요."
                    );
                }
            }
        }

        @Nested
        class WhenTitleIsInvalid {

            @ParameterizedTest(name = "{index} => title=''{0}''")
            @MethodSource("invalidValueProvider")
            void shouldThrowBadRequestException(String title) {

                // Given
                given(createPushRequest.getTitle()).willReturn(title);

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(createPushRequest),
                    "제목을 확인해주세요."
                );
            }

            private static Stream<Arguments> invalidValueProvider() {
                return Stream.of(
                    Arguments.of(""),
                    Arguments.of((String) null),
                    Arguments.of("    ")
                );
            }
        }

        @Nested
        class WhenContentIsInvalid {

            @ParameterizedTest(name = "{index} => content=''{0}''")
            @MethodSource("invalidValueProvider")
            void shouldThrowBadRequestException(String content) {

                // Given
                given(createPushRequest.getContent()).willReturn(content);

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(createPushRequest),
                    "내용을 확인해주세요."
                );
            }

            private static Stream<Arguments> invalidValueProvider() {
                return Stream.of(
                    Arguments.of(""),
                    Arguments.of((String) null),
                    Arguments.of("    ")
                );
            }
        }

        @Nested
        class WhenPushSendTypeIsInvalid {

            @ParameterizedTest(name = "{index} => sendTypeName=''{0}''")
            @MethodSource("invalidValueProvider")
            void shouldThrowBadRequestException(String sendTypeName) {

                // Given
                given(createPushRequest.getSendType()).willReturn(sendTypeName);

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(createPushRequest),
                    "발송타입을 확인해주세요."
                );
            }

            private static Stream<Arguments> invalidValueProvider() {
                return Stream.of(
                    Arguments.of(""),
                    Arguments.of((String) null),
                    Arguments.of("    ")
                );
            }
        }

        @Nested
        class WhenAppLinkTypeIsInvalid {
            @ParameterizedTest(name = "{index} => linkType=''{0}''")
            @MethodSource("invalidValueProvider")
            void shouldThrowBadRequestException(String linkType) {
                // Given
                given(createPushRequest.getLinkType()).willReturn(linkType);

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(createPushRequest),
                    "지원하지 않는 AppLinkType %s 입니다.".formatted(linkType)
                );
            }

            private static Stream<Arguments> invalidValueProvider() {
                return Stream.of(
                    Arguments.of(someString(10)),
                    Arguments.of(""),
                    Arguments.of((String) null),
                    Arguments.of("    ")
                );
            }
        }

        @Nested
        class WhenPushSendTypeIsScheduled {

            @BeforeEach
            void setUp() {
                given(createPushRequest.getSendType()).willReturn(PushSendType.SCHEDULE.name());
            }

            @Nested
            class AndTargetDatetimeIsMissing {

                @Test
                void shouldThrowBadRequestException() {

                    // Given
                    given(createPushRequest.getTargetDatetime()).willReturn(null);

                    // When // Then
                    assertException(
                        BadRequestException.class,
                        () -> validator.validate(createPushRequest),
                        "발송시간을 확인해주세요."
                    );
                }
            }

            @Nested
            class AndTargetDatetimeIsInPast {
                @Test
                void shouldThrowBadRequestException() {

                    // Given
                    given(createPushRequest.getTargetDatetime()).willReturn(someTimeInThePast().toInstant());

                    // When // Then
                    assertException(
                        BadRequestException.class,
                        () -> validator.validate(createPushRequest),
                        "발송시간은 현재시간 이후로 설정 가능합니다."
                    );
                }
            }
        }

        @Nested
        class WhenNotFoundStock {
            @BeforeEach
            void setUp() {
                given(createPushRequest.getStockTargetType()).willReturn(PushTargetType.STOCK.name());
                given(stockService.findByCode(anyString())).willReturn(Optional.empty());
            }

            @Test
            void shouldThrowBadRequestException() {
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(createPushRequest),
                    "종목코드를 확인해주세요."
                );
            }
        }

        @Nested
        class WhenNotFoundPost {
            @Test
            void shouldThrowBadRequestException() {
                // Given
                given(createPushRequest.getLinkType()).willReturn(AppLinkType.LINK.name());
                given(postService.findById(createPushRequest.getPostId())).willReturn(Optional.empty());

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(createPushRequest),
                    "해당 게시글을 찾을 수 없습니다."
                );
            }
        }
    }

    @Nested
    class ValidateForDeleteAndGet {
        @Mock
        private Push push;
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private Optional<Push> pushOptional;
        private Push actualPush;

        @BeforeEach
        void setUp() {
            pushOptional = Optional.of(push);

            given(push.getSendStatus()).willReturn(PushSendStatus.READY);
            given(push.getSendType()).willReturn(PushSendType.SCHEDULE);
        }

        @Nested
        class WhenValidationSuccess {

            @BeforeEach
            void setUp() {
                actualPush = validator.validateForDeleteAndGet(pushOptional);
            }

            @Test
            void shouldReturnPush() {
                assertThat(actualPush, is(push));
            }
        }

        @Nested
        class WhenNotFoundPush {

            @BeforeEach
            void setUp() {
                pushOptional = Optional.empty();
            }

            @Test
            void shouldThrowNotFoundException() {
                assertException(
                    NotFoundException.class,
                    () -> validator.validateForDeleteAndGet(pushOptional),
                    "해당 푸시를 찾을 수 없습니다."
                );
            }
        }

        @Nested
        class WhenPushSendTypeIsImmediately {

            @BeforeEach
            void setUp() {
                given(push.getSendType()).willReturn(PushSendType.IMMEDIATELY);
            }

            @Test
            void shouldThrowBadRequestException() {
                assertException(
                    BadRequestException.class,
                    () -> validator.validateForDeleteAndGet(pushOptional),
                    "발송 예약된 푸시만 삭제 가능합니다."
                );
            }
        }

        @Nested
        class WhenPushSendStatusIsNotReady {

            @BeforeEach
            void setUp() {
                given(push.getSendStatus())
                    .willReturn(someThing(PushSendStatus.notReady().toArray(new PushSendStatus[0])));
            }

            @Test
            void shouldThrowBadRequestException() {
                assertException(
                    BadRequestException.class,
                    () -> validator.validateForDeleteAndGet(pushOptional),
                    "발송대기중인 푸시만 삭제 가능합니다."
                );
            }
        }
    }
}
