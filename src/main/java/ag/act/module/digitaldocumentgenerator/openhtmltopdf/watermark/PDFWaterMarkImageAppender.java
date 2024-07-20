package ag.act.module.digitaldocumentgenerator.openhtmltopdf.watermark;

import ag.act.enums.digitaldocument.IdCardWatermarkType;
import ag.act.util.DateTimeFormatUtil;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Component
public class PDFWaterMarkImageAppender {
    private static final int ANGLE_DEGREES = 45;
    private static final int DEFAULT_PAGE_INDEX = 0;
    private static final int MAX_REPEAT_COUNT = 20;
    private static final int HORIZONTAL_SPACE = 20;
    private static final int VERTICAL_SPACE = 40;
    private static final int START_Y = 500;
    private static final int BOTTOM_PAGE_WIDTH = 1000;
    private static final int BOTTOM_PAGE_HEIGHT = 120;
    private static final int BOTTOM_PAGE_X = -50;
    private static final int BOTTOM_PAGE_Y = 0;
    private static final float BOTTOM_PAGE_ALPHA = 1f;
    private static final float WATER_MARK_ALPHA = 0.2f;
    private final PDPageContentStreamFactory pdPageContentStreamFactory;
    private final PDFWaterMarkImageProvider pdfWaterMarkImageProvider;
    private final PDFWaterMarkCurrentDateTimeProvider pdfWaterMarkCurrentDateTimeProvider;

    public byte[] append(final byte[] pdfBytes, IdCardWatermarkType idCardWatermarkType) throws IOException {

        try (final PDDocument pdDocument = PDDocument.load(pdfBytes)) {
            final PDPage page = pdDocument.getPage(DEFAULT_PAGE_INDEX);

            try (PDPageContentStream contentStream = pdPageContentStreamFactory.create(pdDocument, page)) {
                appendWaterMark(pdDocument, contentStream, idCardWatermarkType);
                writeBottomMargin(contentStream);
            }

            return getBytes(pdDocument);
        }
    }

    private void appendWaterMark(
        final PDDocument pdDocument,
        final PDPageContentStream contentStream,
        IdCardWatermarkType idCardWatermarkType
    ) throws IOException {
        final PDFWaterMarkImage pdfWaterMarkImage = getPDFWaterMarkImage(idCardWatermarkType, pdDocument);
        final PDImageXObject pdImage = pdfWaterMarkImage.pdImage();
        float imageWith = pdfWaterMarkImage.width();
        float imageHeight = pdfWaterMarkImage.height();

        PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
        graphicsState.setNonStrokingAlphaConstant(WATER_MARK_ALPHA);
        contentStream.setGraphicsStateParameters(graphicsState);

        float startY = START_Y;
        for (int column = 0; column < MAX_REPEAT_COUNT; column++) {
            float startX = 0;

            for (int row = 0; row < MAX_REPEAT_COUNT; row++) {
                contentStream.saveGraphicsState();
                contentStream.transform(
                    org.apache.pdfbox.util.Matrix.getRotateInstance(Math.toRadians(ANGLE_DEGREES), 0, startY)
                );
                contentStream.drawImage(pdImage, startX + (row * imageWith), startY, imageWith, imageHeight);
                contentStream.restoreGraphicsState();
                startX += HORIZONTAL_SPACE;
            }

            startY -= VERTICAL_SPACE;
        }
    }

    private PDFWaterMarkImage getPDFWaterMarkImage(IdCardWatermarkType idCardWatermarkType, PDDocument pdDocument) throws IOException {
        if (idCardWatermarkType == IdCardWatermarkType.ACT_LOGO_WITH_DATE) {
            return pdfWaterMarkImageProvider.get(pdDocument, getFormattedDate());
        }
        return pdfWaterMarkImageProvider.get(pdDocument);
    }

    private String getFormattedDate() {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatUtil.yyyy_MM_dd_HH_mm();
        final ZonedDateTime koreanDateTime = pdfWaterMarkCurrentDateTimeProvider.getKoreanDateTime();
        return dateTimeFormatter.format(koreanDateTime);
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
