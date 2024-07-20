package ag.act.module.digitaldocumentgenerator.converter;

import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.entity.digitaldocument.IDigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.BadRequestException;
import ag.act.module.digitaldocumentgenerator.DigitalDocumentNumberGenerator;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.DigitalProxyFill;
import ag.act.module.digitaldocumentgenerator.model.HolderListReadAndCopyFill;
import ag.act.module.digitaldocumentgenerator.model.JointOwnershipDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.OtherDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.SinglePageImageFill;
import ag.act.module.digitaldocumentgenerator.util.MultipartFileToBase64Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public abstract class BaseDigitalDocumentFillConverter {
    @Autowired
    private AttachingFilesDescriptionConverter attachingFilesDescriptionConverter;
    @Autowired
    private GrantorFillConverter grantorFillConverter;
    @Autowired
    private DigitalDocumentNumberGenerator digitalDocumentNumberGenerator;

    public abstract DigitalDocumentFill convert(IGenerateHtmlDocumentDto generateHtmlDocumentDto);

    public SinglePageImageFill convert(MultipartFile image) {
        final SinglePageImageFill fill = new SinglePageImageFill();

        if (image == null) {
            throw new BadRequestException("전자문서에 첨부된 이미지가 없습니다.");
        }

        fill.setImageSrc(
            MultipartFileToBase64Util.convertMultipartFileToBase64(image)
        );

        return fill;
    }

    protected DigitalDocumentFill getBaseDigitalDocumentFill(IGenerateHtmlDocumentDto dto) {
        final IDigitalDocument digitalDocument = dto.getDigitalDocument();
        final DigitalDocumentUser digitalDocumentUser = dto.getDigitalDocumentUser();
        final DigitalDocumentType digitalDocumentType = digitalDocument.getType();
        final DigitalDocumentFill digitalDocumentFill = getDigitalDocumentFill(digitalDocumentType);

        digitalDocumentFill.setDigitalDocumentNo(digitalDocumentNumberGenerator.generate(
            digitalDocumentType, digitalDocument.getId(), digitalDocumentUser.getIssuedNumber()
        ));
        digitalDocumentFill.setCompanyName(digitalDocument.getCompanyName());
        if (dto.getSignatureImage() != null) {
            digitalDocumentFill.setSignatureImageSrc(
                MultipartFileToBase64Util.convertMultipartFileToBase64(dto.getSignatureImage())
            );
        }
        digitalDocumentFill.setGrantor(
            grantorFillConverter.apply(digitalDocumentUser)
        );
        digitalDocumentFill.setAttachingFilesDescription(
            attachingFilesDescriptionConverter.convert(dto.getAttachingFilesDto())
        );
        digitalDocumentFill.setVersion(digitalDocument.getVersion().name());

        return digitalDocumentFill;
    }

    private DigitalDocumentFill getDigitalDocumentFill(DigitalDocumentType digitalDocumentType) {
        switch (digitalDocumentType) {
            case DIGITAL_PROXY -> {
                return new DigitalProxyFill();
            }
            case JOINT_OWNERSHIP_DOCUMENT -> {
                return new JointOwnershipDocumentFill();
            }
            case HOLDER_LIST_READ_AND_COPY_DOCUMENT -> {
                return new HolderListReadAndCopyFill();
            }
            default -> {
                return new OtherDocumentFill();
            }
        }
    }
}
