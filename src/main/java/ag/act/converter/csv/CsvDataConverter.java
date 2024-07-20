package ag.act.converter.csv;

import ag.act.converter.DecryptColumnConverter;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CsvDataConverter {
    private final DecryptColumnConverter decryptColumnConverter;

    public CsvDataConverter(DecryptColumnConverter decryptColumnConverter) {
        this.decryptColumnConverter = decryptColumnConverter;
    }

    public String getUpdatedAtInKoreanTime(DigitalDocumentUser digitalDocumentUser) {
        return DateTimeUtil.getFormattedKoreanTime(
            "yyyy-MM-dd HH:mm:ss",
            KoreanDateTimeUtil.toKoreanTime(digitalDocumentUser.getUpdatedAt()).toInstant()
        );
    }

    public String getPhoneNumber(DigitalDocumentUser digitalDocumentUser) {
        return getPhoneNumber(digitalDocumentUser.getHashedPhoneNumber());
    }

    public String getPhoneNumber(String hashedPhoneNumber) {
        return decryptColumnConverter.convert(hashedPhoneNumber)
            .replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
    }

    public String getGender(DigitalDocumentUser digitalDocumentUser) {
        return ag.act.model.Gender.M == digitalDocumentUser.getGender() ? "남" : "여";
    }

    public String getStringNumber(Long longValue) {
        return Optional.ofNullable(longValue).map(Object::toString).orElse("0");
    }

    public String getString(String stringValue) {
        return Optional.ofNullable(stringValue).orElse("").replaceAll(",", " ");
    }

    public String getBirthday(LocalDateTime birthDate) {
        return DateTimeUtil.formatLocalDateTime(birthDate, "yyyy/MM/dd");
    }

    public String getRegisteredAt(LocalDateTime registeredAt) {
        return DateTimeUtil.getFormattedKoreanTime(
            "yyyy-MM-dd",
            KoreanDateTimeUtil.toKoreanTime(registeredAt).toInstant()
        );
    }
}
