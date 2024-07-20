package ag.act.module.digitaldocumentgenerator.openhtmltopdf.watermark;

import ag.act.util.ZoneIdUtil;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class PDFWaterMarkCurrentDateTimeProvider {

    public ZonedDateTime getKoreanDateTime() {
        return ZonedDateTime.now(ZoneIdUtil.getSeoulZoneId());
    }
}
