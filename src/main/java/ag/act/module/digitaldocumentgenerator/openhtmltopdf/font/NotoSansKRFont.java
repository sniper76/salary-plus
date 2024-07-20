package ag.act.module.digitaldocumentgenerator.openhtmltopdf.font;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Component
public class NotoSansKRFont {
    private static final String FONT_FAMILY = "NotoSansKR";
    private final Resource notoSansKrFontResource;

    public NotoSansKRFont(
        @Value("classpath:/fonts/NotoSansKR.ttf") Resource notoSansKrFontResource
    ) {
        this.notoSansKrFontResource = notoSansKrFontResource;
    }

    public PDFFontDto get() throws IOException {
        return new PDFFontDto(notoSansKrFontResource.getInputStream(), FONT_FAMILY);
    }

    public File getFile() throws IOException {
        return notoSansKrFontResource.getFile();
    }

    public InputStream getInputStream() throws IOException {
        return notoSansKrFontResource.getInputStream();
    }
}
