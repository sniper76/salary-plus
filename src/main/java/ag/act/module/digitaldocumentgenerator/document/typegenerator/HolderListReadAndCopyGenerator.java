package ag.act.module.digitaldocumentgenerator.document.typegenerator;

import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.dto.user.HolderListReadAndCopyDataModel;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.InternalServerException;
import ag.act.module.digitaldocumentgenerator.converter.HolderListReadAndCopyFillConverter;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import ag.act.module.digitaldocumentgenerator.dto.IHolderListReadAndCopyGenerateHtmlDocumentDto;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentFill;
import ag.act.module.digitaldocumentgenerator.validator.HolderListReadAndCopyFillValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class HolderListReadAndCopyGenerator extends AbstractDigitalDocumentTypeGenerator {

    HolderListReadAndCopyGenerator(
        HolderListReadAndCopyFillValidator holderListReadAndCopyFillValidator,
        HolderListReadAndCopyFillConverter holderListReadAndCopyFillConverter
    ) {
        super(
            HOLDER_LIST_READ_AND_COPY_TEMPLATE,
            holderListReadAndCopyFillValidator,
            holderListReadAndCopyFillConverter
        );
    }

    @Override
    public boolean supports(DigitalDocumentType type) {
        return DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT == type;
    }

    @Override
    public PdfDataDto generateDigitalDocumentPdf(IGenerateHtmlDocumentDto dto) {
        try {
            List<byte[]> pdfSources = new ArrayList<>();
            final long originalPageCount = addOriginalPdfToSources(pdfSources, dto);
            final long attachmentPageCount = addAttachmentPdfToSources(pdfSources, dto);
            byte[] mergedBytes = pdfMergerService.mergePdfSources(pdfSources);


            List<byte[]> pdfMaskingSources = new ArrayList<>();
            addOriginalPdfToMaskingSources(pdfMaskingSources, dto);
            byte[] mergedMaskingBytes = pdfMergerService.mergePdfSources(pdfMaskingSources);

            return PdfDataDto.builder()
                .pdfBytes(mergedBytes)
                .pdfMaskingBytes(mergedMaskingBytes)
                .originalPageCount(originalPageCount)
                .attachmentPageCount(attachmentPageCount)
                .build();
        } catch (IOException e) {
            log.error("전자문서 PDF 생성 중에 알 수 없는 오류가 발생하였습니다. {}", e.getMessage(), e);
            throw new InternalServerException("전자문서 PDF 생성 중에 알 수 없는 오류가 발생하였습니다.", e);
        }
    }

    private void addOriginalPdfToMaskingSources(
        List<byte[]> pdfSources, IGenerateHtmlDocumentDto generateHtmlDocumentDto
    ) throws IOException {
        final byte[] originalPdfBytes = generateDigitalDocumentMaskingPdfBytes(generateHtmlDocumentDto);
        pdfSources.add(originalPdfBytes);
    }

    private byte[] generateDigitalDocumentMaskingPdfBytes(IGenerateHtmlDocumentDto dto) {
        return pdfRenderService.renderPdf(generateDocumentHtmlMaskingString(dto));
    }

    private String generateDocumentHtmlMaskingString(IGenerateHtmlDocumentDto dto) {
        digitalDocumentFillValidator.validate(dto);

        final HolderListReadAndCopyDataModel holderListReadAndCopyDataModel =
            ((IHolderListReadAndCopyGenerateHtmlDocumentDto) dto).getHolderListReadAndCopyDataModel();
        holderListReadAndCopyDataModel.setLeaderName("******");
        holderListReadAndCopyDataModel.setLeaderAddress("******");
        holderListReadAndCopyDataModel.setLeaderEmail("******");
        ((IHolderListReadAndCopyGenerateHtmlDocumentDto) dto).setHolderListReadAndCopyDataModel(holderListReadAndCopyDataModel);

        final DigitalDocumentFill digitalDocumentFill = digitalDocumentFillConverter.convert(dto);

        return digitalDocumentHtmlGenerator.fillAndGetHtmlString(digitalDocumentFill, HOLDER_LIST_READ_AND_COPY_TEMPLATE);
    }
}
