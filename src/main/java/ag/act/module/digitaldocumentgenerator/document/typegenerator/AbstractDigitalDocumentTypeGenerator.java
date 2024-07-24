package ag.act.module.digitaldocumentgenerator.document.typegenerator;

import ag.act.constants.DigitalDocumentTemplateNames;
import ag.act.dto.digitaldocument.PdfDataDto;
import ag.act.enums.digitaldocument.IdCardWatermarkType;
import ag.act.exception.InternalServerException;
import ag.act.module.digitaldocumentgenerator.DigitalDocumentHtmlGenerator;
import ag.act.module.digitaldocumentgenerator.converter.BaseDigitalDocumentFillConverter;
import ag.act.module.digitaldocumentgenerator.dto.AttachingFilesDto;
import ag.act.module.digitaldocumentgenerator.dto.IGenerateHtmlDocumentDto;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.SinglePageImageFill;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.PDFMergerService;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.PDFRenderService;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.watermark.PDFWaterMarkImageAppender;
import ag.act.module.digitaldocumentgenerator.validator.BaseDigitalDocumentFillValidator;
import ag.act.module.mydata.crypto.MyDataCryptoHelper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Transactional
@Slf4j
public abstract class AbstractDigitalDocumentTypeGenerator implements IDigitalDocumentTypeGenerator, DigitalDocumentTemplateNames {
    private final String templateName;
    protected final BaseDigitalDocumentFillValidator digitalDocumentFillValidator;
    protected final BaseDigitalDocumentFillConverter digitalDocumentFillConverter;
    @Autowired
    protected PDFMergerService pdfMergerService;
    @Autowired
    protected DigitalDocumentHtmlGenerator digitalDocumentHtmlGenerator;
    @Autowired
    protected PDFRenderService pdfRenderService;
    @Autowired
    protected PDFWaterMarkImageAppender pdfWaterMarkImageAppender;
    @Autowired
    protected MyDataCryptoHelper myDataCryptoHelper;

    AbstractDigitalDocumentTypeGenerator(
        String templateName,
        BaseDigitalDocumentFillValidator digitalProxyFillValidator,
        BaseDigitalDocumentFillConverter digitalDocumentFillConverter
    ) {
        this.templateName = templateName;
        this.digitalDocumentFillValidator = digitalProxyFillValidator;
        this.digitalDocumentFillConverter = digitalDocumentFillConverter;
    }

    public PdfDataDto generateDigitalDocumentPdf(IGenerateHtmlDocumentDto dto) {
        try {
            List<byte[]> pdfSources = new ArrayList<>();

            final long originalPageCount = addOriginalPdfToSources(pdfSources, dto);
            final long attachmentPageCount = addAttachmentPdfToSources(pdfSources, dto);

            byte[] mergedBytes = pdfMergerService.mergePdfSources(pdfSources);

            return PdfDataDto.builder()
                .pdfBytes(mergedBytes)
                .originalPageCount(originalPageCount)
                .attachmentPageCount(attachmentPageCount)
                .build();
        } catch (IOException e) {
            log.error("전자문서 PDF 생성 중에 알 수 없는 오류가 발생하였습니다. {}", e.getMessage(), e);
            throw new InternalServerException("전자문서 PDF 생성 중에 알 수 없는 오류가 발생하였습니다.", e);
        }
    }

    private long addOriginalPdfToSources(
        List<byte[]> pdfSources, IGenerateHtmlDocumentDto generateHtmlDocumentDto
    ) throws IOException {
        final byte[] originalPdfBytes = generateDigitalDocumentPdfBytes(generateHtmlDocumentDto);
        final long originalPageCount = getPdfPageCount(originalPdfBytes);
        pdfSources.add(originalPdfBytes);

        return originalPageCount;
    }

    private long addAttachmentPdfToSources(
        List<byte[]> pdfSources, IGenerateHtmlDocumentDto generateHtmlDocumentDto
    ) throws IOException {
        if (generateHtmlDocumentDto.getAttachingFilesDto() != null) {
            byte[] attachmentPdfBytes = pdfMergerService.mergePdfSources(
                generateAttachingDocumentPdfBytesList(generateHtmlDocumentDto.getAttachingFilesDto())
            );
            pdfSources.add(attachmentPdfBytes);
            return getPdfPageCount(attachmentPdfBytes);
        }

        return 0;
    }

