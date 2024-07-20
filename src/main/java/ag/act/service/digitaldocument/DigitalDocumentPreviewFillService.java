package ag.act.service.digitaldocument;

import ag.act.converter.digitaldocument.DigitalDocumentPreviewFillConverter;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.InternalServerException;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.GrantorFill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DigitalDocumentPreviewFillService {
    private final List<DigitalDocumentPreviewFillConverter<PreviewDigitalDocumentRequest, DigitalDocumentFill>>
        digitalDocumentPreviewFillConverters;

    public DigitalDocumentFill fill(PreviewDigitalDocumentRequest request) {
        DigitalDocumentFill digitalDocumentFill = createDigitalDocumentFill(request);

        digitalDocumentFill.setDigitalDocumentNo(
            DigitalDocumentType.fromValue(request.getType()).generateDocumentNo(
                0L,
                "000000",
                0L
            )
        );
        digitalDocumentFill.setGrantor(GrantorFill.createPreview());
        digitalDocumentFill.setCompanyName(request.getCompanyName());
        digitalDocumentFill.setAttachingFilesDescription("유저별 첨부파일 첨부 내역이 들어갑나다.");
        digitalDocumentFill.setVersion(request.getVersion());

        return digitalDocumentFill;
    }

    private DigitalDocumentFill createDigitalDocumentFill(PreviewDigitalDocumentRequest request) {
        return digitalDocumentPreviewFillConverters.stream()
            .filter(converter -> converter.canConvert(DigitalDocumentType.fromValue(request.getType())))
            .findFirst()
            .orElseThrow(() -> new InternalServerException("지원하지 않는 문서 타입입니다."))
            .apply(request);
    }
}
