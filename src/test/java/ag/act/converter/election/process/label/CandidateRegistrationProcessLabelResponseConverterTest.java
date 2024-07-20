package ag.act.converter.election.process.label;

import ag.act.entity.solidarity.election.LeaderElectionCurrentDateTimeProvider;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.model.LeaderElectionProcessLabelResponse;
import ag.act.util.KoreanDateTimeUtil;
import ag.act.util.ZoneIdUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@MockitoSettings(strictness = Strictness.LENIENT)
class CandidateRegistrationProcessLabelResponseConverterTest {
    private static final int DEFAULT_YEAR = 2024;
    @InjectMocks
    private CandidateRegistrationProcessLabelResponseConverter converter;
    private List<MockedStatic<?>> statics;

    @Mock
    private SolidarityLeaderElection leaderElection;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(KoreanDateTimeUtil.class), mockStatic(LeaderElectionCurrentDateTimeProvider.class));
    }


    private static Stream<Arguments> valueProvider() {
        return Stream.of(
            Arguments.of(
                "오늘 마감",
                koreanEndDateTime(Month.JUNE, 5, 0),
                koreanCurrentDateTime(Month.JUNE, 4, 10, 30)
            ),
            Arguments.of(
                "오늘 마감",
                koreanEndDateTime(Month.JUNE, 6, 0),
                koreanCurrentDateTime(Month.JUNE, 5, 22, 0)
            ),
            Arguments.of(
                "4일 남음",
                koreanEndDateTime(Month.JUNE, 9, 0),
                koreanCurrentDateTime(Month.JUNE, 5, 14, 15)
            ),
            Arguments.of(
                "6일 남음",
                koreanEndDateTime(Month.JUNE, 11, 0),
                koreanCurrentDateTime(Month.JUNE, 5, 1, 30)
            ),
            Arguments.of(
                "7일 남음",
                koreanEndDateTime(Month.JUNE, 11, 0),
                koreanCurrentDateTime(Month.JUNE, 4, 19, 30)
            ),
            Arguments.of(
                "8일 남음",
                koreanEndDateTime(Month.JUNE, 11, 2),
                koreanCurrentDateTime(Month.JUNE, 4, 19, 30)
            ),
            Arguments.of(
                "9일 남음",
                koreanEndDateTime(Month.JUNE, 11, 5),
                koreanCurrentDateTime(Month.JUNE, 3, 0, 0)
            )
        );
    }

    public static ZonedDateTime koreanEndDateTime(Month month, int dayOfMonth, int hour) {
        return koreanZonedDateTime(month, dayOfMonth, hour, 0);
    }

    public static ZonedDateTime koreanCurrentDateTime(Month month, int dayOfMonth, int hour, int minute) {
        return koreanZonedDateTime(month, dayOfMonth, hour, minute);
    }

    @NotNull
    private static ZonedDateTime koreanZonedDateTime(Month month, int dayOfMonth, int hour, int minute) {
        return ZonedDateTime.of(DEFAULT_YEAR, month.getValue(), dayOfMonth, hour, minute, 0, 0, ZoneIdUtil.getSeoulZoneId());
    }

    @ParameterizedTest(name = "{index} => expectedTitle=''{0}'', endDateTime=''{1}'', currentDateTime=''{2}''")
    @MethodSource("valueProvider")
    void shouldReturnLabelResponse(String expectedTitle, ZonedDateTime endDateTimeInKoreanTime, ZonedDateTime koreanLocalDateTime) {

        final LocalDateTime endDateTime = endDateTimeInKoreanTime.withZoneSameInstant(ZoneIdUtil.getSystemZoneId()).toLocalDateTime();

        given(KoreanDateTimeUtil.toKoreanTime(endDateTime)).willReturn(endDateTimeInKoreanTime);
        given(LeaderElectionCurrentDateTimeProvider.getKoreanDateTime()).willReturn(koreanLocalDateTime);

        given(leaderElection.getCandidateRegistrationEndDateTime()).willReturn(endDateTime);
        given(leaderElection.isCandidateRegistrationPeriod()).willReturn(Boolean.TRUE);
        given(leaderElection.isCandidateRegistrationPeriodEnded()).willReturn(Boolean.FALSE);

        final LeaderElectionProcessLabelResponse actual = converter.convert(leaderElection);
        assertThat(actual.getTitle(), is(expectedTitle));
    }
}
