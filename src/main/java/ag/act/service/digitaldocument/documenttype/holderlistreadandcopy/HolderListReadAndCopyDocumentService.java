package ag.act.service.digitaldocument.documenttype.holderlistreadandcopy;

import ag.act.converter.digitaldocument.DigitalDocumentRequestCommonConverter;
import ag.act.dto.download.DownloadFile;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.IDigitalDocument;
import ag.act.entity.digitaldocument.IHolderListReadAndCopyDocument;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.model.UserDigitalDocumentResponse;
import ag.act.repository.DigitalDocumentRepository;
import ag.act.service.digitaldocument.documenttype.DigitalDocumentTypeService;
import ag.act.util.DateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class HolderListReadAndCopyDocumentService implements DigitalDocumentTypeService<IHolderListReadAndCopyDocument> {

    private final DigitalDocumentRepository digitalDocumentRepository;
    private final DigitalDocumentRequestCommonConverter digitalDocumentRequestCommonConverter;
    private final HolderListReadAndCopyItemService holderListReadAndCopyItemService;

    @Override
    public boolean supports(DigitalDocumentType type) {
        return type == DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT;
    }

    @Override
    public DigitalDocument create(Post post, CreateDigitalDocumentRequest createRequest) {
        DigitalDocument digitalDocument = digitalDocumentRequestCommonConverter.convert(post, createRequest);
        digitalDocument.setTitle(createRequest.getTitle());
        digitalDocument.setTargetStartDate(DateTimeUtil.getTodayLocalDateTime());
        digitalDocument.setTargetEndDate(DateTimeUtil.getTodayLocalDateTime());
        DigitalDocument savedDigitalDocument = digitalDocumentRepository.save(digitalDocument);

        holderListReadAndCopyItemService.saveItems(savedDigitalDocument.getId(), createRequest.getHolderListReadAndCopyItems());

        return savedDigitalDocument;
    }

    @Override
    public IDigitalDocument update(IHolderListReadAndCopyDocument digitalDocument) {
        return null;
    }

    @Override
    public UserDigitalDocumentResponse getPostResponseDigitalDocument(Post post, User currentUser, Long stockCount) {
        return null;
    }

    @Override
    public DownloadFile preview(PreviewDigitalDocumentRequest previewRequest) {
        return null;
    }
}
