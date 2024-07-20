package ag.act.module.digitaldocumentgenerator.converter;

import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.entity.digitaldocument.IDigitalProxy;
import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.DigitalDocumentNumberGenerator;
import ag.act.module.digitaldocumentgenerator.dto.GenerateHtmlCertificationDto;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentCertificationFill;
import ag.act.module.digitaldocumentgenerator.model.DigitalProxyCertificationFill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DigitalProxyCertificationFillConverter implements IDigitalDocumentCertificationFillConverter {
    private final AcceptorFillConverter acceptorFillConverter;
    private final GrantorFillConverter grantorFillConverter;
    private final ProgressRecordFillConverter progressRecordFillConverter;
    private final ShareHolderMeetingFillConverter shareHolderMeetingFillConverter;
    private final DigitalDocumentNumberGenerator digitalDocumentNumberGenerator;

    @Override
    public DigitalDocumentCertificationFill convert(GenerateHtmlCertificationDto dto) {
        IDigitalProxy digitalProxy = (IDigitalProxy) dto.getDigitalDocument();
        DigitalDocumentUser digitalDocumentUser = dto.getDigitalDocumentUser();

        DigitalProxyCertificationFill digitalProxyCertificationFill = new DigitalProxyCertificationFill();

        digitalProxyCertificationFill.setGrantor(grantorFillConverter.apply(digitalDocumentUser));
        digitalProxyCertificationFill.setCompanyName(digitalProxy.getCompanyName());
        digitalProxyCertificationFill.setOriginalPageCount(digitalDocumentUser.getOriginalPageCount());
        digitalProxyCertificationFill.setAttachmentPageCount(digitalDocumentUser.getAttachmentPageCount());
        digitalProxyCertificationFill.setDocumentNo(
            digitalDocumentNumberGenerator.generate(
                DigitalDocumentType.DIGITAL_PROXY, digitalProxy.getId(), digitalDocumentUser.getIssuedNumber()
            )
        );
        digitalProxyCertificationFill.setAcceptor(
            acceptorFillConverter.convert(digitalProxy.getStockCode(), digitalProxy.getAcceptUserId())
        );
        digitalProxyCertificationFill.setProgressRecords(
            progressRecordFillConverter.convert(digitalDocumentUser.getId(), digitalDocumentUser.getUserId())
        );
        digitalProxyCertificationFill.setShareHolderMeeting(shareHolderMeetingFillConverter.convert(digitalProxy));

        return digitalProxyCertificationFill;
    }
}
