package ag.act.service.download.datamatrix.headerwriter;

import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserRetentionWeeklyCsvHeaderWriterResolver {
    private final ThreeWeeksInARowHeaderWriter threeWeeksInARowHeaderWriter;
    private final WeeklyHeaderWriter weeklyHeaderWriter;

    public UserRetentionWeeklyCsvHeaderWriter resolve(UserRetentionWeeklyCsvDataType type) {
        return switch (type.getWeekUnit()) {
            case WEEK -> weeklyHeaderWriter;
            case THREE_WEEKS -> threeWeeksInARowHeaderWriter;
        };
    }
}
