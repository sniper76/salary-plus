package ag.act.service.digitaldocument.answer;

import ag.act.dto.DigitalDocumentUserAnswerDto;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItemUserAnswer;
import ag.act.enums.DigitalDocumentType;
import ag.act.repository.DigitalDocumentItemUserAnswerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DigitalDocumentItemUserAnswerService {
    private final DigitalDocumentItemUserAnswerRepository digitalDocumentItemUserAnswerRepository;
    private final AnswerParser answerParser;
    private final AnswerValidator answerValidator;

    public void deleteByDigitalDocumentUserId(Long digitalDocumentId, Long userId) {
        digitalDocumentItemUserAnswerRepository.deleteAllById(
            getAnswerIds(digitalDocumentId, userId)
        );
    }

    @NotNull
    private List<Long> getAnswerIds(Long digitalDocumentId, Long userId) {
        final List<DigitalDocumentUserAnswerDto> answerList = getUserAnswerList(digitalDocumentId, userId);

        return answerList.stream()
            .map(DigitalDocumentUserAnswerDto::getDigitalDocumentItemAnswerId)
            .toList();
    }

    private List<DigitalDocumentUserAnswerDto> getUserAnswerList(Long digitalDocumentId, Long userId) {
        return digitalDocumentItemUserAnswerRepository.findUserAnswerList(digitalDocumentId, userId);
    }

    public void saveAnswers(String answerData, Long userId, DigitalDocument digitalDocument) {
        if (!isDigitalProxy(digitalDocument) || StringUtils.isBlank(answerData)) {
            return;
        }

        final List<Answer> answers = answerParser.parseAnswers(answerData);

        answerValidator.validateItemsAndAnswers(digitalDocument, answers);

        answers.forEach(
            answer -> digitalDocumentItemUserAnswerRepository.save(
                DigitalDocumentItemUserAnswer.of(userId, answer.itemId(), answer.answerType())
            )
        );
    }

    private boolean isDigitalProxy(DigitalDocument digitalDocument) {
        return digitalDocument.getType() == DigitalDocumentType.DIGITAL_PROXY;
    }

    public Map<Long, List<DigitalDocumentItemUserAnswer>> getUserAnswersMapByUserId(
        Long digitalDocumentId,
        List<Long> userIds
    ) {
        return getAllDigitalDocumentItemUserAnswers(digitalDocumentId, userIds)
            .stream()
            .collect(Collectors.groupingBy(DigitalDocumentItemUserAnswer::getUserId));
    }

    private List<DigitalDocumentItemUserAnswer> getAllDigitalDocumentItemUserAnswers(Long digitalDocumentId, List<Long> userIds) {
        return digitalDocumentItemUserAnswerRepository.findAllByDigitalDocumentIdAndUserIdInOrderByUserIdAscOrderByIdAsc(
            digitalDocumentId,
            userIds
        );
    }
}
