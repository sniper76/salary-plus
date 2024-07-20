package ag.act.entity;

import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.DigitalDocumentType;

public interface PostExtension {

    DigitalDocument getDigitalDocument();

    default boolean isHolderListReadAndCopyDocumentType() {
        return getDigitalDocument() != null && getDigitalDocument().getType() == DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT;
    }
}
