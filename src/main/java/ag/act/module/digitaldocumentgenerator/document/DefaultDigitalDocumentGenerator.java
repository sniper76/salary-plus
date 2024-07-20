package ag.act.module.digitaldocumentgenerator.document;

import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.DigitalDocumentFilenameGenerator;
import ag.act.module.digitaldocumentgenerator.document.typegenerator.DigitalDocumentTypeGenerator;
import ag.act.module.digitaldocumentgenerator.dto.AttachingFilesDto;
import ag.act.module.digitaldocumentgenerator.dto.GenerateDigitalDocumentDto;
import ag.act.module.digitaldocumentgenerator.dto.GenerateHtmlDocumentDto;
import ag.act.module.markany.dna.MarkAnyDigitalDocument;
import ag.act.module.markany.dna.MarkAnyService;
import ag.act.util.FilenameUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@SuppressWarnings("AbbreviationAsWordInName")
class DefaultDigitalDocumentGenerator implements IDigitalDocumentGenerator {

    private final DigitalDocumentTypeGenerator digitalDocumentTypeGenerator;
    private final MarkAnyService markAnyService;
    private final DigitalDocumentFilenameGenerator digitalDocumentFilenameGenerator;
    private final DigitalDocumentUploadService digitalDocumentUploadService;

    @Override
    public boolean supports(DigitalDocumentType digitalDocumentType) {
        return !isHolderListReadAndCopy(digitalDocumentType);
    }

    @Override
    public PdfDataDto generate(
        GenerateDigitalDocumentDto dto,
        DigitalDocument digitalDocument,
        DigitalDocumentUser digitalDocumentUser
    ) {
        final GenerateHtmlDocumentDto generateHtmlDocumentDto = GenerateHtmlDocumentDto.builder()
            .digitalDocument(digitalDocument)
            .digitalDocumentUser(digitalDocumentUser)
            .signatureImage(dto.getSignatureImage())
            .attachingFilesDto(dto.getAttachingFilesDto())
            .build();

        return uploadAndReturn(generateHtmlDocumentDto, digitalDocument, digitalDocumentUser);
    }

    private PdfDataDto uploadAndReturn(
        GenerateHtmlDocumentDto dto,
        DigitalDocument digitalDocument,
        DigitalDocumentUser digitalDocumentUser
    ) {
        final PdfDataDto pdfDataDto = generateDigitalDocumentPdf(fillWatermarkType(dto, digitalDocument));
        final String uploadedPdfPath = uploadPdf(pdfDataDto, digitalDocument, digitalDocumentUser);

        pdfDataDto.setPath(uploadedPdfPath);

        return pdfDataDto;
    }

    private GenerateHtmlDocumentDto fillWatermarkType(GenerateHtmlDocumentDto dto, DigitalDocument digitalDocument) {
        final AttachingFilesDto attachingFilesDto = dto.getAttachingFilesDto();
        if (attachingFilesDto != null) {
            attachingFilesDto.setIdCardWatermarkType(digitalDocument.getIdCardWatermarkType());
            dto.setAttachingFilesDto(attachingFilesDto);
        }
        return dto;
    }

    private String uploadPdf(PdfDataDto pdfDataDto, DigitalDocument digitalDocument, DigitalDocumentUser digitalDocumentUser) {

        final String originalFilename = digitalDocumentFilenameGenerator.generate(digitalDocument, digitalDocumentUser.getUserId());
        final String fullPath = FilenameUtil.getDigitalDocumentUploadPath(digitalDocument.getId(), originalFilename);

        return digitalDocumentUploadService.uploadPdf(pdfDataDto, fullPath, originalFilename);
    }

    private PdfDataDto generateDigitalDocumentPdf(GenerateHtmlDocumentDto generateHtmlDocumentDto) {

        final PdfDataDto pdfDataDto = digitalDocumentTypeGenerator.generate(generateHtmlDocumentDto);

        final byte[] markAnyDnaPdfBytes = markAnyService.makeDna(
            new MarkAnyDigitalDocument(
                generateHtmlDocumentDto.getDigitalDocument().getId(),
                generateHtmlDocumentDto.getDigitalDocumentUser().getUserId(),
                generateHtmlDocumentDto.getDigitalDocumentUser().getIssuedNumber(),
                pdfDataDto.getPdfBytes()
            )
        );

        pdfDataDto.setPdfBytes(markAnyDnaPdfBytes);
        return pdfDataDto;
    }
}
