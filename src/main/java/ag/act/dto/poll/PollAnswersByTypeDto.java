package ag.act.dto.poll;

import ag.act.entity.PollAnswer;

import java.util.List;

public record PollAnswersByTypeDto(
    List<PollAnswer> insertPollAnswers,
    List<PollAnswer> matchPollAnswers,
    List<PollAnswer> deletePollAnswers
) {
}
