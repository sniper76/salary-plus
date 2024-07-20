package ag.act.service.download.csv;

import ag.act.entity.Post;
import ag.act.enums.BoardCategory;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PollCsvDownloadService implements CsvDownloadService<List<Post>> {
    private final PollCsvDownloadProcessor pollCsvDownloadProcessor;

    @Override
    public boolean isSupport(CsvDownloadSourceProvider<List<Post>> sourceProvider) {
        return sourceProvider.getBoardCategory() == BoardCategory.SURVEYS
            && List.class.isAssignableFrom(sourceProvider.getSourceType());
    }

    @Override
    public void download(HttpServletResponse response, CsvDownloadSourceProvider<List<Post>> sourceProvider) {
        pollCsvDownloadProcessor.download(response, sourceProvider.getSource());
    }
}
