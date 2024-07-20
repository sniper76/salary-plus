package ag.act.service.download.datamatrix;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UserRetentionDataMapUtil {

    public static void addValueToDataByWeek(LocalDate keyWeek, double value, Map<LocalDate, Double> dataByWeek) {
        dataByWeek.merge(keyWeek, value, Double::sum);
    }

    public static Map<LocalDate, Double> initializeNumericDataMapByWeek(LocalDate startDate, LocalDate endDate) {
        return new ConcurrentHashMap<>(
            Stream.iterate(
                    startDate,
                    week -> week.isBefore(endDate),
                    week -> week.plusWeeks(1)
                )
                .collect(Collectors.toMap(Function.identity(), week -> 0.0))
        );
    }

    public static Map<LocalDate, String> initializeStringDataMapByWeek(LocalDate startDate, LocalDate endDate) {
        return new ConcurrentHashMap<>(
            Stream.iterate(
                    startDate,
                    week -> week.isBefore(endDate) || week.isEqual(endDate),
                    week -> week.plusWeeks(1)
                )
                .collect(Collectors.toMap(Function.identity(), week -> ""))
        );
    }
}
