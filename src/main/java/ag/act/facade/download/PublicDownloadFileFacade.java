package ag.act.facade.download;

import ag.act.dto.download.DownloadFile;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PublicDownloadFileFacade {

    private final DownloadableFacadeResolver downloadableFacadeResolver;

    public DownloadFile downloadFile(String zipFileKey) {
        return downloadableFacadeResolver.resolve(zipFileKey).downloadZipFile(zipFileKey);
    }
}
