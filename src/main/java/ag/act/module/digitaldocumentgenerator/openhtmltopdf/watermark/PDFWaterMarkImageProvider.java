package ag.act.module.digitaldocumentgenerator.openhtmltopdf.watermark;

import ag.act.module.digitaldocumentgenerator.openhtmltopdf.font.PDFFontProvider;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class PDFWaterMarkImageProvider {
    private static final float IMAGE_MAX_WIDTH = 100;
    private static final float IMAGE_MAX_HEIGHT = 30;

    private static final int FONT_SIZE = 140;
    //    private static final Font MONOSPACED = new Font("Monospaced", Font.PLAIN, FONT_SIZE);
    private static final Color TEXT_COLOR = new Color(41, 75, 186);
    private static final int TEXT_HEIGHT = FONT_SIZE;
    private static final int ONE_LETTER_WITH = (int) (FONT_SIZE * 0.66);
    private static final int SPACE_WITH = ONE_LETTER_WITH;

    private final Resource actLogo;
    private final PDFFontProvider pdfFontProvider;

    public PDFWaterMarkImageProvider(
        @Value("classpath:/templates/images/watermark/act-logo-v1.png") Resource actLogo,
        PDFFontProvider pdfFontProvider
    ) {
        this.actLogo = actLogo;
        this.pdfFontProvider = pdfFontProvider;
    }

    public PDFWaterMarkImage get(final PDDocument pdDocument) throws IOException {
        return getPdfWaterMarkImage(getPdImageXObject(pdDocument));
    }

    public PDFWaterMarkImage get(final PDDocument pdDocument, String text) throws IOException {
        return getPdfWaterMarkImage(getPdImageXObject(pdDocument, text));
    }

    private PDFWaterMarkImage getPdfWaterMarkImage(PDImageXObject pdImage) {
        float originalWidth = pdImage.getWidth();
        float originalHeight = pdImage.getHeight();
        float aspectRatio = originalWidth / originalHeight;

        float newWidth = IMAGE_MAX_WIDTH;
        float newHeight = IMAGE_MAX_HEIGHT;

        if (originalHeight > IMAGE_MAX_HEIGHT) {
            newHeight = IMAGE_MAX_HEIGHT;
            newWidth = IMAGE_MAX_HEIGHT * aspectRatio;
        }

        return new PDFWaterMarkImage(pdImage, newWidth, newHeight);
    }

    private ByteArrayOutputStream makeLogoImageWithText(String text) throws IOException {
        BufferedImage newImage = createNewLogoImageWithText(text);

        return toByteArrayOutputStream(newImage);
    }

    private ByteArrayOutputStream toByteArrayOutputStream(BufferedImage newImage) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(newImage, "png", byteArrayOutputStream);
        return byteArrayOutputStream;
    }

    private BufferedImage createNewLogoImageWithText(String text) throws IOException {
        final byte[] contentAsByteArray = actLogo.getContentAsByteArray();
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(contentAsByteArray));
        int imageWidth = originalImage.getWidth();
        int totalImageHeight = originalImage.getHeight();
        int textWidth = (text.length() * ONE_LETTER_WITH) - SPACE_WITH;
        int totalImageWidth = imageWidth + SPACE_WITH + textWidth;
        int textX = imageWidth + SPACE_WITH;
        int textY = (totalImageHeight / 2) + (TEXT_HEIGHT / 2);

        BufferedImage newImage = new BufferedImage(totalImageWidth, totalImageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = newImage.createGraphics();
        try {
            drawImage(g2d, originalImage);
            drawText(g2d, text, textX, textY);
        } finally {
            g2d.dispose();
        }

        return newImage;
    }

    private void drawImage(Graphics2D g2d, BufferedImage originalImage) {
        g2d.drawImage(originalImage, 0, 0, null);
    }

    private void drawText(Graphics2D g2d, String text, int textX, int textY) throws IOException {
        try {
            g2d.setFont(getFont());
            g2d.setColor(TEXT_COLOR);
            g2d.drawString(text, textX, textY);
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private Font getFont() throws IOException, FontFormatException {
        Font pdfFont = Font.createFont(Font.TRUETYPE_FONT, pdfFontProvider.getInputStream());
        return pdfFont.deriveFont(Font.PLAIN, FONT_SIZE);
    }

    private PDImageXObject getPdImageXObject(final PDDocument pdDocument) throws IOException {
        return PDImageXObject.createFromByteArray(pdDocument, actLogo.getContentAsByteArray(), actLogo.getFilename());
    }

    private PDImageXObject getPdImageXObject(final PDDocument pdDocument, String text) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = makeLogoImageWithText(text);
        return PDImageXObject.createFromByteArray(pdDocument, byteArrayOutputStream.toByteArray(), actLogo.getFilename() + text);
    }
}
