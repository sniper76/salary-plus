package ag.act.converter.post.poll;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.PollAnswer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PollAnswerResponseConverter {

    public List<ag.act.model.PollAnswerResponse> convert(List<PollAnswer> answerList) {
        if (answerList.isEmpty()) {
            return Collections.emptyList();
        }

        return answerList.stream()
            .map(element -> new ag.act.model.PollAnswerResponse()
                .id(element.getId())
                .pollId(element.getPollId())
                .userId(element.getUserId())
                .pollItemId(element.getPollItemId())
                .status(element.getStatus().name())
                .stockQuantity(element.getStockQuantity())
                .createdAt(DateTimeConverter.convert(element.getCreatedAt()))
                .updatedAt(DateTimeConverter.convert(element.getUpdatedAt())))
            .toList();
    }
}
