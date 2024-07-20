package ag.act.service.download.datamatrix.headerwriter;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Component
public class ThreeWeeksInARowHeaderWriter extends UserRetentionWeeklyCsvHeaderWriter {

    @Override
    public void setHeaders(CSVWriter csvWriter, LocalDate appRenewalDate, LocalDate today) {
        List<String> headerList = Stream.iterate(
                appRenewalDate,
                week -> week.isBefore(today),
                week -> week.plusWeeks(1)
            )
            .map(week -> week + TILDE + week.plusWeeks(3).minusDays(1))
            .toList();

        write(csvWriter, headerList);
    }

    //TODO: 해당 클래스가 사용되는 csv 배치저장+다운로드로 변경되면 수정되어야 함.
    @Override
    public int setHeadersAndGetSize(CSVWriter csvWriter, LocalDate appRenewalDate, LocalDate referenceDate) {
        return 0;
    }
}
