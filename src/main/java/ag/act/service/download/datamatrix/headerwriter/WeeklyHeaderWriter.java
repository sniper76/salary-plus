package ag.act.service.download.datamatrix.headerwriter;

import ag.act.util.DateTimeUtil;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Component
public class WeeklyHeaderWriter extends UserRetentionWeeklyCsvHeaderWriter {

    @Override
    public void setHeaders(CSVWriter csvWriter, LocalDate appRenewalDate, LocalDate today) {
        List<String> headerList = getHeaderList(appRenewalDate, today);

        write(csvWriter, headerList);
    }

    @Override
    public int setHeadersAndGetSize(CSVWriter csvWriter, LocalDate appRenewalDate, LocalDate referenceDate) {
        List<String> headerList = getHeaderList(appRenewalDate, referenceDate.plusDays(1));

        write(csvWriter, headerList);

        return headerList.size();
    }

    private List<String> getHeaderList(LocalDate appRenewalDate, LocalDate date) {
        List<String> headerList = Stream.iterate(
                appRenewalDate,
                week -> week.isBefore(date),
                week -> week.plusWeeks(1)
            )
            .map(week -> week + TILDE + DateTimeUtil.getDateBeforeNextWeek(week))
            .toList();
        return headerList;
    }
}
