package ag.act.service.download.csv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
@RequiredArgsConstructor
@Component
public class CsvDownloadServiceResolver {
    private final List<CsvDownloadService> csvDownloadServices;
    private final DefaultCsvDownloadService defaultCsvDownloadService;

    public CsvDownloadService resolve(CsvDownloadSourceProvider sourceProvider) {
        return csvDownloadServices
            .stream()
            .filter(csvDownloadService -> csvDownloadService.isSupport(sourceProvider))
            .findFirst()
            .orElse(defaultCsvDownloadService);
    }
}
