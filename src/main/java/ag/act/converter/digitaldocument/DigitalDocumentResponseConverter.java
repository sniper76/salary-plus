package ag.act.converter.digitaldocument;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.model.DigitalDocumentResponse;
import ag.act.util.DigitalDocumentItemTreeGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DigitalDocumentResponseConverter {
    private final DigitalDocumentItemTreeGenerator digitalDocumentItemTreeGenerator;
    private final DigitalDocumentDownloadResponseConverter digitalDocumentDownloadResponseConverter;
    private final DigitalDocumentJsonAttachOptionConverter digitalDocumentJsonAttachOptionConverter;

    public DigitalDocumentResponse convert(
        DigitalDocument digitalDocument, List<DigitalDocumentItem> itemList
    ) {
        return new DigitalDocumentResponse()
            .id(digitalDocument.getId())
            .type(digitalDocument.getType().name())
            .version(digitalDocument.getVersion().name())
            .title(digitalDocument.getTitle())
            .content(digitalDocument.getContent())
            .companyName(digitalDocument.getCompanyName())
            .targetStartDate(DateTimeConverter.convert(digitalDocument.getTargetStartDate()))
            .targetEndDate(DateTimeConverter.convert(digitalDocument.getTargetEndDate()))
            .companyRegistrationNumber(digitalDocument.getCompanyRegistrationNumber())
            .acceptUserId(digitalDocument.getAcceptUserId())
            .stockReferenceDate(DateTimeConverter.convert(getStockReferenceLocalDateTime(digitalDocument)))
            .shareholderMeetingDate(DateTimeConverter.convert(digitalDocument.getShareholderMeetingDate()))
            .shareholderMeetingName(digitalDocument.getShareholderMeetingName())
            .shareholderMeetingType(digitalDocument.getShareholderMeetingType())
            .designatedAgentNames(digitalDocument.getDesignatedAgentNames())
            .attachOptions(digitalDocumentJsonAttachOptionConverter.apply(digitalDocument.getJsonAttachOption()))
            .items(getItems(itemList))
            .digitalDocumentDownload(getDigitalDocumentDownload(digitalDocument))
            .createdAt(DateTimeConverter.convert(digitalDocument.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(digitalDocument.getUpdatedAt()));
    }

    private ag.act.model.DigitalDocumentDownloadResponse getDigitalDocumentDownload(DigitalDocument digitalDocument) {
        return digitalDocument.getLatestDigitalDocumentDownload().map(digitalDocumentDownloadResponseConverter::convert).orElse(null);
    }

    private List<ag.act.model.DigitalDocumentItemResponse> getItems(List<DigitalDocumentItem> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return null;
        }
        return digitalDocumentItemTreeGenerator.buildTree(itemList);
    }

    private LocalDateTime getStockReferenceLocalDateTime(DigitalDocument digitalDocument) {
        if (digitalDocument.getStockReferenceDate() == null) {
            return null;
        }
        return digitalDocument.getStockReferenceDate().atStartOfDay();
    }
}
