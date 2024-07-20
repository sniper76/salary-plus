package ag.act.service.download.datamatrix.record.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

import static ag.act.util.DecimalFormatUtil.formatWithoutFollowingZero;

@Component
public class UserRetentionWeeklyCsvRecordByWeekGenerator {

    private static final String PLACEHOLDER = "";
    private static final String TILDE = "~";

    public String[] toCsvRecord(LocalDate referenceWeekStartDate, Map<LocalDate, Double> dataByWeekStartDate) {
        return Stream.concat(
                getFirstColumnOfRecordStream(referenceWeekStartDate),
                getWeeklyRetentionRecordStream(referenceWeekStartDate, dataByWeekStartDate)
            )
            .toArray(String[]::new);
    }

    private Stream<String> getFirstColumnOfRecordStream(LocalDate referenceWeekStartDate) {
        return Stream.of(
            referenceWeekStartDate
                + TILDE
                + referenceWeekStartDate.plusWeeks(1).minusDays(1)
        );
    }

    private Stream<String> getWeeklyRetentionRecordStream(
        LocalDate referenceWeekStartDate, Map<LocalDate, Double> dataByWeekStartDate
    ) {
        return dataByWeekStartDate.keySet().stream()
            .sorted()
            .map(weekStartDate ->
                weekStartDate.isBefore(referenceWeekStartDate)
                    ? PLACEHOLDER
                    : formatWithoutFollowingZero(dataByWeekStartDate.get(weekStartDate))
            );
    }
}
