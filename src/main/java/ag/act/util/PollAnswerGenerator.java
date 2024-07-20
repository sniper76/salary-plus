package ag.act.util;

import ag.act.dto.poll.PollAnswersByTypeDto;
import ag.act.entity.PollAnswer;
import ag.act.model.CreatePollAnswerItemRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class PollAnswerGenerator {

    public PollAnswersByTypeDto getPollAnswersByTypeDto(
        List<CreatePollAnswerItemRequest> pollAnswerItemRequests,
        List<PollAnswer> existingPollAnswers
    ) {

        List<PollAnswer> leftValues = new ArrayList<>();
        List<PollAnswer> rightValues = new ArrayList<>();
        List<PollAnswer> matchValues = new ArrayList<>();

        for (CreatePollAnswerItemRequest itemRequest : pollAnswerItemRequests) {
            boolean found = false;
            for (PollAnswer pollAnswer : existingPollAnswers) {
                if (Objects.equals(itemRequest.getPollItemId(), pollAnswer.getPollItemId())) {
                    matchValues.add(pollAnswer);

                    found = true;
                    break;
                }
            }
            if (!found) {
                PollAnswer newPollAnswer = new PollAnswer();
                newPollAnswer.setPollItemId(itemRequest.getPollItemId());
                leftValues.add(newPollAnswer);
            }
        }

        for (PollAnswer item2 : existingPollAnswers) {
            boolean found = false;
            for (CreatePollAnswerItemRequest item1 : pollAnswerItemRequests) {
                if (Objects.equals(item2.getPollItemId(), item1.getPollItemId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                rightValues.add(item2);
            }
        }

        return new PollAnswersByTypeDto(
            leftValues,
            matchValues,
            rightValues
        );
    }
}
