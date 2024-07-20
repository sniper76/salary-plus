package ag.act.service.download.csv;

import ag.act.enums.BoardCategory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CsvDownloadSourceProvider<T> {

    private final BoardCategory boardCategory;
    private final T source;

    public CsvDownloadSourceProvider(T source) {
        this(null, source);
    }

    @SuppressWarnings("unchecked")
    public Class<T> getSourceType() {
        return (Class<T>) source.getClass();
    }
}
