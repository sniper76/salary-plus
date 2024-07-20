package ag.act.itutil;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PdfTestHelper {
    public static void assertPdfTextEquals(byte[] actual, File expected) {
        try (PDDocument actualPdf = PDDocument.load(actual);
             PDDocument expectedPdf = PDDocument.load(expected)) {

            assertPdfTextEquals(actualPdf, expectedPdf);
        } catch (IOException e) {
            throw new RuntimeException("[TEST] 파일을 찾을 수 없습니다.", e);
        }
    }

    private static void assertPdfTextEquals(PDDocument actualPdf, PDDocument expectedPdf) {
        try {
            PDFTextStripper pdfStripper = new PDFTextStripper();

            String actualText = pdfStripper.getText(actualPdf);
            String expectedText = pdfStripper.getText(expectedPdf);

            assertEquals(expectedText.trim(), actualText.trim());

        } catch (IOException e) {
            throw new RuntimeException("[TEST] 파일을 찾을 수 없습니다.", e);
        }
    }

    public static void assertPdfBytesEqualsToFile(byte[] actual, File expected) {
        try (PDDocument actualPdf = PDDocument.load(actual);
             PDDocument expectedPdf = PDDocument.load(expected)) {

            assertEquals(expectedPdf.getNumberOfPages(), actualPdf.getNumberOfPages());
            assertPdfTextEquals(actualPdf, expectedPdf);

            for (int i = 0; i < actualPdf.getNumberOfPages(); i++) {
                assertPdfResourcesEquals(actualPdf.getPage(i), expectedPdf.getPage(i));
            }

        } catch (IOException e) {
            throw new RuntimeException("[TEST] PDF 파일 처리 중 오류가 발생했습니다.", e);
        }
    }

    private static void assertPdfResourcesEquals(PDPage actualPage, PDPage expectedPage) throws IOException {
        assertEquals(actualPage.getResources().getXObjectNames(), expectedPage.getResources().getXObjectNames());
        for (COSName name : actualPage.getResources().getXObjectNames()) {
            PDXObject actualObject = actualPage.getResources().getXObject(name);
            PDXObject expectedObject = expectedPage.getResources().getXObject(name);

            if (actualObject instanceof PDImageXObject && expectedObject instanceof PDImageXObject) {
                assertPdfImageEquals((PDImageXObject) actualObject, (PDImageXObject) expectedObject);
            } else {
                throw new RuntimeException("[TEST] 예측하지 못한 PDF 리소스 타입이 포함되어있습니다.");
            }
        }
    }

    private static void assertPdfImageEquals(PDImageXObject actualImageObject, PDImageXObject expectedImageObject) {
        try {
            assertBufferedImageEquals(actualImageObject.getImage(), expectedImageObject.getImage());
        } catch (IOException e) {
            throw new RuntimeException("[TEST] PDF 이미지 처리 중 오류가 발생했습니다.", e);
        }
    }

    public static void assertBufferedImageEquals(BufferedImage img1, BufferedImage img2) {
        assertEquals(img1.getWidth(), img2.getWidth());
        assertEquals(img1.getHeight(), img2.getHeight());
        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                assertEquals(
                    img1.getRGB(x, y),
                    img2.getRGB(x, y),
                    "x: %s, y: %s, expected: %s, actual: %s".formatted(x, y, img1.getRGB(x, y), img2.getRGB(x, y))
                );
            }
        }
    }

    public static String readFileToString(String filePath) {
        try {
            return Files.readString(Paths.get(filePath), UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }
    }

    // for local test file generation. please do not delete.
    @SuppressWarnings("unused")
    public static void generatePdfFile(byte[] contentAsByteArray, String name) {
        try (FileOutputStream fos = new FileOutputStream(name)) {
            fos.write(contentAsByteArray);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // for local test file generation. please do not delete.
    @SuppressWarnings("unused")
    public static void generateBufferedImageFile(BufferedImage bufferedImage, String outputDir) {
        try {
            String randomName = UUID.randomUUID().toString();
            File outputFile = new File(outputDir, randomName + ".png");

            ImageIO.write(bufferedImage, "PNG", outputFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
