package ag.act.service.download.csv;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultCsvDownloadService implements CsvDownloadService<Object> {

    @Override
    public void download(HttpServletResponse response, CsvDownloadSourceProvider<Object> sourceProvider) {
        log.error("Not found csv download service for category: {}, source: {}", sourceProvider.getBoardCategory(), sourceProvider.getSource());
    }
}
