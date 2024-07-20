package ag.act.service.download.csv;

import jakarta.servlet.http.HttpServletResponse;

public interface CsvDownloadService<T> {

    default boolean isSupport(CsvDownloadSourceProvider<T> sourceProvider) {
        return false;
    }

    void download(HttpServletResponse response, CsvDownloadSourceProvider<T> sourceProvider);

}
