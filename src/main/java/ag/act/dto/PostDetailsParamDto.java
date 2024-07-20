package ag.act.dto;

import ag.act.entity.Board;
import ag.act.entity.FileContent;
import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.Post;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.model.HolderListReadAndCopyDigitalDocumentResponse;
import ag.act.repository.interfaces.PollItemCount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Builder(builderMethodName = "internalBuilder")
public class PostDetailsParamDto {
    private Post post;
    private Board board;
    private Push push;
    private SolidarityLeaderElection solidarityLeaderElection;
    private HolderListReadAndCopyDigitalDocumentResponse holderListReadAndCopyDigitalDocumentResponse;

    @Builder.Default
    private Boolean liked = Boolean.FALSE;
    @Builder.Default
    private List<FileContent> fileContents = new ArrayList<>();
    @Builder.Default
    private Map<Long, List<PollItemCount>> voteItemMap = new HashMap<>();
    @Builder.Default
    private Map<Long, List<PollAnswer>> answerMap = new HashMap<>();
    private PostUserProfile postUserProfile;
    private Stock stock;

    public static PostDetailsParamDtoBuilder builder(
        Post post,
        Board board,
        List<FileContent> fileContents,
        PostUserProfile postUserProfile
    ) {
        return internalBuilder()
            .post(post)
            .board(board)
            .fileContents(fileContents)
            .postUserProfile(postUserProfile);
    }

    public List<PollItemCount> getFirstVoteItemList(Poll poll) {
        if (voteItemMap.isEmpty() || poll == null) {
            return List.of();
        }
        return voteItemMap.get(poll.getId());
    }

    public List<PollAnswer> getFirstPollAnswerList(Poll poll) {
        if (answerMap.isEmpty() || poll == null) {
            return List.of();
        }
        return answerMap.get(poll.getId());
    }
}
