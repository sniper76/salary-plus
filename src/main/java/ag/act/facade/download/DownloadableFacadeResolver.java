package ag.act.facade.download;

import ag.act.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DownloadableFacadeResolver {
    private final List<Downloadable> downloadableList;

    public Downloadable resolve(String zipFileKey) {
        for (Downloadable downloadable : downloadableList) {
            if (downloadable.findZipFileDownloadByZipFileKey(zipFileKey).isPresent()) {
                return downloadable;
            }
        }

        throw new BadRequestException("파일을 찾을 수 없습니다.");
    }
}
