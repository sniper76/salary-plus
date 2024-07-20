package ag.act.module.time;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;

@MockitoSettings(strictness = Strictness.LENIENT)
class TimeDisplayFormatterTest {

    @InjectMocks
    private TimeDisplayFormatter timeDisplayFormatter;

    @Test
    @DisplayName("0초는 '0초' 로 포맷팅한다.")
    void formatTime_GivenZeroSeconds_ShouldReturnZeroSeconds() {
        long seconds = 0L;
        String result = timeDisplayFormatter.format(seconds);
        assertThat(result).isEqualTo("0초");
    }

    @Test
    @DisplayName("1초는 '1초' 로 포맷팅한다.")
    void formatTime_GivenOneSecond_ShouldReturnOneSecond() {
        long seconds = 1L;
        String result = timeDisplayFormatter.format(seconds);
        assertThat(result).isEqualTo("1초");
    }

    @Test
    @DisplayName("59초는 '59초' 로 포맷팅한다.")
    void formatTime_GivenFiftyNineSeconds_ShouldReturnFiftyNineSeconds() {
        long seconds = 59L;
        String result = timeDisplayFormatter.format(seconds);
        assertThat(result).isEqualTo("59초");
    }

    @Test
    @DisplayName("60초는 '1분'으로 포맷팅한다.")
    void formatTime_GivenSixtySeconds_ShouldReturnOneMinute() {
        long seconds = 60L;
        String result = timeDisplayFormatter.format(seconds);
        assertThat(result).isEqualTo("1분");
    }

    @Test
    @DisplayName("61초는 '1분 1초'로 포맷팅한다.")
    void formatTime_GivenSixtyOneSeconds_ShouldReturnOneMinuteAndOneSecond() {
        long seconds = 61L;
        String result = timeDisplayFormatter.format(seconds);
        assertThat(result).isEqualTo("1분 1초");
    }

    @Test
    @DisplayName("3600초는 '1시간'으로 포맷팅한다.")
    void formatTime_GivenOneHour_ShouldReturnOneHour() {
        long seconds = 3600L;
        String result = timeDisplayFormatter.format(seconds);
        assertThat(result).isEqualTo("1시간");
    }

    @Test
    @DisplayName("1시간 1초는 '1시간 1초'로 포맷팅한다.")
    void formatTime_GivenThreeHoursFiveSeconds_ShouldReturnThreeHoursFiveSeconds() {
        long seconds = 3601;
        String result = timeDisplayFormatter.format(seconds);
        assertThat(result).isEqualTo("1시간 1초");
    }

    @Test
    @DisplayName("1시간 60초는 '1시간 1분'으로 포맷팅한다.")
    void formatTime_GivenOneHourSixtySeconds_ShouldReturnOneHourOneMinute() {
        long seconds = 3660L;
        String result = timeDisplayFormatter.format(seconds);
        assertThat(result).isEqualTo("1시간 1분");
    }

    @Test
    @DisplayName("3661초는 '1시간 1분 1초'로 포맷팅한다.")
    void formatTime_GivenOneHourOneMinuteOneSecond_ShouldReturnOneHourOneMinuteOneSecond() {
        long seconds = 3661L;
        String result = timeDisplayFormatter.format(seconds);
        assertThat(result).isEqualTo("1시간 1분 1초");
    }
}