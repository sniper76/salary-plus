package ag.act.module.digitaldocumentgenerator.document;

import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.dto.file.FilenameDto;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentType;
import ag.act.module.digitaldocumentgenerator.document.typegenerator.DigitalDocumentTypeGenerator;
import ag.act.module.digitaldocumentgenerator.dto.GenerateDigitalDocumentDto;
import ag.act.module.digitaldocumentgenerator.dto.HolderListReadAndCopyGenerateHtmlDocumentDto;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@SuppressWarnings("AbbreviationAsWordInName")
class HolderListReadAndCopyDigitalDocumentGenerator implements IDigitalDocumentGenerator {

    private final DigitalDocumentTypeGenerator digitalDocumentTypeGenerator;
    private final HolderListReadAndCopyDigitalDocumentFilenameProvider filenameProvider;
    private final DigitalDocumentUploadService digitalDocumentUploadService;

    @Override
    public boolean supports(DigitalDocumentType digitalDocumentType) {
        return isHolderListReadAndCopy(digitalDocumentType);
    }

    @Override
    public PdfDataDto generate(
        GenerateDigitalDocumentDto dto,
        DigitalDocument digitalDocument,
        DigitalDocumentUser digitalDocumentUser
    ) {
        final HolderListReadAndCopyGenerateHtmlDocumentDto generateHtmlDocumentDto = HolderListReadAndCopyGenerateHtmlDocumentDto.builder()
            .digitalDocument(digitalDocument)
            .digitalDocumentUser(digitalDocumentUser)
            .signatureImage(dto.getSignatureImage())
            .attachingFilesDto(dto.getAttachingFilesDto())
            .holderListReadAndCopyDataModel(dto.getHolderListReadAndCopyDataModel())
            .holderListReadAndCopyMaskingDataModel(dto.getHolderListReadAndCopyMaskingDataModel())
            .build();

        return uploadAndReturn(generateHtmlDocumentDto, digitalDocument);
    }

    private PdfDataDto uploadAndReturn(
        HolderListReadAndCopyGenerateHtmlDocumentDto dto,
        DigitalDocument digitalDocument
    ) {
        final PdfDataDto pdfDataDto = generateDigitalDocumentPdf(dto);
        final FilenameDto filenameDto = getFilenameDto(dto, digitalDocument);

        pdfDataDto.setPath(uploadPdf(pdfDataDto, filenameDto));

        uploadSignImageFile(dto, filenameDto);

        return pdfDataDto;
    }

    private PdfDataDto generateDigitalDocumentPdf(IGenerateHtmlDocumentDto generateHtmlDocumentDto) {
        return digitalDocumentTypeGenerator.generate(generateHtmlDocumentDto);
    }

    private FilenameDto getFilenameDto(HolderListReadAndCopyGenerateHtmlDocumentDto dto, DigitalDocument digitalDocument) {
        return filenameProvider.getFilenameDto(
            digitalDocument,
            dto.getHolderListReadAndCopyDataModel().getReferenceDateByLeader()
        );
    }

    private String uploadPdf(PdfDataDto pdfDataDto, FilenameDto filenameDto) {
        return digitalDocumentUploadService.uploadPdf(pdfDataDto, filenameDto.fullPath(), filenameDto.filename());
    }

    public void uploadSignImageFile(
        HolderListReadAndCopyGenerateHtmlDocumentDto dto,
        FilenameDto filenameDto
    ) {
        final String path = FilenameUtils.getPath(filenameDto.fullPath());
        final String originalFilename = "%s_서명이미지.png".formatted(filenameDto.baseName());
        final String fullPath = "%s%s".formatted(path, originalFilename);

        digitalDocumentUploadService.uploadMultipartFile(dto.getSignatureImage(), fullPath, originalFilename);
    }
}