    private int getPdfPageCount(byte[] pdfBytes) throws IOException {
        try (final PDDocument pdDocument = PDDocument.load(pdfBytes)) {
            return pdDocument.getNumberOfPages();
        }
    }

    private byte[] generateDigitalDocumentPdfBytes(IGenerateHtmlDocumentDto dto) {
        return pdfRenderService.renderPdf(generateDocumentHtmlString(dto));
    }

    private List<byte[]> generateAttachingDocumentPdfBytesList(AttachingFilesDto attachingFilesDto) {
        final List<byte[]> attachingPdfDocuments = new ArrayList<>();

        if (attachingFilesDto.getIdCardImage() != null) {
            attachingPdfDocuments.add(
                generateIdCardPdf(attachingFilesDto.getIdCardImage(), attachingFilesDto.getIdCardWatermarkType())
            );
        }

        if (CollectionUtils.isNotEmpty(attachingFilesDto.getBankAccountImages())) {
            Optional.ofNullable(generateAdditionalPdfArray(attachingFilesDto))
                .ifPresent(attachingPdfDocuments::addAll);
        }

        if (attachingFilesDto.getHectoEncryptedBankAccountPdf() != null) {
            attachingPdfDocuments.add(generateHectoBankAccountPdf(attachingFilesDto.getHectoEncryptedBankAccountPdf()));
        }

        return attachingPdfDocuments;
    }

    private List<byte[]> generateAdditionalPdfArray(AttachingFilesDto dto) {
        return dto.getBankAccountImages().stream()
            .map(this::generateSinglePageHtmlString)
            .map(pdfRenderService::renderPdf)
            .toList();
    }

    private byte[] generateIdCardPdf(MultipartFile idCardImage, IdCardWatermarkType idCardWatermarkType) {
        final byte[] pdfBytes = pdfRenderService.renderPdf(generateSinglePageHtmlString(idCardImage));
        if (isNotValidBytes(pdfBytes)) {
            return pdfBytes;
        }
        if (isNoneIdCardWatermarkType(idCardWatermarkType)) {
            return pdfBytes;
        }

        try {
            return pdfWaterMarkImageAppender.append(pdfBytes, idCardWatermarkType);
        } catch (Exception ex) {
            throw new InternalServerException("신분증을 전자문서에 첨부하는 중 알 수 없는 오류가 발생했습니다.", ex);
        }
    }

    private byte[] generateHectoBankAccountPdf(MultipartFile hectoEncryptedBankAccountPdf) {
        try {
            return Base64.getDecoder().decode(
                myDataCryptoHelper.decrypt(
                    Base64.getEncoder().encodeToString(
                        hectoEncryptedBankAccountPdf.getBytes()
                    )
                )
            );
        } catch (IOException | GeneralSecurityException e) {
            throw new InternalServerException("헥토의 pdf를 복호화하던 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }

    private String generateDocumentHtmlString(IGenerateHtmlDocumentDto dto) {
        digitalDocumentFillValidator.validate(dto);

        final DigitalDocumentFill digitalDocumentFill = digitalDocumentFillConverter.convert(dto);

        return digitalDocumentHtmlGenerator.fillAndGetHtmlString(digitalDocumentFill, this.templateName);
    }

    private String generateSinglePageHtmlString(MultipartFile image) {
        final SinglePageImageFill singlePageImageFill = digitalDocumentFillConverter.convert(image);

        return digitalDocumentHtmlGenerator.fillAndGetHtmlString(singlePageImageFill, SINGLE_PAGE_IMAGE_TEMPLATE);
    }

    private boolean isNotValidBytes(final byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }

    private boolean isNoneIdCardWatermarkType(IdCardWatermarkType idCardWatermarkType) {
        return idCardWatermarkType == IdCardWatermarkType.NONE;
    }
}
