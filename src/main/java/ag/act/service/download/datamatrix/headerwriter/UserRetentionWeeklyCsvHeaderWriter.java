package ag.act.service.download.datamatrix.headerwriter;

import com.opencsv.CSVWriter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public abstract class UserRetentionWeeklyCsvHeaderWriter {
    protected static final String TILDE = "~";
    protected static final String PLACEHOLDER = "";

    // TODO: 모든 csv가 배치저장+다운로드로 변경되면 setHeaders 통일 필요
    public abstract void setHeaders(CSVWriter csvWriter, LocalDate appRenewalDate, LocalDate today);

    public abstract int setHeadersAndGetSize(CSVWriter csvWriter, LocalDate appRenewalDate, LocalDate referenceDate);

    protected void write(CSVWriter csvWriter, List<String> headerList) {
        csvWriter.writeNext(
            Stream.concat(
                Stream.of(PLACEHOLDER),
                headerList.stream()
            ).toArray(String[]::new)
        );
    }

}
