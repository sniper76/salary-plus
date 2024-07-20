package ag.act.service.digitaldocument;

import ag.act.entity.digitaldocument.HolderListReadAndCopy;
import ag.act.repository.HolderListReadAndCopyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class HolderListReadAndCopyService {
    private final HolderListReadAndCopyRepository holderListReadAndCopyRepository;

    public List<HolderListReadAndCopy> getHolderListReadAndCopyList(Long digitalDocumentId) {
        return holderListReadAndCopyRepository.findAllByDigitalDocumentId(digitalDocumentId);
    }

    public HolderListReadAndCopyListWrapper getHolderListReadAndCopyListWrapper(Long digitalDocumentId) {
        return new HolderListReadAndCopyListWrapper(getHolderListReadAndCopyList(digitalDocumentId));
    }
}
