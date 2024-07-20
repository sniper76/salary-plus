package ag.act.service.stockboardgrouppost.holderlistreadandcopy;

import ag.act.constants.DigitalDocumentTemplateNames;
import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.dto.user.HolderListReadAndCopyDataModel;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.digitaldocument.IdCardWatermarkType;
import ag.act.exception.InternalServerException;
import ag.act.module.digitaldocumentgenerator.document.DigitalDocumentGenerator;
import ag.act.module.digitaldocumentgenerator.dto.AttachingFilesDto;
import ag.act.module.digitaldocumentgenerator.dto.GenerateDigitalDocumentDto;
import ag.act.service.digitaldocument.HolderListReadAndCopyListWrapper;
import ag.act.service.digitaldocument.HolderListReadAndCopyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class HolderListReadAndCopyUploadService implements DigitalDocumentTemplateNames {
    private final DigitalDocumentGenerator digitalDocumentGenerator;
    private final HolderListReadAndCopyService holderListReadAndCopyService;

    public PdfDataDto generatePdf(
        DigitalDocument digitalDocument,
        DigitalDocumentUser digitalDocumentUser,
        MultipartFile signImage,
        MultipartFile idCardImage,
        List<MultipartFile> bankAccountImages,
        MultipartFile hectoEncryptedBankAccountPdf
    ) {
        try {
            return digitalDocumentGenerator.generate(
                getGenerateDigitalDocumentDto(
                    signImage, idCardImage, bankAccountImages, hectoEncryptedBankAccountPdf, digitalDocument.getId()
                ),
                digitalDocument,
                digitalDocumentUser
            );
        } catch (Exception e) {
            throw new InternalServerException("주주명부 열람/등사 청구 PDF 생성 중에 알 수 없는 오류가 발생하였습니다.", e);
        }
    }

    private GenerateDigitalDocumentDto getGenerateDigitalDocumentDto(
        MultipartFile signatureImage,
        MultipartFile idCardImage,
        List<MultipartFile> bankAccountImageList,
        MultipartFile hectoEncryptedBankAccountPdf,
        Long digitalDocumentId
    ) {
        return GenerateDigitalDocumentDto
            .builder()
            .signatureImage(signatureImage)
            .attachingFilesDto(
                getAttachingFilesDto(idCardImage, bankAccountImageList, hectoEncryptedBankAccountPdf)
                    .orElse(null)
            )
            .holderListReadAndCopyDataModel(getHolderListReadAndCopyDataModel(digitalDocumentId))
            .build();
    }

    private HolderListReadAndCopyDataModel getHolderListReadAndCopyDataModel(Long digitalDocumentId) {
        final HolderListReadAndCopyListWrapper holderListReadAndCopyListWrapper = getHolderListReadAndCopyList(digitalDocumentId);

        return new HolderListReadAndCopyDataModel(holderListReadAndCopyListWrapper);
    }

    private HolderListReadAndCopyListWrapper getHolderListReadAndCopyList(Long digitalDocumentId) {
        return holderListReadAndCopyService.getHolderListReadAndCopyListWrapper(digitalDocumentId);
    }

    private Optional<AttachingFilesDto> getAttachingFilesDto(
        MultipartFile idCardImage,
        List<MultipartFile> bankAccountImageList,
        MultipartFile hectoEncryptedBankAccountPdf
    ) {
        if (idCardImage == null && CollectionUtils.isEmpty(bankAccountImageList) && hectoEncryptedBankAccountPdf == null) {
            return Optional.empty();
        }

        return Optional.of(
            AttachingFilesDto.builder()
                .idCardWatermarkType(IdCardWatermarkType.ACT_LOGO)
                .idCardImage(idCardImage)
                .bankAccountImages(bankAccountImageList)
                .hectoEncryptedBankAccountPdf(hectoEncryptedBankAccountPdf)
                .build()
        );
    }
}
