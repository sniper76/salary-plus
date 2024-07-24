package ag.act.module.mydata;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.module.mydata.crypto.MyDataCryptoHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;

@Disabled
@TestPropertySource(properties = {"external.mydata.aes256key=AAAAA"})
class MyDataCryptoHelperRealTest extends AbstractCommonIntegrationTest {
    @Autowired
    private MyDataCryptoHelper myDataCryptoHelper;

    private final List<String> paths = List.of(
        "/Users/yanggun7201/Downloads/박노석(51207)_51207_2024_03_16_15_47_07.json",
        "/Users/yanggun7201/Downloads/박신환(48441)_48441_2024_03_16_10_19_41.json",
        "/Users/yanggun7201/Downloads/서춘수(2817)_2817_2024_03_17_09_51_18.json",
        "/Users/yanggun7201/Downloads/신부경(46716)_46716_2024_03_16_17_42_51.json"
    );

    @SuppressWarnings("CallToPrintStackTrace")
    @Test
    void shouldReturnDecryptedData() {
        paths.forEach(path -> {
            try {
                decryptFile(new File(path));
            } catch (IOException | GeneralSecurityException e) {
                e.printStackTrace();
            }
        });
    }

    private void decryptFile(File file) throws IOException, GeneralSecurityException {
        final String formattedJson = readFile(file);
        final File outputFile = createOutputFile(file);

        FileUtils.writeStringToFile(outputFile, formattedJson, StandardCharsets.UTF_8);
    }

    private File createOutputFile(File file) {
        final String fileName = file.getName();
        final String extension = FilenameUtils.getExtension(fileName);
        final String baseName = FilenameUtils.getBaseName(fileName);

        return new File("%s/%s_decrypted.%s".formatted(file.getParent(), baseName, extension));
    }

    private String readFile(File file) throws IOException, GeneralSecurityException {
        final String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        final String decryptedContent = myDataCryptoHelper.decrypt(fileContent.trim());

        return formattedJson(decryptedContent);
    }

    private String formattedJson(String jsonContent) throws JsonProcessingException {
        final ObjectMapper mapper = createObjectMapper();
        final JsonNode jsonNode = mapper.readTree(jsonContent);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
    }

    private ObjectMapper createObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        return mapper;
    }
}
