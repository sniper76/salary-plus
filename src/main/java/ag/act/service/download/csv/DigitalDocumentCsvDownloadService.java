package ag.act.service.download.csv;

import ag.act.enums.BoardCategory;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DigitalDocumentCsvDownloadService implements CsvDownloadService<List<Long>> {
    private static final List<BoardCategory> SUPPORT_BOARD_CATEGORIES = List.of(
        BoardCategory.ETC,
        BoardCategory.DIGITAL_DELEGATION,
        BoardCategory.CO_HOLDING_ARRANGEMENTS
    );
    private final DigitalDocumentCsvDownloadProcessor digitalDocumentCsvDownloadProcessor;

    @Override
    public boolean isSupport(CsvDownloadSourceProvider<List<Long>> sourceProvider) {
        return SUPPORT_BOARD_CATEGORIES.contains(sourceProvider.getBoardCategory())
            && List.class.isAssignableFrom(sourceProvider.getSourceType());
    }

    @Override
    public void download(HttpServletResponse response, CsvDownloadSourceProvider<List<Long>> sourceProvider) {
        digitalDocumentCsvDownloadProcessor.download(response, sourceProvider.getSource());
    }
}
