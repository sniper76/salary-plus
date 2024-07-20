package ag.act.service.download.datamatrix.record.generator;

import ag.act.dto.SimpleUserDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class UserRetentionWeeklyCsvRecordByUserGenerator {

    public String[] toCsvRecord(Map<LocalDate, String> dataByWeek, SimpleUserDto user, String[] record, int endIndex) {
        return Stream.of(
                getFirstColumnOfRecordStream(user),
                getRecordFromCsv(record, dataByWeek.size(), endIndex),
                getWeeklyRetentionRecordStream(dataByWeek)
            )
            .flatMap(Function.identity())
            .toArray(String[]::new);
    }

    private Stream<String> getRecordFromCsv(String[] record, int newDataSize, int endIndex) {
        if (endIndex > newDataSize && record.length == 0) {
            return Arrays.stream(new String[endIndex - newDataSize]);
        }
        return Arrays.stream(record);
    }

    private Stream<String> getFirstColumnOfRecordStream(SimpleUserDto user) {
        return Stream.of(
            "%s/%s/%s".formatted(
                user.getId(),
                getAgeRange(user.getBirthDate().getYear()),
                user.getGender()
            )
        );
    }

    private Stream<String> getWeeklyRetentionRecordStream(Map<LocalDate, String> dataByWeek) {
        return dataByWeek.keySet().stream()
            .sorted()
            .map(dataByWeek::get);
    }

    private int getAgeRange(int userBirthYear) {
        return ((LocalDate.now().getYear() - userBirthYear) / 10) * 10;
    }

}
