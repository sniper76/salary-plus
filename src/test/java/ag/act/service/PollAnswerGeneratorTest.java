package ag.act.service;

import ag.act.dto.poll.PollAnswersByTypeDto;
import ag.act.entity.PollAnswer;
import ag.act.util.PollAnswerGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@MockitoSettings(strictness = Strictness.LENIENT)
class PollAnswerGeneratorTest {
    @InjectMocks
    private PollAnswerGenerator pollAnswerGenerator;

    @Nested
    class WhenPollAnswerMake {

        @Test
        void shouldPollAnswerGenerator() {
            final List<ag.act.model.CreatePollAnswerItemRequest> requestList = new ArrayList<>();
            requestList.add(new ag.act.model.CreatePollAnswerItemRequest()
                .pollItemId(1L));
            requestList.add(new ag.act.model.CreatePollAnswerItemRequest()
                .pollItemId(2L));
            requestList.add(new ag.act.model.CreatePollAnswerItemRequest()
                .pollItemId(3L));

            final List<PollAnswer> answerList = new ArrayList<>();
            PollAnswer pollAnswer1 = new PollAnswer();
            pollAnswer1.setPollItemId(2L);
            pollAnswer1.setStockQuantity(20L);
            PollAnswer pollAnswer2 = new PollAnswer();
            pollAnswer2.setPollItemId(4L);
            pollAnswer2.setStockQuantity(40L);
            answerList.add(pollAnswer1);
            answerList.add(pollAnswer2);

            final PollAnswersByTypeDto result = pollAnswerGenerator.getPollAnswersByTypeDto(requestList, answerList);

            assertThat(result.insertPollAnswers().size(), is(2));
            assertThat(result.matchPollAnswers().size(), is(1));
            assertThat(result.deletePollAnswers().size(), is(1));
        }
    }
}
