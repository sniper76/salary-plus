package ag.act.converter.post;

import ag.act.converter.image.SimpleImageResponseConverter;
import ag.act.converter.post.poll.PollResponseConverter;
import ag.act.entity.FileContent;
import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.Post;
import ag.act.model.PollResponse;
import ag.act.model.PostDataResponse;
import ag.act.repository.interfaces.PollItemCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PostDetailResponseConverter {
    private final PostResponseConverter postResponseConverter;
    private final SimpleImageResponseConverter simpleImageResponseConverter;
    private final PollResponseConverter pollResponseConverter;

    public ag.act.model.PostDataResponse convert(ag.act.model.PostResponse postResponse) {
        return new PostDataResponse()
            .data(postResponse);
    }

    public ag.act.model.PostDataResponse convert(Post post, List<FileContent> postImages) {
        return convert(
            postResponseConverter.convert(post)
            .postImageList(simpleImageResponseConverter.convert(postImages))
        );
    }

    public ag.act.model.PostResponse convertWithAnswer(
        Post post,
        Map<Long, List<PollItemCount>> voteItemCountMap,
        Map<Long, List<PollAnswer>> userAnswerMap,
        List<FileContent> postImages
    ) {
        return postResponseConverter.convert(post)
            .postImageList(simpleImageResponseConverter.convert(postImages))
            .poll(convertPollWithAnswer(
                post.getFirstPoll(),
                voteItemCountMap,
                userAnswerMap,
                post.getId()
            ))
            .polls(convertPollWithAnswer(
                post.getPolls(),
                voteItemCountMap,
                userAnswerMap,
                post.getId()
            ));
    }

    public PollResponse convertPollWithAnswer(
        Poll poll,
        Map<Long, List<PollItemCount>> voteItemCountMap,
        Map<Long, List<PollAnswer>> userAnswerMap,
        Long postId
    ) {
        if (poll == null) {
            return null;
        }
        return pollResponseConverter.convertWithAnswer(
            poll,
            voteItemCountMap.getOrDefault(poll.getId(), List.of()),
            userAnswerMap.getOrDefault(poll.getId(), List.of()),
            postId
        );
    }

    public List<PollResponse> convertPollWithAnswer(
        List<Poll> polls,
        Map<Long, List<PollItemCount>> voteItemCountMap,
        Map<Long, List<PollAnswer>> userAnswerMap,
        Long postId
    ) {
        if (polls == null) {
            return null;
        }
        return pollResponseConverter.convertWithAnswer(
            polls,
            voteItemCountMap,
            userAnswerMap,
            postId
        );
    }
}
