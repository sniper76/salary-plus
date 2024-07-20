package ag.act.service.digitaldocument.documenttype.holderlistreadandcopy;

import ag.act.model.HolderListReadAndCopyItemRequest;
import ag.act.repository.HolderListReadAndCopyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class HolderListReadAndCopyItemService {

    private final HolderListReadAndCopyRepository holderListReadAndCopyRepository;
    private final HolderListReadAndCopyRequestConverter holderListReadAndCopyRequestConverter;

    public void saveItems(Long digitalDocumentId, List<HolderListReadAndCopyItemRequest> requests) {
        holderListReadAndCopyRepository.saveAll(
            holderListReadAndCopyRequestConverter.convert(digitalDocumentId, requests)
        );
    }
}
