package ag.act.service;

import ag.act.converter.admin.StopWordResponseConverter;
import ag.act.dto.SimplePageDto;
import ag.act.entity.StopWord;
import ag.act.enums.admin.StopWordActivationType;
import ag.act.exception.NotFoundException;
import ag.act.model.Status;
import ag.act.model.StopWordResponse;
import ag.act.model.UpdateStopWordRequest;
import ag.act.repository.StopWordRepository;
import ag.act.validator.AdminStopWordValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StopWordService {

    private final StopWordRepository stopWordRepository;
    private final StopWordResponseConverter stopWordResponseConverter;
    private final AdminStopWordValidator adminStopWordValidator;

    public boolean containsStopWord(String word) {
        return stopWordRepository.findAllByStatusIs(Status.ACTIVE)
            .stream()
            .anyMatch(stopWord -> word.contains(stopWord.getWord()));
    }

    public SimplePageDto<StopWordResponse> getStopWords(String filterType, String searchKeyword, PageRequest pageRequest) {
        final List<Status> statusList = StopWordActivationType.fromValue(filterType).getStatusList();
        return new SimplePageDto<>(
            getStopWordPage(statusList, searchKeyword, pageRequest)
                .map(stopWordResponseConverter::convert)
        );
    }

    private Page<StopWord> getStopWordPage(List<Status> statusList, String searchKeyword, PageRequest pageRequest) {
        if (StringUtils.isNotBlank(searchKeyword)) {
            return stopWordRepository.findAllByStatusInAndWordContaining(statusList, searchKeyword, pageRequest);
        }
        return stopWordRepository.findAllByStatusIn(statusList, pageRequest);
    }

    public StopWordResponse create(String word) {
        final String trimmedWord = word.trim();

        adminStopWordValidator.validateDuplicateStopWord(trimmedWord);

        final StopWord stopWord = new StopWord(trimmedWord, Status.ACTIVE);
        final StopWord savedStopWord = stopWordRepository.save(stopWord);

        return stopWordResponseConverter.convert(savedStopWord);
    }

    public StopWordResponse update(Long stopWordId, UpdateStopWordRequest updateStopWordRequest) {
        final StopWord stopWord = getStopWord(stopWordId);

        final Status beforeStatus = Status.fromValue(updateStopWordRequest.getBeforeStatus().toUpperCase());
        final Status afterStatus = Status.fromValue(updateStopWordRequest.getAfterStatus().toUpperCase());
        adminStopWordValidator.validateStopWordStatus(beforeStatus, afterStatus);
        adminStopWordValidator.validateAlreadyUpdate(stopWord, beforeStatus, afterStatus);

        stopWord.setStatus(afterStatus);
        return stopWordResponseConverter.convert(stopWordRepository.save(stopWord));
    }

    public void delete(Long stopWordId) {
        final StopWord stopWord = getStopWord(stopWordId);

        stopWordRepository.delete(stopWord);
    }

    private StopWord getStopWord(Long stopWordId) {
        return stopWordRepository.findById(stopWordId).orElseThrow(
            () -> new NotFoundException("존재하지 않는 금칙어입니다.")
        );
    }

}
