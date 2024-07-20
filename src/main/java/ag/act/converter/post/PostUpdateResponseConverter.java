package ag.act.converter.post;

import ag.act.converter.DateTimeConverter;
import ag.act.converter.digitaldocument.DigitalDocumentResponseConverter;
import ag.act.converter.digitaldocument.DigitalProxyResponseConverter;
import ag.act.converter.election.SolidarityLeaderElectionResponseConverter;
import ag.act.converter.image.SimpleImageResponseConverter;
import ag.act.converter.post.poll.PollResponseConverter;
import ag.act.converter.push.PushResponseConverter;
import ag.act.converter.stock.StockResponseConverter;
import ag.act.converter.user.UserProfileResponseForDetailsConverter;
import ag.act.dto.PostDetailsParamDto;
import ag.act.entity.Board;
import ag.act.entity.FileContent;
import ag.act.entity.PollAnswer;
import ag.act.entity.Post;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.BoardCategory;
import ag.act.model.DigitalDocumentResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.Status;
import ag.act.repository.interfaces.PollItemCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class PostUpdateResponseConverter {
    private final BoardCategoryResponseConverter boardCategoryResponseConverter;
    private final PushResponseConverter pushResponseConverter;
    private final SimpleImageResponseConverter simpleImageResponseConverter;
    private final UserProfileResponseForDetailsConverter userProfileResponseConverter;
    private final PollResponseConverter pollResponseConverter;
    private final DigitalProxyResponseConverter digitalProxyResponseConverter;
    private final DigitalDocumentResponseConverter digitalDocumentResponseConverter;
    private final StockResponseConverter stockResponseConverter;
    private final SolidarityLeaderElectionResponseConverter solidarityLeaderElectionResponseConverter;

    public PostDetailsResponse convertForAdmin(PostDetailsParamDto postDetailsParamDto) {
        return convert(postDetailsParamDto);
    }

    public PostDetailsResponse convert(
        Post post,
        Push push,
        Board board,
        List<FileContent> fileContents,
        PostUserProfile postUserProfile
    ) {
        return convert(
            PostDetailsParamDto.builder(post, board, fileContents, postUserProfile)
                .liked(Boolean.FALSE)
                .push(push)
                .build()
        );
    }

    public PostDetailsResponse convert(
        Post post,
        Push push,
        Board board,
        Boolean liked,
        List<FileContent> fileContents,
        PostUserProfile postUserProfile
    ) {
        return convert(
            PostDetailsParamDto.builder(post, board, fileContents, postUserProfile)
                .liked(liked)
                .push(push)
                .build()
        );
    }

    public PostDetailsResponse convert(
        Post post,
        Board board,
        List<FileContent> fileContents,
        PostUserProfile postUserProfile
    ) {
        return convert(
            PostDetailsParamDto.builder(post, board, fileContents, postUserProfile)
                .liked(Boolean.FALSE)
                .build()
        );
    }

    public PostDetailsResponse convert(
        Post post,
        Board board,
        Boolean liked,
        List<FileContent> fileContents,
        PostUserProfile postUserProfile
    ) {
        return convert(
            PostDetailsParamDto.builder(post, board, fileContents, postUserProfile)
                .liked(liked)
                .build()
        );
    }

    public PostDetailsResponse convert(
        PostDetailsParamDto postDetailsParamDto
    ) {
        final Post post = postDetailsParamDto.getPost();
        final Push push = postDetailsParamDto.getPush();
        final Stock stock = postDetailsParamDto.getStock();
        final Board board = postDetailsParamDto.getBoard();
        final Boolean liked = postDetailsParamDto.getLiked();
        final PostUserProfile postUserProfile = postDetailsParamDto.getPostUserProfile();
        final List<FileContent> fileContents = postDetailsParamDto.getFileContents();
        final SolidarityLeaderElection election = postDetailsParamDto.getSolidarityLeaderElection();
        final List<PollAnswer> answerList = postDetailsParamDto.getFirstPollAnswerList(post.getFirstPoll());
        final List<PollItemCount> voteItemList = postDetailsParamDto.getFirstVoteItemList(post.getFirstPoll());
        final Map<Long, List<PollAnswer>> answerMap = postDetailsParamDto.getAnswerMap();
        final Map<Long, List<PollItemCount>> voteItemMap = postDetailsParamDto.getVoteItemMap();

        final PostDetailsResponse response = new PostDetailsResponse()
            .id(post.getId())
            .boardId(board.getId())
            .boardGroup(board.getGroup().name())
            .boardCategory(boardCategoryResponseConverter.convert(board))
            .title(post.getTitle())
            .content(post.getContent())
            .thumbnailImageUrl(post.getThumbnailImageUrl())
            .status(post.getStatus())
            .viewCount(post.getViewCount())
            .commentCount(post.getCommentCount())
            .liked(liked)
            .userProfile(userProfileResponseConverter.convert(post.getUserId(), postUserProfile))
            .isPush(post.getPushId() != null)
            .isActive(Status.ACTIVE == post.getStatus())
            .isNotification(post.getIsNotification())
            .isExclusiveToHolders(post.getIsExclusiveToHolders())
            .activeStartDate(DateTimeConverter.convert(post.getActiveStartDate()))
            .postImageList(simpleImageResponseConverter.convert(fileContents))
            .activeStartDate(DateTimeConverter.convert(post.getActiveStartDate()))
            .activeEndDate(DateTimeConverter.convert(post.getActiveEndDate()))
            .createdAt(DateTimeConverter.convert(post.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(post.getUpdatedAt()))
            .push(push == null ? null : pushResponseConverter.convert(push))
            .stock(stock == null ? null : stockResponseConverter.convert(stock))
            .digitalProxy(digitalProxyResponseConverter.convert(post.getDigitalProxy()))
            .poll(election != null ? null : pollResponseConverter.convertWithAnswer(post.getFirstPoll(), voteItemList, answerList, post.getId()))
            .polls(election != null ? null : pollResponseConverter.convertWithAnswer(post.getPolls(), voteItemMap, answerMap, post.getId()))
            .election(election == null ? null : solidarityLeaderElectionResponseConverter.convert(stock.getCode(), election));

        if (board.getCategory() == BoardCategory.HOLDER_LIST_READ_AND_COPY) {
            response.holderListReadAndCopyDigitalDocument(
                postDetailsParamDto.getHolderListReadAndCopyDigitalDocumentResponse()
            );
        } else {
            response.setDigitalDocument(convertDigitalDocument(post.getDigitalDocument()));
        }

        return response;
    }

    private DigitalDocumentResponse convertDigitalDocument(DigitalDocument digitalDocument) {
        if (digitalDocument == null) {
            return null;
        }

        return digitalDocumentResponseConverter.convert(
            digitalDocument, digitalDocument.getDigitalDocumentItemList()
        );
    }
}
