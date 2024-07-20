package ag.act.validator;

import ag.act.entity.NicknameHistory;
import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.service.StopWordService;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.util.DateTimeUtil;
import ag.act.validator.user.NicknameValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;

import static ag.act.TestUtil.assertException;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class NicknameValidatorTest {

    private NicknameValidator validator;
    @Mock
    private StopWordService stopWordService;
    @Mock
    private SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    @Mock
    private User user;
    private String nickname;
    private Integer nicknameChangeLimitDays;
    private List<MockedStatic<?>> statics;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));
        nickname = someString(5);
        nicknameChangeLimitDays = someIntegerBetween(30, 90);

        given(user.getNickname()).willReturn(nickname);

        validator = new NicknameValidator(
            nicknameChangeLimitDays,
            stopWordService,
            solidarityLeaderApplicantService
        );
    }

    @Nested
    class WhenValidNicknameProvided {
        @BeforeEach
        void setUp() {
            given(stopWordService.containsStopWord(nickname)).willReturn(false);
        }

        @Test
        void shouldNotThrowAnyException() {
            validator.validateNickname(user);
        }
    }

    @Nested
    class WhenNicknameContainsStopWord {
        @BeforeEach
        void setUp() {
            given(stopWordService.containsStopWord(nickname)).willReturn(true);
        }

        @Test
        void shouldThrowException() {
            assertException(
                BadRequestException.class,
                () -> validator.validateNickname(user),
                "닉네임에 사용할 수 없는 단어입니다."
            );
        }
    }

    @Nested
    class PreviousNicknameChangedWithinCertainDays {
        @Mock
        private NicknameHistory nicknameHistory;

        @BeforeEach
        void setUp() {
            final LocalDateTime updatedAt = LocalDateTime.now().minusDays(nicknameChangeLimitDays + 30);
            given(user.getNicknameHistory()).willReturn(nicknameHistory);
            given(nicknameHistory.getIsFirst()).willReturn(false);
            given(nicknameHistory.getCreatedAt()).willReturn(updatedAt);
            given(DateTimeUtil.isBeforeInDays(updatedAt, nicknameChangeLimitDays)).willReturn(true);
        }

        @Test
        void shouldNotThrowAnyException() {
            validator.validateNicknameWithin90Days(user);
        }
    }

    @Nested
    class PreviousNicknameChangedOverCertainDays {
        @Mock
        NicknameHistory nicknameHistory;

        @BeforeEach
        void setUp() {
            final LocalDateTime updatedAt = LocalDateTime.now().minusDays(nicknameChangeLimitDays - 1);

            given(user.getNicknameHistory()).willReturn(nicknameHistory);
            given(nicknameHistory.getIsFirst()).willReturn(false);
            given(nicknameHistory.getUpdatedAt()).willReturn(updatedAt);
            given(DateTimeUtil.isBeforeInDays(updatedAt, nicknameChangeLimitDays)).willReturn(false);
        }

        @Test
        void shouldThrowBadRequestException() {
            assertException(
                BadRequestException.class,
                () -> validator.validateNicknameWithin90Days(user),
                "닉네임은 %s일에 한번만 변경할 수 있습니다.".formatted(nicknameChangeLimitDays)
            );
        }
    }
}
