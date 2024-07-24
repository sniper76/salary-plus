package ag.act.module.digitaldocumentgenerator.document.typegenerator;

import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.ActRuntimeException;
import ag.act.exception.InternalServerException;
import ag.act.module.digitaldocumentgenerator.converter.HolderListReadAndCopyFillConverter;
import ag.act.module.digitaldocumentgenerator.dto.HolderListReadAndCopyGenerateHtmlDocumentDto;
import ag.act.module.digitaldocumentgenerator.dto.HolderListReadAndCopyGenerateHtmlDocumentMaskingDto;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentFill;
import ag.act.module.digitaldocumentgenerator.validator.HolderListReadAndCopyFillValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
            final PdfDataDto pdfDataDto = super.generateDigitalDocumentPdf(dto);

            pdfDataDto.setPdfMaskingBytes(generateMergedMaskingBytes(dto));

            return pdfDataDto;

        } catch (ActRuntimeException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("전자문서 PDF 생성 중에 알 수 없는 오류가 발생하였습니다. {}", e.getMessage(), e);
            throw new InternalServerException("전자문서 PDF 생성 중에 알 수 없는 오류가 발생하였습니다.", e);
        }
    }

    private byte[] generateMergedMaskingBytes(IGenerateHtmlDocumentDto dto) {
        final var maskingDto = new HolderListReadAndCopyGenerateHtmlDocumentMaskingDto((HolderListReadAndCopyGenerateHtmlDocumentDto) dto);
        byte[] pdfMaskingSources = generateDigitalDocumentMaskingPdfBytes(maskingDto);
        return pdfMergerService.mergePdfSources(List.of(pdfMaskingSources));
    }

    private byte[] generateDigitalDocumentMaskingPdfBytes(IGenerateHtmlDocumentDto dto) {
        return pdfRenderService.renderPdf(generateDocumentHtmlMaskingString(dto));
    }

    private String generateDocumentHtmlMaskingString(IGenerateHtmlDocumentDto dto) {
        digitalDocumentFillValidator.validate(dto);

        final DigitalDocumentFill digitalDocumentFill = digitalDocumentFillConverter.convert(dto);

        return digitalDocumentHtmlGenerator.fillAndGetHtmlString(digitalDocumentFill, HOLDER_LIST_READ_AND_COPY_TEMPLATE);
    }
}
