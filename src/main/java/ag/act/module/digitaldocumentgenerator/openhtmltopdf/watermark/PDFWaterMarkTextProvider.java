package ag.act.module.digitaldocumentgenerator.openhtmltopdf.watermark;

import ag.act.util.DateTimeFormatUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@RequiredArgsConstructor
@Component
public class PDFWaterMarkTextProvider {
    private static final int WATER_MARK_MIN_LENGTH = 70;
    private static final String DEFAULT_WATER_MARK_TEXT = "ACT";
    private final PDFWaterMarkCurrentDateTimeProvider pdfWaterMarkCurrentDateTimeProvider;

    public String generateWithDateTime() {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatUtil.yyyy_MM_dd_HH_mm();
        final ZonedDateTime koreanDateTime = pdfWaterMarkCurrentDateTimeProvider.getKoreanDateTime();
        final String currentDateTime = dateTimeFormatter.format(koreanDateTime);
        final String baseWaterMarkText = "%s - %s".formatted(DEFAULT_WATER_MARK_TEXT, currentDateTime);

        return createWatermarkText(baseWaterMarkText);
    }

    public String generate() {
        return createWatermarkText(DEFAULT_WATER_MARK_TEXT);
    }

    private String createWatermarkText(final String waterMarkText) {
        final String waterMarkTextToUse = defaultIfBlank(waterMarkText);

        final int textLength = waterMarkTextToUse.length();
        if (textLength > WATER_MARK_MIN_LENGTH) {
            return waterMarkTextToUse;
        }

        final int repeatCount = (int) Math.ceil((double) WATER_MARK_MIN_LENGTH / textLength);

        return String.join(" ", Collections.nCopies(repeatCount, waterMarkTextToUse));
    }

    private String defaultIfBlank(final String waterMarkText) {
        return StringUtils.defaultIfBlank(waterMarkText, DEFAULT_WATER_MARK_TEXT).trim();
    }
}
