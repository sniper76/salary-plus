package ag.act.handler.callback;

import ag.act.api.DigitalDocumentCallbackApiDelegate;
import ag.act.core.guard.BatchApiKeyGuard;
import ag.act.core.guard.UseGuards;
import ag.act.enums.FileType;
import ag.act.facade.digitaldocument.DigitalDocumentDownloadFacade;
import ag.act.facade.download.Downloadable;
import ag.act.model.DigitalDocumentZipFileCallbackRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("checkstyle:ParameterName")
@UseGuards({BatchApiKeyGuard.class})
public class DigitalDocumentCallbackApiDelegateImpl implements DigitalDocumentCallbackApiDelegate {

    private final List<Downloadable> downloadServices;
    private final DigitalDocumentDownloadFacade digitalDocumentDownloadFacade;

    @Override
    public ResponseEntity<SimpleStringResponse> digitalDocumentZipFileCallback(
        String xApiKey,
        String fileType,
        DigitalDocumentZipFileCallbackRequest digitalDocumentZipFileCallbackRequest
    ) {
        final FileType fileTypeEnum = FileType.fromValue(fileType);

        downloadServices.stream()
            .filter(it -> it.supports(fileTypeEnum))
            .findFirst()
            .orElse(digitalDocumentDownloadFacade)
            .updateDownloadZipFile(digitalDocumentZipFileCallbackRequest);

        return SimpleStringResponseUtil.okResponse();
    }
}
