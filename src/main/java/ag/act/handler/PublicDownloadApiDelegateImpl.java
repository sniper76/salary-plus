package ag.act.handler;

import ag.act.api.PublicDownloadApiDelegate;
import ag.act.enums.DownloadFileType;
import ag.act.exception.BadRequestException;
import ag.act.facade.download.PublicDownloadFileFacade;
import ag.act.util.DownloadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PublicDownloadApiDelegateImpl implements PublicDownloadApiDelegate {
    private final PublicDownloadFileFacade publicDownloadFileFacade;

    @Override
    public ResponseEntity<Resource> downloadFile(String downloadFileTypeName, String zipFileKey) {
        final DownloadFileType downloadFileType = DownloadFileType.fromValue(downloadFileTypeName);

        if (downloadFileType == DownloadFileType.ZIP) {
            return DownloadFileUtil.ok(publicDownloadFileFacade.downloadFile(zipFileKey));
        }

        throw new BadRequestException("지원하지 않는 DownloadFileType %s 입니다.".formatted(downloadFileTypeName));

    }
}
