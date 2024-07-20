package ag.act.converter.digitaldocument;

import ag.act.converter.Converter;
import ag.act.enums.DigitalDocumentType;

public interface DigitalDocumentPreviewFillConverter<I, O> extends Converter<I, O> {
    default boolean canConvert(DigitalDocumentType type) {
        return false;
    }
}
