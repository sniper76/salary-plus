package ag.act.module.dart;

import ag.act.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
@RequiredArgsConstructor
public class DartCorpCodeXmlReader {
    private static final String XML_FILE_NAME = "CORPCODE.xml";

    public String convert(InputStream zipInputStream) {
        try (
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(zipInputStream));
            ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (!XML_FILE_NAME.equals(entry.getName())) {
                    continue;
                }

                byte[] buffer = new byte[1024];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }

                return baos.toString(StandardCharsets.UTF_8);
            }

            throw new InternalServerException("Not found %s zip file from dart".formatted(XML_FILE_NAME));
        } catch (IOException e) {
            throw new InternalServerException("Error occurred during read %s zip file of dart".formatted(XML_FILE_NAME), e);
        }
    }
}