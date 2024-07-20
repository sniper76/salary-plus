package ag.act.service.digitaldocument;

import ag.act.dto.download.DownloadFile;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.module.digitaldocumentgenerator.DigitalDocumentHtmlGenerator;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.PDFRenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DigitalDocumentPreviewService {
    private final DigitalDocumentHtmlGenerator digitalDocumentHtmlGenerator;
    private final PDFRenderService pdfRenderService;
    private final DigitalDocumentPreviewFillService digitalDocumentPreviewFillService;

    public DownloadFile createPreviewDocument(PreviewDigitalDocumentRequest previewRequest) {
        String htmlString = digitalDocumentHtmlGenerator.fillAndGetHtmlString(
            digitalDocumentPreviewFillService.fill(previewRequest),
            templateNameOf(previewRequest)
        );

        byte[] pdfBytes = pdfRenderService.renderPdf(htmlString);

        return createPreviewFile(pdfBytes);
    }

    private String templateNameOf(PreviewDigitalDocumentRequest previewRequest) {
        return DigitalDocumentType.valueOf(previewRequest.getType()).getTemplateName();
    }

    private DownloadFile createPreviewFile(byte[] pdfBytes) {
        return DownloadFile
            .builder()
            .resource(new ByteArrayResource(pdfBytes))
            .fileName("digital-document-preview.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .build();
    }
}
