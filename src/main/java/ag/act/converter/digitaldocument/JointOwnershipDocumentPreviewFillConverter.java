package ag.act.converter.digitaldocument;

import ag.act.enums.DigitalDocumentType;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.module.digitaldocumentgenerator.model.AcceptorFill;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.JointOwnershipDocumentFill;
import ag.act.service.admin.CorporateUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JointOwnershipDocumentPreviewFillConverter
    implements DigitalDocumentPreviewFillConverter<PreviewDigitalDocumentRequest, DigitalDocumentFill> {
    private final CorporateUserService corporateUserService;

    @Override
    public boolean canConvert(DigitalDocumentType type) {
        return type == DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT;
    }

    @Override
    public JointOwnershipDocumentFill apply(PreviewDigitalDocumentRequest request) {
        return convert(request);
    }

    private JointOwnershipDocumentFill convert(PreviewDigitalDocumentRequest request) {
        JointOwnershipDocumentFill jointOwnershipDocumentFill = new JointOwnershipDocumentFill();
        final String corporateNo = corporateUserService.getNullableCorporateNoByUserId(request.getAcceptUserId());

        jointOwnershipDocumentFill.setCompanyRegistrationNumber(request.getCompanyRegistrationNumber());
        jointOwnershipDocumentFill.setAcceptor(AcceptorFill.createPreview(corporateNo));
        jointOwnershipDocumentFill.setContent(request.getContent());

        return jointOwnershipDocumentFill;
    }
}
