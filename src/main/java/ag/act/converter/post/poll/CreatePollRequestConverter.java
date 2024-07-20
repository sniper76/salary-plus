package ag.act.converter.post.poll;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.Poll;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.enums.SelectionOption;
import ag.act.enums.VoteType;
import ag.act.model.CreatePollRequest;
import ag.act.model.Status;
import ag.act.validator.PollValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CreatePollRequestConverter {
    private final PollValidator pollValidator;
    private final CreatePollItemRequestConverter createPollItemRequestConverter;

    public List<Poll> convert(List<CreatePollRequest> pollRequests, Post post) {
        return pollRequests.stream()
            .map(this::convert)
            .peek(poll -> poll.setPost(post))
            .toList();
    }

    public Poll convert(CreatePollRequest createPollRequest) {
        pollValidator.validate(createPollRequest);

        final Poll poll = new Poll();

        poll.setStatus(Status.ACTIVE);
        poll.setTitle(createPollRequest.getTitle());
        poll.setContent(createPollRequest.getContent());
        poll.setVoteType(VoteType.valueOf(createPollRequest.getVoteType()));
        poll.setSelectionOption(SelectionOption.valueOf(createPollRequest.getSelectionOption()));
        poll.setTargetStartDate(DateTimeConverter.convert(createPollRequest.getTargetStartDate()));
        poll.setTargetEndDate(DateTimeConverter.convert(createPollRequest.getTargetEndDate()));
        poll.setPollItemList(convertToPollItems(createPollRequest, poll));

        return poll;
    }

    private List<PollItem> convertToPollItems(CreatePollRequest createPostRequestPoll, Poll poll) {
        return createPollItemRequestConverter.covert(createPostRequestPoll, poll);
    }

}
