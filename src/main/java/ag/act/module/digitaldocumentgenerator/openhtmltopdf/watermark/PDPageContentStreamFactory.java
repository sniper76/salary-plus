package ag.act.module.digitaldocumentgenerator.openhtmltopdf.watermark;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PDPageContentStreamFactory {

    public PDPageContentStream create(PDDocument pdDocument, PDPage page) throws IOException {
        return create(pdDocument, page, PDPageContentStream.AppendMode.APPEND);
    }

    public PDPageContentStream create(PDDocument pdDocument, PDPage page, PDPageContentStream.AppendMode appendMode) throws IOException {
        return new PDPageContentStream(pdDocument, page, appendMode, true, true);
    }
}
