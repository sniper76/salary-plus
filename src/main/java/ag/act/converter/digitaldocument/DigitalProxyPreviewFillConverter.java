package ag.act.converter.digitaldocument;

import ag.act.enums.DigitalDocumentType;
import ag.act.enums.digitaldocument.ShareholderMeetingType;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.module.digitaldocumentgenerator.model.AcceptorFill;
import ag.act.module.digitaldocumentgenerator.model.DateFill;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.DigitalProxyFill;
import ag.act.module.digitaldocumentgenerator.model.ShareHolderMeetingFill;
import ag.act.service.admin.CorporateUserService;
import ag.act.util.KoreanDateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DigitalProxyPreviewFillConverter
    implements DigitalDocumentPreviewFillConverter<PreviewDigitalDocumentRequest, DigitalDocumentFill> {
    private final DigitalProxyItemsPreviewFillConverter digitalProxyItemsPreviewFillConverter;
    private final CorporateUserService corporateUserService;

    @Override
    public boolean canConvert(DigitalDocumentType type) {
        return type == DigitalDocumentType.DIGITAL_PROXY;
    }

    @Override
    public DigitalProxyFill apply(PreviewDigitalDocumentRequest previewDigitalDocumentRequest) {
        return convert(previewDigitalDocumentRequest);
    }

    private DigitalProxyFill convert(PreviewDigitalDocumentRequest request) {
        DigitalProxyFill digitalProxyFill = new DigitalProxyFill();
        final String corporateNo = corporateUserService.getNullableCorporateNoByUserId(request.getAcceptUserId());

        digitalProxyFill.setShareHolderMeeting(
            new ShareHolderMeetingFill(
                request.getShareholderMeetingName(),
                ShareholderMeetingType.REGULAR_GENERAL_MEETING_STOCKHOLDERS.name()
                    .equals(request.getShareholderMeetingType()),
                new DateFill(KoreanDateTimeUtil.toKoreanTime(request.getShareholderMeetingDate()))
            )
        );
        digitalProxyFill.setDesignatedAgentNames(request.getDesignatedAgentNames());
        digitalProxyFill.setDigitalDocumentItems(digitalProxyItemsPreviewFillConverter.convert(request.getChildItems()));
        digitalProxyFill.setAcceptor(AcceptorFill.createPreview(corporateNo));

        return digitalProxyFill;
    }
}

