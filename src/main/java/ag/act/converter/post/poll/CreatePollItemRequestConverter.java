package ag.act.converter.post.poll;

import ag.act.entity.Poll;
import ag.act.entity.PollItem;
import ag.act.model.CreatePollRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CreatePollItemRequestConverter {

    public List<PollItem> covert(CreatePollRequest createPostRequestPoll, Poll poll) {
        return createPostRequestPoll.getPollItems()
            .stream()
            .map(requestPollItem -> {
                PollItem pollItem = new PollItem();
                pollItem.setText(requestPollItem.getText());
                pollItem.setStatus(ag.act.model.Status.ACTIVE);

                pollItem.setPoll(poll);
                return pollItem;
            })
            .toList();
    }

}
