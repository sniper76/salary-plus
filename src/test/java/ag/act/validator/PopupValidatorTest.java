package ag.act.validator;

import ag.act.enums.AppLinkType;
import ag.act.enums.popup.PopupDisplayTargetType;
import ag.act.enums.push.PushTargetType;
import ag.act.exception.BadRequestException;
import ag.act.model.PopupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static ag.act.TestUtil.assertException;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class PopupValidatorTest {
    @InjectMocks
    private PopupValidator validator;
    @Mock
    private LinkTypeValidator linkTypeValidator;
    @Mock
    private DefaultRequestValidator defaultRequestValidator;

    @Nested
    class Validate {
        private PopupRequest popupRequest;

        @BeforeEach
        void setUp() {
            popupRequest = new PopupRequest();
        }

        @Nested
        class Update {
            private final Instant now = Instant.now();

            @BeforeEach
            void setUp() {
                final Instant startDatetime = now.plus(1, ChronoUnit.DAYS);
                final Instant endDatetime = now.plus(3, ChronoUnit.DAYS);

                popupRequest = popupRequest
                    .content(someAlphanumericString(15))
                    .title(someString(5))
                    .postId(somePositiveLong())
                    .linkTitle(someString(10))
                    .linkType(someEnum(AppLinkType.class).name())
                    .stockGroupId(somePositiveLong())
                    .stockCode(someString(6))
                    .stockTargetType(someEnum(PushTargetType.class).name())
                    .displayTargetType(someEnum(PopupDisplayTargetType.class).name())
                    .targetStartDatetime(startDatetime)
                    .targetEndDatetime(endDatetime);
            }

            @Nested
            class AndNothingChanged {

                @Test
                void shouldNotThrowException() {
                    validator.validateUpdate(popupRequest);
                }
            }

            @Nested
            class AndWhenTargetEndDatetime {

                @Nested
                class AndIsBeforeStartDatetime {
                    @BeforeEach
                    void setUp() {
                        final Instant now = Instant.now();
                        final Instant plus = now.plus(10, ChronoUnit.SECONDS);
                        popupRequest = popupRequest.targetStartDatetime(plus)
                            .targetEndDatetime(now);
                    }

                    @Test
                    void shouldThrowException() {
                        assertException(
                            BadRequestException.class,
                            () -> validator.validateUpdate(popupRequest),
                            "팝업 게시 종료일이 시작일보다 이전일 수 없습니다."
                        );
                    }
                }
            }
        }
    }
}
