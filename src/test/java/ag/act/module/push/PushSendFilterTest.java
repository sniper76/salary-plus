package ag.act.module.push;

import ag.act.entity.Push;
import ag.act.module.cache.PushPreference;
import ag.act.util.KoreanDateTimeUtil;
import org.apache.commons.lang3.IntegerRange;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

@MockitoSettings(strictness = Strictness.LENIENT)
class PushSendFilterTest {
    @InjectMocks
    private PushSendFilter filter;
    private List<MockedStatic<?>> statics;
    @Mock
    private PushPreference pushPreference;
    @Mock
    private Push push;
    @Mock
    private Push pushForSafeTime;
    @Mock
    private ZonedDateTime currentKoreanTime;
    private Integer currentKoreanTimeHour;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(KoreanDateTimeUtil.class));
        currentKoreanTimeHour = someIntegerBetween(0, 23);

        given(currentKoreanTime.getHour()).willReturn(currentKoreanTimeHour);
        given(KoreanDateTimeUtil.getNowInKoreanTime()).willReturn(currentKoreanTime);
    }


    @Nested
    class WhenCurrentTimeIsInPushSafeTimeRange {
        @BeforeEach
        void setUp() {
            given(pushPreference.getPushSafeTimeRangeInHours())
                .willReturn(IntegerRange.of(currentKoreanTimeHour - 1, currentKoreanTimeHour + 1));
        }

        @Test
        void shouldReturnEmptyList() {
            List<Push> actualResult = filter.filter(List.of(push, pushForSafeTime));

            assertThat(actualResult, is(List.of(push, pushForSafeTime)));
        }
    }

    @Nested
    class WhenCurrentTimeIsNotInSafeTimeRange {
        @BeforeEach
        void setUp() {
            given(pushPreference.getPushSafeTimeRangeInHours())
                .willReturn(IntegerRange.of(currentKoreanTimeHour + 1, currentKoreanTimeHour + 2));
        }

        @Test
        void shouldReturnEmptyList() {
            List<Push> actualResult = filter.filter(List.of(push, pushForSafeTime));

            assertThat(actualResult, is(List.of()));
        }
    }
}
