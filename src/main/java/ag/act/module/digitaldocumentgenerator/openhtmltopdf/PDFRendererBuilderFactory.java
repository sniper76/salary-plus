package ag.act.module.digitaldocumentgenerator.openhtmltopdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Component;

@SuppressWarnings("AbbreviationAsWordInName")
@Component
public class PDFRendererBuilderFactory {
    public PdfRendererBuilder create() {
        return new PdfRendererBuilder();
    }
}
