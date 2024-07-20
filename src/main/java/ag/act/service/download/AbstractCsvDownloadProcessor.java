package ag.act.service.download;

import ag.act.util.CSVWriterFactory;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;

import java.io.OutputStream;

@RequiredArgsConstructor
public abstract class AbstractCsvDownloadProcessor {
    private final CSVWriterFactory csvWriterFactory;

    protected CSVWriter initializeCsvWriter(OutputStream outputStream) {
        return csvWriterFactory.create(outputStream);
    }
}
