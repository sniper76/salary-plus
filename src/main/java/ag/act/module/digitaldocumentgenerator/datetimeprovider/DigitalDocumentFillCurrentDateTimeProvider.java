package ag.act.module.digitaldocumentgenerator.datetimeprovider;

import ag.act.util.KoreanDateTimeUtil;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class DigitalDocumentFillCurrentDateTimeProvider {

    public ZonedDateTime getKoreanDateTime() {
        return KoreanDateTimeUtil.getNowInKoreanTime();
    }
}
