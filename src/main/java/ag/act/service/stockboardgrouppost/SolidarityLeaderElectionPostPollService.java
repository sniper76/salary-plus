package ag.act.service.stockboardgrouppost;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.DateTimeConverter;
import ag.act.converter.post.CreatePostRequestConverter;
import ag.act.converter.post.poll.CreatePollRequestConverter;
import ag.act.dto.SolidarityLeaderApplicantDto;
import ag.act.entity.ActUser;
import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.BoardCategory;
import ag.act.enums.ClientType;
import ag.act.enums.SelectionOption;
import ag.act.enums.VoteType;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionAnswerType;
import ag.act.exception.BadRequestException;
import ag.act.model.CreatePollItemRequest;
import ag.act.model.CreatePollRequest;
import ag.act.model.CreatePostRequest;
import ag.act.module.solidarity.election.ISolidarityLeaderElection;
import ag.act.service.BoardService;
import ag.act.service.post.PostService;
import ag.act.service.solidarity.SolidarityService;
import ag.act.service.solidarity.election.SolidarityLeaderApplicantService;
import ag.act.service.solidarity.election.SolidarityLeaderElectionPollItemMappingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class SolidarityLeaderElectionPostPollService implements ISolidarityLeaderElection.ElectionPost {
    private final PostService postService;
    private final BoardService boardService;
    private final SolidarityService solidarityService;
    private final SolidarityLeaderApplicantService solidarityLeaderApplicantService;
    private final SolidarityLeaderElectionPollItemMappingService leaderElectionPollItemMappingServices;
    private final CreatePollRequestConverter createPollRequestConverter;
    private final CreatePostRequestConverter createPostRequestConverter;

    public Post createBoardGroupPost(
        SolidarityLeaderElection leaderElection
    ) {
        final Board board = getBoard(leaderElection);
        return createPost(board);
    }

    public void createPolls(SolidarityLeaderElection leaderElection) {
        final Post post = postService.getPostNotDeleted(leaderElection.getPostId());

        if (post.hasPolls()) {
            return;
        }

        addPollsToPost(leaderElection, post);
    }

    private void addPollsToPost(SolidarityLeaderElection leaderElection, Post post) {
        final List<SolidarityLeaderApplicantDto> leaderApplicants = getSolidarityLeaderApplicants(leaderElection);
        final List<PollItem> savedPollItemList = addPollsToPost(leaderElection, leaderApplicants, post);

        leaderElectionPollItemMappingServices.createPollItemMappings(
            leaderElection.getId(),
            leaderApplicants,
            savedPollItemList
        );
    }

    private List<PollItem> addPollsToPost(
        SolidarityLeaderElection leaderElection,
        List<SolidarityLeaderApplicantDto> leaderApplicants,
        Post post
    ) {
        final List<CreatePollRequest> createPollRequest = makePollRequests(leaderElection, leaderApplicants);
        List<Poll> polls = createPollRequestConverter.convert(createPollRequest, post);

        polls.forEach(post::addPoll);
        Post savedPost = postService.savePost(post);
        return savedPost.getFirstPoll().getPollItemList();
    }

    private List<SolidarityLeaderApplicantDto> getSolidarityLeaderApplicants(SolidarityLeaderElection leaderElection) {
        final Long solidarityId = getSolidarityId(leaderElection);
        return solidarityLeaderApplicantService.getSolidarityLeaderApplicants(solidarityId, leaderElection.getId());
    }

    private Long getSolidarityId(SolidarityLeaderElection leaderElection) {
        return solidarityService.getSolidarityByStockCode(leaderElection.getStockCode()).getId();
    }

    private Post createPost(Board board) {
        return postService.savePost(
            makePost(
                getCreatePostRequest(board),
                getSystemAdminUser(),
                board
            )
        );
    }

    private Board getBoard(SolidarityLeaderElection leaderElection) {
        return boardService.findBoard(leaderElection.getStockCode(), BoardCategory.SOLIDARITY_LEADER_ELECTION)
            .orElseThrow(() -> new BadRequestException("주주선출 게시판을 찾을 수 없습니다."));
    }

    private CreatePostRequest getCreatePostRequest(Board board) {
        return new CreatePostRequest()
            .boardCategory(BoardCategory.SOLIDARITY_LEADER_ELECTION.name())
            .title("%s %s".formatted(board.getStock().getName(), POST_TITLE))
            .content(POST_CONTENT)
            .isAnonymous(Boolean.FALSE)
            .isActive(Boolean.FALSE)
            .isNotification(Boolean.FALSE)
            .isExclusiveToHolders(Boolean.FALSE);
    }

    private ActUser getSystemAdminUser() {
        return ActUserProvider.getSystemUser();
    }

    private List<CreatePollRequest> makePollRequests(
        SolidarityLeaderElection leaderElection,
        List<SolidarityLeaderApplicantDto> solidarityLeaderApplicants
    ) {
        return List.of(
            new CreatePollRequest()
                .title(POST_TITLE)
                .targetStartDate(DateTimeConverter.convert(leaderElection.getVoteStartDateTime()))
                .targetEndDate(DateTimeConverter.convert(leaderElection.getVoteEndDateTime()))
                .voteType(VoteType.SHAREHOLDER_BASED.name())
                .selectionOption(SelectionOption.MULTIPLE_ITEMS.name())
                .pollItems(makePollItemRequests(solidarityLeaderApplicants))
        );
    }

    private List<CreatePollItemRequest> makePollItemRequests(List<SolidarityLeaderApplicantDto> solidarityLeaderApplicants) {
        return solidarityLeaderApplicants.stream()
            .flatMap(this::makePollItemRequestsForApplicant)
            .toList();
    }

    private Stream<CreatePollItemRequest> makePollItemRequestsForApplicant(SolidarityLeaderApplicantDto solidarityLeaderApplicant) {
        return Stream.of(
            new CreatePollItemRequest().text(SolidarityLeaderElectionAnswerType.APPROVAL.getDisplayName()),
            new CreatePollItemRequest().text(SolidarityLeaderElectionAnswerType.REJECTION.getDisplayName())
        );
    }

    private Post makePost(CreatePostRequest createPostRequest, ActUser user, Board board) {
        return createPostRequestConverter.convert(createPostRequest, user, board, ClientType.CMS);
    }
}
