package ag.act.service.stockboardgrouppost;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.HolderListReadAndCopyDigitalDocumentResponseConverter;
import ag.act.converter.post.BoardGroupPostResponseConverter;
import ag.act.converter.post.PostDetailResponseConverter;
import ag.act.converter.post.PostUpdateResponseConverter;
import ag.act.datacollector.HolderListReadAndCopyDigitalDocumentResponseDataCollector;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.dto.PostDetailsParamDto;
import ag.act.dto.post.CreatePostRequestDto;
import ag.act.dto.post.DeletePostRequestDto;
import ag.act.dto.post.UpdatePostRequestDto;
import ag.act.entity.FileContent;
import ag.act.entity.PollAnswer;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.entity.User;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.BoardGroup;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.model.HolderListReadAndCopyDigitalDocumentResponse;
import ag.act.model.PostDataResponse;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.PostResponse;
import ag.act.repository.interfaces.PollItemCount;
import ag.act.service.BoardService;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.poll.PollAnswerService;
import ag.act.service.post.PostImageService;
import ag.act.service.post.PostService;
import ag.act.service.post.PostUserViewService;
import ag.act.service.push.PushService;
import ag.act.service.solidarity.election.SolidarityLeaderElectionService;
import ag.act.util.StatusUtil;
import ag.act.validator.post.StockBoardGroupPostValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class StockBoardGroupPostService {
    private final StockBoardGroupPostCreateService stockBoardGroupPostCreateService;
    private final StockBoardGroupPostUpdateService stockBoardGroupPostUpdateService;
    private final StockBoardGroupPostDeleteService stockBoardGroupPostDeleteService;
    private final BoardService boardService;
    private final PostService postService;
    private final PostImageService postImageService;
    private final BoardGroupPostResponseConverter boardGroupPostResponseConverter;
    private final PostDetailResponseConverter postDetailResponseConverter;
    private final PostUserViewService postUserViewService;
    private final PollAnswerService pollAnswerService;
    private final DigitalDocumentService digitalDocumentService;
    private final StockBoardGroupPostValidator stockBoardGroupPostValidator;
    private final PostUpdateResponseConverter postUpdateResponseConverter;
    private final PushService pushService;
    private final BlockedUserService blockedUserService;
    private final SolidarityLeaderElectionService solidarityLeaderElectionService;
    private final HolderListReadAndCopyDigitalDocumentResponseConverter holderListReadAndCopyDigitalDocumentResponseConverter;
    private final HolderListReadAndCopyDigitalDocumentResponseDataCollector holderListReadAndCopyDigitalDocumentResponseDataCollector;

    public ag.act.model.SimpleStringResponse deleteBoardGroupPost(DeletePostRequestDto deletePostRequestDto) {
        return stockBoardGroupPostDeleteService.deleteBoardGroupPost(deletePostRequestDto);
    }

    public PostDetailsDataResponse createBoardGroupPost(CreatePostRequestDto createPostRequestDto) {
        return stockBoardGroupPostCreateService.createBoardGroupPost(createPostRequestDto);
    }

    public PostDetailsDataResponse updateBoardGroupPost(UpdatePostRequestDto updatePostRequestDto) {
        return stockBoardGroupPostUpdateService.updateBoardGroupPost(updatePostRequestDto);
    }

    public GetBoardGroupPostResponse getBoardGroupPosts(GetBoardGroupPostDto getBoardGroupPostDto, PageRequest pageRequest) {
        if (getBoardGroupPostDto.isValidBoardCategory()) {
            return getBoardGroupPostsByCategory(getBoardGroupPostDto, pageRequest);
        } else {
            return getBoardGroupPostsByAllCategories(getBoardGroupPostDto, pageRequest);
        }
    }

    private GetBoardGroupPostResponse getBoardGroupPostsByAllCategories(
        GetBoardGroupPostDto getBoardGroupPostDto,
        PageRequest pageRequest
    ) {
        final String stockCode = getBoardGroupPostDto.getStockCode();
        final BoardGroup boardGroup = BoardGroup.fromValue(getBoardGroupPostDto.getBoardGroupName());

        final List<Long> blockedUserIdList = blockedUserService.getBlockUserIdListOfMine();
        final Page<Post> boardPosts = postService.getBoardPostsByStockCodeAndBoardGroup(
            stockCode,
            boardGroup,
            blockedUserIdList,
            StatusUtil.getStatusesForPostList(getBoardGroupPostDto.getIsNotDeleted()),
            pageRequest
        );

        return boardGroupPostResponseConverter.convert(boardPosts);
    }


    private GetBoardGroupPostResponse getBoardGroupPostsByCategory(
        GetBoardGroupPostDto getBoardGroupPostDto,
        PageRequest pageRequest
    ) {
        final List<Long> boardIds = boardService.getBoardIdsByStockCodeAndCategoryIn(
            getBoardGroupPostDto.getStockCode(),
            getBoardGroupPostDto.getBoardCategories()
        );
        final List<Long> blockedUserIdList = blockedUserService.getBlockUserIdListOfMine();
        final Page<Post> boardPosts = postService.getBoardPosts(
            boardIds,
            blockedUserIdList,
            StatusUtil.getStatusesForPostList(getBoardGroupPostDto.getIsNotDeleted()),
            pageRequest
        );

        return boardGroupPostResponseConverter.convert(boardPosts);
    }

    public PostDataResponse getBoardGroupPostDetailWithUpdateViewCount(Long postId, String stockCode, String boardGroupName) {

        final Post post = postService.getPostNotDeleted(postId);
        stockBoardGroupPostValidator.validateBoardGroupAndStockCodeOfBoardGroupPost(post, stockCode, boardGroupName);

        updatePostUserViewCount(postId);
        Post updatedPost = updatePostViewAndViewUserCount(post);

        return getBoardGroupPostDetail(updatedPost);
    }

    public PostDataResponse getBoardGroupPostDetail(Post post) {
        final List<FileContent> postImages = postImageService.getFileContentsByPostId(post.getId());
        if (post.isDeletedByUser()) {
            return postDetailResponseConverter.convert(post, postImages);
        }

        PostResponse postResponse = getNotDeletedPostResponse(post, postImages);

        return postDetailResponseConverter.convert(postResponse);
    }

    private PostResponse getNotDeletedPostResponse(Post post, List<FileContent> postImages) {
        final Long postId = post.getId();
        final Optional<User> userOptional = ActUserProvider.get();
        final Map<Long, List<PollItemCount>> voteItemListMap = pollAnswerService.getVoteItemListMap(postId);
        final Map<Long, List<PollAnswer>> answerListMap = userOptional
            .map(user -> pollAnswerService.getAnswerListMap(postId, user.getId()))
            .orElseGet(Map::of);

        PostResponse postResponse = postDetailResponseConverter.convertWithAnswer(
            post, voteItemListMap, answerListMap, postImages
        );

        if (userOptional.isPresent()) {
            postResponse.digitalDocument(digitalDocumentService.getPostResponseDigitalDocument(post, userOptional.get()));
            postResponse.holderListReadAndCopyDigitalDocument(getHolderListReadAndCopyDigitalDocument(post));
        }

        return postResponse;
    }

    private HolderListReadAndCopyDigitalDocumentResponse getHolderListReadAndCopyDigitalDocument(Post post) {
        if (!post.isHolderListReadAndCopyDocumentType()) {
            return null;
        }

        return holderListReadAndCopyDigitalDocumentResponseDataCollector.collect(post.getDigitalDocument())
            .map(holderListReadAndCopyDigitalDocumentResponseConverter::convert)
            .orElse(null);
    }

    private void updatePostUserViewCount(Long postId) {
        try {
            postUserViewService.createOrUpdatePostUserViewCount(postId);
        } catch (Exception e) {
            log.error("Failed to upsert post user view count. postId: {}", postId, e);
            // ignore exception
        }
    }

    private Post updatePostViewAndViewUserCount(Post post) {
        Long viewCount = postUserViewService.sumViewCountByPostId(post.getId());
        Long viewUserCount = postUserViewService.countByPostId(post.getId());

        post.setViewCount(viewCount);
        post.setViewUserCount(viewUserCount);

        return postService.savePost(post);
    }

    @SuppressWarnings("unused")
    public PostDetailsDataResponse getPostDetails(Long postId, String stockCode, String boardGroupName) {
        final Post post = postService.getPostNotDeleted(postId);
        final List<FileContent> postImages = postImageService.getFileContentsByPostId(postId);
        final Optional<SolidarityLeaderElection> election = solidarityLeaderElectionService.findSolidarityLeaderElectionByPostId(postId);
        final Map<Long, List<PollAnswer>> answerListMap = election.isPresent() ? Map.of() : pollAnswerService.getAllAnswerListMap(postId);
        final Map<Long, List<PollItemCount>> voteItemListMap = election.isPresent() ? Map.of() : pollAnswerService.getVoteItemListMap(postId);
        final Optional<Push> push = findPush(post.getPushId());
        final HolderListReadAndCopyDigitalDocumentResponse holderListReadAndCopyDigitalDocumentResponse =
            getHolderListReadAndCopyDigitalDocument(post);

        final PostDetailsResponse postDetailsResponse = postUpdateResponseConverter.convertForAdmin(
            PostDetailsParamDto.builder(
                    post,
                    post.getBoard(),
                    postImages,
                    post.getPostUserProfile()
                )
                .stock(post.getBoard().getStock())
                .push(push.orElse(null))
                .voteItemMap(voteItemListMap)
                .answerMap(answerListMap)
                .solidarityLeaderElection(election.orElse(null))
                .holderListReadAndCopyDigitalDocumentResponse(holderListReadAndCopyDigitalDocumentResponse)
                .build()
        );
        return new PostDetailsDataResponse()
            .data(
                postDetailsResponse
            );
    }

    private Optional<Push> findPush(Long pushId) {
        return Optional.ofNullable(pushId)
            .flatMap(pushService::findPush);
    }
}
