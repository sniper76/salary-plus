package ag.act.validator;

import ag.act.entity.ZipFileDownload;
import ag.act.enums.digitaldocument.ZipFileStatus;
import ag.act.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class ZipFileDownloadValidator {

    public void validate(ZipFileDownload zipFileDownload) {

        if (zipFileDownload == null
            || zipFileDownload.getZipFileStatus() != ZipFileStatus.COMPLETE) {
            throw new BadRequestException("ZIP 파일이 준비되지 않았습니다.");
        }

        if (!zipFileDownload.getIsLatest()) {
            throw new BadRequestException("ZIP 파일을 더 이상 다운로드 할 수 없습니다.");
        }
    }
}
