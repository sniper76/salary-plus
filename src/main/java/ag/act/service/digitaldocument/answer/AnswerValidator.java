package ag.act.service.digitaldocument.answer;

import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.repository.DigitalDocumentItemRepository;
import ag.act.validator.document.DigitalDocumentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnswerValidator {
    private final DigitalDocumentItemRepository digitalDocumentItemRepository;
    private final DigitalDocumentValidator digitalDocumentValidator;

    public void validateItemsAndAnswers(DigitalDocument digitalDocument, List<Answer> answers) {
        List<Long> answerItemIds = getAnswerItemIds(answers);

        validateRequestAnswerItemCount(digitalDocument, answerItemIds);
        validateAnswerItemCount(answerItemIds);
    }

    private void validateAnswerItemCount(List<Long> answerItemIds) {
        int itemCount = digitalDocumentItemRepository.countByIdInAndIsLastItem(answerItemIds, true);
        digitalDocumentValidator.validateAnswerItemCount(itemCount, answerItemIds.size());
    }

    private void validateRequestAnswerItemCount(DigitalDocument digitalDocument, List<Long> answerIds) {
        int itemCount = digitalDocumentItemRepository.countByDigitalDocumentIdAndIsLastItem(digitalDocument.getId(), true);
        digitalDocumentValidator.validateRequestAnswerItemCount(itemCount, answerIds.size());
    }

    private List<Long> getAnswerItemIds(List<Answer> answers) {
        return answers.stream()
            .map(Answer::itemId)
            .toList();
    }
}
