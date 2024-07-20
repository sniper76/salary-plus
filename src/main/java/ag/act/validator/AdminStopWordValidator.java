package ag.act.validator;

import ag.act.entity.StopWord;
import ag.act.enums.admin.StopWordActivationType;
import ag.act.exception.BadRequestException;
import ag.act.model.Status;
import ag.act.repository.StopWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static ag.act.enums.ActErrorCode.DUPLICATE_INACTIVE_STOP_WORD_ERROR_CODE;

@Component
@RequiredArgsConstructor
public class AdminStopWordValidator {

    private final StopWordRepository stopWordRepository;

    public void validateDuplicateStopWord(String trimmedWord) {
        final Optional<StopWord> existingStopWordOptional = stopWordRepository.findByWord(trimmedWord);

        if (existingStopWordOptional.isEmpty()) {
            return;
        }

        final StopWord existingStopWord = existingStopWordOptional.get();
        if (existingStopWord.isActive()) {
            throw new BadRequestException("이미 등록된 금칙어입니다.");
        }

        throw new BadRequestException(
            DUPLICATE_INACTIVE_STOP_WORD_ERROR_CODE.getCode(),
            "이미 등록된 비활성화 금칙어입니다. 활성화 하시겠습니까?",
            Map.of("stopWordId", existingStopWord.getId(), "word", existingStopWord.getWord())
        );
    }

    public void validateAlreadyUpdate(StopWord stopWord, Status beforeStatus, Status afterStatus) {
        if (stopWord.getStatus() != beforeStatus) {
            throw new BadRequestException(String.format("이미 %s된 금칙어입니다.", StopWordActivationType.getDisplayNameByStatus(afterStatus)));
        }
    }

    public void validateStopWordStatus(Status beforeStatus, Status afterStatus) {
        if (!StopWordActivationType.containsStopWordStatus(beforeStatus, afterStatus)) {
            throw new BadRequestException("상태를 확인해주세요.");
        }
    }

}
