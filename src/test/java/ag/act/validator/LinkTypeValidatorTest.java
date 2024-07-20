package ag.act.validator;

import ag.act.dto.admin.LinkDto;
import ag.act.enums.AppLinkType;
import ag.act.enums.push.PushTargetType;
import ag.act.exception.BadRequestException;
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

import java.util.stream.Stream;

import static ag.act.TestUtil.assertException;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class LinkTypeValidatorTest {
    @InjectMocks
    private LinkTypeValidator validator;
    @Mock
    private DefaultRequestValidator defaultRequestValidator;

    @Nested
    class Validate {

        @Nested
        class WhenLink {
            private LinkDto linkDto;

            @BeforeEach
            void setUp() {
                linkDto = new LinkDto(
                    someString(10),
                    somePositiveLong(),
                    AppLinkType.LINK
                );
            }

            @Nested
            class NotNullTitleAndLinkUrl {
                @BeforeEach
                void setUp() {
                    validator.validate(linkDto, someEnum(PushTargetType.class));
                }

                @Test
                void shouldValidateLinkTitle() {
                    then(defaultRequestValidator).should().validateNotNull(linkDto.getLinkTitle(), "링크 제목을 확인하세요.");
                }

                @Test
                void shouldValidateLinkUrl() {
                    then(defaultRequestValidator).should().validateNotNull(linkDto.getPostId(), "링크 대상 게시글 번호를 확인하세요.");
                }
            }
        }

        @Nested
        class WhenLinkTypeNotification {
            private LinkDto linkDto;

            @BeforeEach
            void setUp() {
                linkDto = new LinkDto(
                    someString(10),
                    somePositiveLong(),
                    AppLinkType.NOTIFICATION
                );
            }

            @Test
            void shouldThrowExceptionWhenPostIdNotNull() {
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(linkDto, someEnum(PushTargetType.class)),
                    "링크 타입 이외에는 게시글 번호를 입력할 필요없습니다."
                );
            }
        }

        @Nested
        class WhenLinkTypeNone {
            private LinkDto linkDto;

            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of(""),
                    Arguments.of("   "),
                    Arguments.of(someString(10))
                );
            }

            @BeforeEach
            void setUp() {
                linkDto = new LinkDto(
                    null,
                    null,
                    AppLinkType.NONE
                );
            }

            @ParameterizedTest(name = "{index} => linkTitle=''{0}''")
            @MethodSource("valueProvider")
            void shouldThrowExceptionWhenLinkTitleNotNull(String linkTitle) {
                linkDto.setLinkTitle(linkTitle);

                assertException(
                    BadRequestException.class,
                    () -> validator.validate(linkDto, someEnum(PushTargetType.class)),
                    "링크 타입 없을때 링크 제목과 게시글 번호는 입력할 필요없습니다."
                );
            }

            @Test
            void shouldThrowExceptionWhenLinkUrlNotNull() {
                linkDto.setPostId(somePositiveLong());

                assertException(
                    BadRequestException.class,
                    () -> validator.validate(linkDto, someEnum(PushTargetType.class)),
                    "링크 타입 없을때 링크 제목과 게시글 번호는 입력할 필요없습니다."
                );
            }
        }

        @Nested
        class WhenStockTargetTypeIsStockGroup {
            private final PushTargetType stockTargetType = PushTargetType.STOCK_GROUP;
            private LinkDto linkDto;

            @Nested
            class AndLinkTypeStockHome {

                @BeforeEach
                void setUp() {
                    linkDto = new LinkDto(
                        someString(10),
                        null,
                        AppLinkType.STOCK_HOME
                    );
                }

                @Test
                void shouldThrowException() {

                    assertException(
                        BadRequestException.class,
                        () -> validator.validate(linkDto, stockTargetType),
                        "타켓이 그룹이거나 종목전체 일때 링크(종목홈)을 선택할 수 없습니다."
                    );
                }
            }
        }
    }
}
