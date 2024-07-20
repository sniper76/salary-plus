package ag.act.module.digitaldocumentgenerator.util;

import ag.act.exception.InternalServerException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

public class MultipartFileToBase64Util {
    public static String convertMultipartFileToBase64(MultipartFile file) {
        final byte[] fileContent = getFileBytes(file);

        String encodedString = encodeFileToBase64(fileContent);

        return createDataUrl(file.getContentType(), encodedString);
    }

    private static byte[] getFileBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception e) {
            throw new InternalServerException("Failed to get bytes from file", e);
        }
    }

    private static String encodeFileToBase64(byte[] fileContent) {
        return Base64.getEncoder().encodeToString(fileContent);
    }

    private static String createDataUrl(String contentType, String encodedString) {
        return "data:" + contentType + ";base64," + encodedString;
    }
}
