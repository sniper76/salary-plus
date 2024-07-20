package ag.act.converter.digitaldocument;

import ag.act.enums.DigitalDocumentType;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.module.digitaldocumentgenerator.model.DigitalDocumentFill;
import ag.act.module.digitaldocumentgenerator.model.OtherDocumentFill;
import org.springframework.stereotype.Component;

@Component
public class OtherDocumentPreviewFillConverter
    implements DigitalDocumentPreviewFillConverter<PreviewDigitalDocumentRequest, DigitalDocumentFill> {
    @Override
    public boolean canConvert(DigitalDocumentType type) {
        return type == DigitalDocumentType.ETC_DOCUMENT;
    }

    private OtherDocumentFill convert(PreviewDigitalDocumentRequest request) {
        OtherDocumentFill otherDocumentFill = new OtherDocumentFill();

        otherDocumentFill.setTitle(request.getTitle());
        otherDocumentFill.setContent(request.getContent());

        return otherDocumentFill;
    }

    @Override
    public OtherDocumentFill apply(PreviewDigitalDocumentRequest request) {
        return convert(request);
    }
}
