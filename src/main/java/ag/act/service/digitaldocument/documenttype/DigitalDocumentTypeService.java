package ag.act.service.digitaldocument.documenttype;

import ag.act.dto.download.DownloadFile;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.IDigitalDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.model.UserDigitalDocumentResponse;

public interface DigitalDocumentTypeService<T extends IDigitalDocument> {

    boolean supports(DigitalDocumentType type);

    IDigitalDocument create(Post post, CreateDigitalDocumentRequest createDigitalDocumentRequest);

    IDigitalDocument update(T digitalDocument);

    UserDigitalDocumentResponse getPostResponseDigitalDocument(
        Post post, User currentUser, Long stockCount
    );

    DownloadFile preview(PreviewDigitalDocumentRequest previewRequest);
}
