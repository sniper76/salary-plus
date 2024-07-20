package ag.act.module.digitaldocumentgenerator.openhtmltopdf.watermark;

import ag.act.module.digitaldocumentgenerator.openhtmltopdf.font.PDFFontProvider;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class PDFWaterMarkTextAppender {
    private static final int FONT_SIZE = 22;
    private static final int POINT_X = 40;
    private static final int START_Y_OFFSET = 100;
    private static final int WATER_MARK_MAX_COUNT = 10;
    private static final int WATER_MARK_TEXT_SPACE = 160;
    private static final int ANGLE_DEGREES = 45;
    private static final int DEFAULT_PAGE_INDEX = 0;
    private static final int BOTTOM_PAGE_WIDTH = 1000;
    private static final int BOTTOM_PAGE_HEIGHT = 120;
    private static final int BOTTOM_PAGE_X = -50;
    private static final int BOTTOM_PAGE_Y = 0;
    private static final float BOTTOM_PAGE_ALPHA = 1f;
    private static final float WATER_MARK_ALPHA = 0.3f;
    private final PDFFontProvider pdfFontProvider;
    private final PDPageContentStreamFactory pdPageContentStreamFactory;

    public byte[] append(final byte[] pdfBytes, final String waterMarkText) throws IOException {

        try (final PDDocument pdDocument = PDDocument.load(pdfBytes)) {

            final PDPage page = pdDocument.getPage(DEFAULT_PAGE_INDEX);
            final PDRectangle pdRectangle = page.getMediaBox();

            try (PDPageContentStream contentStream = pdPageContentStreamFactory.create(pdDocument, page)) {
                appendWaterMark(pdDocument, pdRectangle, contentStream, waterMarkText);

                writeBottomMargin(contentStream);
            }

            return getBytes(pdDocument);
        }
    }

    private void appendWaterMark(
        final PDDocument pdDocument,
        final PDRectangle pdRectangle,
        final PDPageContentStream contentStream,
        final String waterMarkText
    ) throws IOException {
        final PDType0Font pdType0Font = pdfFontProvider.loadFont(pdDocument);
        float startY = pdRectangle.getUpperRightY() - START_Y_OFFSET;

        PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
        graphicsState.setNonStrokingAlphaConstant(WATER_MARK_ALPHA);
        contentStream.setGraphicsStateParameters(graphicsState);

        for (int i = DEFAULT_PAGE_INDEX; i < WATER_MARK_MAX_COUNT; i++) {
            contentStream.beginText();
            contentStream.setFont(pdType0Font, FONT_SIZE);
            contentStream.setNonStrokingColor(Color.black);
            contentStream.setTextMatrix(
                org.apache.pdfbox.util.Matrix.getRotateInstance(Math.toRadians(ANGLE_DEGREES), POINT_X, startY)
            );
            contentStream.showText(waterMarkText);
            contentStream.endText();
            startY -= WATER_MARK_TEXT_SPACE;
        }
    }

    private byte[] getBytes(final PDDocument pdDocument) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        pdDocument.save(output);
        return output.toByteArray();
    }

    private void writeBottomMargin(final PDPageContentStream contentStream) throws IOException {
        PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
        graphicsState.setNonStrokingAlphaConstant(BOTTOM_PAGE_ALPHA);
        contentStream.setGraphicsStateParameters(graphicsState);
        contentStream.setNonStrokingColor(Color.white);
        contentStream.addRect(BOTTOM_PAGE_X, BOTTOM_PAGE_Y, BOTTOM_PAGE_WIDTH, BOTTOM_PAGE_HEIGHT);
        contentStream.fill();
    }
}
