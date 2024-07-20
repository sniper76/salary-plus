package ag.act.converter;

import ag.act.dto.HolderListReadAndCopyDigitalDocumentResponseData;
import ag.act.model.HolderListReadAndCopyDigitalDocumentResponse;
import org.springframework.stereotype.Component;

@Component
public class HolderListReadAndCopyDigitalDocumentResponseConverter {

    public HolderListReadAndCopyDigitalDocumentResponse convert(HolderListReadAndCopyDigitalDocumentResponseData responseData) {
        return new HolderListReadAndCopyDigitalDocumentResponse()
            .digitalDocumentId(responseData.digitalDocumentId())
            .fileName(responseData.fileNameWithExtension())
            .userId(responseData.userId());
    }
}
