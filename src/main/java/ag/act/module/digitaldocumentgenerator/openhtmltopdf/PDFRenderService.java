package ag.act.module.digitaldocumentgenerator.openhtmltopdf;

import ag.act.exception.InternalServerException;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.font.PDFFontDto;
import ag.act.module.digitaldocumentgenerator.openhtmltopdf.font.PDFFontProvider;
import ag.act.service.digitaldocument.DigitalDocumentTemplatePathService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.slf4j.Slf4jLogger;
import com.openhtmltopdf.util.XRLog;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static ag.act.util.XHTMLFormatUtil.convertHtmlToXhtml;

@SuppressWarnings("AbbreviationAsWordInName")
@Service
public class PDFRenderService {
    private final PDFRendererBuilderFactory pdfRendererBuilderFactory;
    private final DigitalDocumentTemplatePathService digitalDocumentTemplatePathService;
    private final PDFFontProvider pdfFontProvider;

    public PDFRenderService(
        PDFRendererBuilderFactory pdfRendererBuilderFactory,
        DigitalDocumentTemplatePathService digitalDocumentTemplatePathService,
        PDFFontProvider pdfFontProvider
    ) {
        this.digitalDocumentTemplatePathService = digitalDocumentTemplatePathService;
        this.pdfRendererBuilderFactory = pdfRendererBuilderFactory;
        this.pdfFontProvider = pdfFontProvider;
        XRLog.setLoggerImpl(new Slf4jLogger());
    }

    public byte[] renderPdf(String htmlString) {
        try {
            return doRenderPdf(convertHtmlToXhtml(htmlString));
        } catch (IOException e) {
            throw new InternalServerException("Failed to render pdf", e);
        }
    }

    private byte[] doRenderPdf(String xhtmlString) throws IOException {
        PdfRendererBuilder builder = pdfRendererBuilderFactory.create();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        final PDFFontDto pdfFontDto = pdfFontProvider.getPDFFontDto();
        try (final InputStream fontStream = pdfFontDto.inputStream()) {
            builder.useFont(() -> fontStream, pdfFontDto.fontFamily())
                .withHtmlContent(xhtmlString, digitalDocumentTemplatePathService.getPath())
                .toStream(os)
                .run();
        }

        return os.toByteArray();
    }
}
