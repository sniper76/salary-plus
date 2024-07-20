package ag.act.module.digitaldocumentgenerator.openhtmltopdf.font;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Component
public class PDFFontProvider {
    private final NotoSansKRFont notoSansKRFont;

    public PDFFontDto getPDFFontDto() throws IOException {
        return notoSansKRFont.get();
    }

    public PDType0Font loadFont(PDDocument pdDocument) throws IOException {
        return PDType0Font.load(pdDocument, getInputStream());
    }

    public InputStream getInputStream() throws IOException {
        return notoSansKRFont.getInputStream();
    }
}
