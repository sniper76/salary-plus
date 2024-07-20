package ag.act.service.post;

import ag.act.configuration.security.ActUserProvider;
import ag.act.constants.MessageConstants;
import ag.act.converter.DateTimeConverter;
import ag.act.dto.GetPostDigitalDocumentSearchDto;
import ag.act.dto.GetPostsSearchDto;
import ag.act.dto.campaign.SimpleCampaignPostDto;
import ag.act.entity.Poll;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentNumber;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.model.CreatePostRequest;
import ag.act.model.ReportStatus;
import ag.act.model.Status;
import ag.act.model.UpdateCampaignRequest;
import ag.act.model.UpdatePollRequest;
import ag.act.model.UpdatePostRequest;
import ag.act.model.UpdatePostRequestDigitalDocument;
import ag.act.repository.PostRepository;
import ag.act.service.blockeduser.BlockedUserEnhancer;
import ag.act.service.digitaldocument.DigitalDocumentNumberService;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.notification.NotificationService;
import ag.act.service.stockboardgrouppost.search.StockBoardGroupPostSearchServiceResolver;
import ag.act.util.AppRenewalDateProvider;
import ag.act.util.StatusUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService implements BlockedUserEnhancer, MessageConstants {

    private final PostRepository postRepository;
    private final DigitalDocumentService digitalDocumentService;
    private final DigitalDocumentNumberService digitalDocumentNumberService;
    private final NotificationService notificationService;
    private final AppRenewalDateProvider appRenewalDateProvider;
    private final StockBoardGroupPostSearchServiceResolver stockBoardGroupPostSearchServiceResolver;
    private final PostIsActiveDecisionMaker postIsActiveDecisionMaker;

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public void saveAll(List<Post> posts) {
        postRepository.saveAll(posts);
    }

    public Optional<Post> findById(Long postId) {
        return postRepository.findById(postId);
    }

    public Post getPost(Long postId) {
        return findById(postId)
            .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));
    }

    public Post getPostNotDeleted(Long postId) {
        return postRepository.findByIdAndStatusNotIn(postId, StatusUtil.getDeletedStatusesForPostDetails())
            .orElseThrow(() -> new BadRequestException("게시글을 찾을 수 없습니다."));
    }

    public void updatePostWithCommentCount(Long postId, Long commentCount) {
        Post post = getPost(postId);
        post.setCommentCount(commentCount);
        savePost(post);
    }

    public Page<Post> getBoardPosts(GetPostsSearchDto getPostsSearchDto) {
        return stockBoardGroupPostSearchServiceResolver.resolve(getPostsSearchDto.getPostSearchType())
            .getBoardPosts(getPostsSearchDto);
    }

    public Page<Post> getBoardPosts(List<Long> boardIdList, List<Long> blockedUserIdList, List<Status> statuses, Pageable pageable) {
        return postRepository.findAllByBoardIdInAndStatusInAndUserIdNotIn(
            boardIdList,
            statuses,
            refinedBlockedUserIdList(blockedUserIdList),
            appRenewalDateProvider.get().atStartOfDay(),
            pageable
        );
    }

    public Page<Post> getBoardPostsByStockCodeAndBoardGroup(
        String stockCode, BoardGroup boardGroup, List<Long> blockedUserIdList, List<Status> statuses, Pageable pageable
    ) {
        return postRepository.findAllByBoardStockCodeAndBoardCategoryInAndStatusInAndUserIdNotIn(
            stockCode, boardGroup.getCategories(), statuses, refinedBlockedUserIdList(blockedUserIdList), pageable
        );
    }

    public DigitalDocument createDigitalDocument(Post post, CreatePostRequest createPostRequest) {
        DigitalDocument digitalDocument = (DigitalDocument) digitalDocumentService.createDigitalDocumentAndGet(
            post,
            createPostRequest.getDigitalDocument()
        );
        createDigitalDocumentNumber(digitalDocument.getId());
        return digitalDocument;
    }

    private void createDigitalDocumentNumber(Long digitalDocumentId) {
        final DigitalDocumentNumber digitalDocumentNumber = new DigitalDocumentNumber();
        digitalDocumentNumber.setDigitalDocumentId(digitalDocumentId);
        digitalDocumentNumber.setLastIssuedNumber(0L);
        digitalDocumentNumberService.save(digitalDocumentNumber);
    }

    public Post deletePost(Post post, LocalDateTime deleteTime) {
        return deletePost(post, Status.DELETED_BY_USER, deleteTime);
    }

    public Post deletePost(Post post, Status status, LocalDateTime deleteTime) {
        post.setDeletedAt(deleteTime);
        post.setStatus(status);

        return savePost(post);
    }

    public Post deletePost(Long postId, Status status, LocalDateTime deleteTime) {
        final Post post = getPostNotDeleted(postId);

        return deletePost(post, status, deleteTime);
    }

    public void updateReportStatus(Long postId, ReportStatus reportStatus) {
        Post post = findById(postId)
            .orElseThrow(() -> new NotFoundException("신고된 게시글 정보가 없습니다."));

        post.setStatus(
            reportStatus == ReportStatus.COMPLETE
                ? Status.DELETED_BY_ADMIN
                : Status.ACTIVE
        );

        savePost(post);
    }

    public List<SimpleCampaignPostDto> getPostsWithStockBySourcePostId(Long sourcePostId) {
        return postRepository.findAllSimpleCampaignPostDtosBySourcePostId(sourcePostId);
    }

    public List<Post> getAllPostsBySourcePostId(Long sourcePostId) {
        return postRepository.findAllBySourcePostId(sourcePostId);
    }

    private List<Post> getAllPostsBySourcePostIdIncludingSourcePost(Long sourcePostId) {
        final List<Post> postList = getAllPostsBySourcePostId(sourcePostId);
        postList.add(getPost(sourcePostId));

        return postList;
    }

    public void updatePostListByCampaign(Long sourcePostId, UpdateCampaignRequest updateCampaignRequest) {
        final User user = ActUserProvider.getNoneNull();
        final List<Post> postList = getAllPostsBySourcePostIdIncludingSourcePost(sourcePostId);

        postList
            .forEach(post -> {
                updateSurvey(post, updateCampaignRequest.getUpdatePostRequest(), user);
                updateEtcDocument(post, updateCampaignRequest.getUpdatePostRequest(), user);
                notificationService.updateNotification(post, updateCampaignRequest.getUpdatePostRequest().getIsNotification());
            });
    }

    private void updateEtcDocument(Post post, UpdatePostRequest updatePostRequest, User user) {
        if (post.getBoard().getCategory() != BoardCategory.ETC) {
            return;
        }
        applyUpdateRequestToPost(post, updatePostRequest, user);

        final UpdatePostRequestDigitalDocument requestDigitalDocument = updatePostRequest.getDigitalDocument();
        final DigitalDocument postDigitalDocument = post.getDigitalDocument();
        postDigitalDocument.setTitle(requestDigitalDocument.getTitle());
        postDigitalDocument.setContent(requestDigitalDocument.getContent());
        postDigitalDocument.setTargetEndDate(DateTimeConverter.convert(requestDigitalDocument.getTargetEndDate()));
        if (requestDigitalDocument.getTargetStartDate() != null) {
            postDigitalDocument.setTargetStartDate(DateTimeConverter.convert(requestDigitalDocument.getTargetStartDate()));
        }

        savePost(post);
    }

    private void applyUpdateRequestToPost(Post post, UpdatePostRequest updatePostRequest, User user) {
        post.setTitle(updatePostRequest.getTitle());
        post.setContent(updatePostRequest.getContent());
        post.setStatus(postIsActiveDecisionMaker.getIsActiveStatus(user, updatePostRequest.getIsActive()));
    }

    private void updateSurvey(Post post, UpdatePostRequest updatePostRequest, User user) {
        if (post.getBoard().getCategory() != BoardCategory.SURVEYS) {
            return;
        }
        applyUpdateRequestToPost(post, updatePostRequest, user);

        final List<UpdatePollRequest> requestPolls = updatePostRequest.getPolls();
        final List<Poll> postPolls = post.getPolls();

        for (int index = 0; index < postPolls.size(); index++) {
            updatePoll(postPolls.get(index), requestPolls.get(index));
        }

        savePost(post);
    }

    private void updatePoll(Poll poll, UpdatePollRequest pollRequest) {
        poll.setTitle(pollRequest.getTitle());
        poll.setTargetEndDate(DateTimeConverter.convert(pollRequest.getTargetEndDate()));
        if (pollRequest.getTargetStartDate() != null) {
            poll.setTargetStartDate(DateTimeConverter.convert(pollRequest.getTargetStartDate()));
        }
    }

    public Page<Post> getDigitalDocumentPosts(DigitalDocumentType type, Long userId, Pageable pageable) {
        return postRepository.findAllByTypeAndUserId(type.name(), userId, pageable);
    }

    public Page<Post> getDigitalDocumentPostsOfAcceptor(GetPostDigitalDocumentSearchDto searchDto) {
        if (searchDto.isBlankSearchKeyword()) {
            return postRepository.findAllByDigitalDocumentTypeAndDigitalDocumentAcceptUserId(
                searchDto.getDigitalDocumentType(),
                searchDto.getAcceptorId(),
                searchDto.getPageRequest()
            );
        }

        return postRepository.findAllByDigitalDocumentTypeAndDigitalDocumentAcceptUserIdAndTitleContaining(
            searchDto.getDigitalDocumentType(),
            searchDto.getAcceptorId(),
            searchDto.getSearchKeyword().trim(),
            searchDto.getPageRequest()
        );
    }

    public long getActivePostsCount(Long userId) {
        return postRepository.countByUserIdAndStatus(userId, Status.ACTIVE);
    }

    public Optional<Post> findLatestPostFrom(Long userId, LocalDateTime startTime) {
        return postRepository.findFirstByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(userId, startTime);
    }
}
