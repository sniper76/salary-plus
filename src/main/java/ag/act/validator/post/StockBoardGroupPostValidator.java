package ag.act.validator.post;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.DateTimeConverter;
import ag.act.dto.TargetStartAndEndDatePeriod;
import ag.act.dto.post.BasePostRequest;
import ag.act.dto.post.UpdatePostRequestDto;
import ag.act.entity.ActUser;
import ag.act.entity.Board;
import ag.act.entity.DigitalProxy;
import ag.act.entity.Poll;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.SelectionOption;
import ag.act.exception.BadRequestException;
import ag.act.exception.TooManyRequestsException;
import ag.act.model.CreatePollAnswerItemRequest;
import ag.act.model.CreatePostRequest;
import ag.act.model.Status;
import ag.act.model.UpdatePollRequest;
import ag.act.module.post.PostAndCommentAopCurrentDateTimeProvider;
import ag.act.module.time.TimeDisplayFormatter;
import ag.act.repository.PostRepository;
import ag.act.service.user.UserRoleService;
import ag.act.util.DateTimeUtil;
import ag.act.validator.DefaultObjectValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
@RequiredArgsConstructor
public class StockBoardGroupPostValidator {
    private final DefaultObjectValidator defaultObjectValidator;
    private final PostRepository postRepository;
    private final UserRoleService userRoleService;
    private final PostAndCommentAopCurrentDateTimeProvider postAndCommentAopCurrentDateTimeProvider;
    private final TimeDisplayFormatter timeDisplayFormatter;

    public Post validateBoardGroupPost(Long postId, String stockCode, String boardGroupName, List<Status> deletedStatuses) {
        final Post post = getPost(postId);
        validateDefaultBoardGroupPost(postId, stockCode, boardGroupName);
        if (!ActUserProvider.getNoneNull().isAdmin()) {
            defaultObjectValidator.validateStatus(post, deletedStatuses, "이미 삭제된 게시글입니다.");
        }
        return post;
    }

    public Post validateBoardGroupPost(Long postId, String stockCode, String boardGroupName, ActUser actUser, List<Status> deletedStatuses) {
        Post post = validateBoardGroupPost(postId, stockCode, boardGroupName);

        if (actUser.isGuest() || !actUser.isAdmin()) {
            defaultObjectValidator.validateStatus(post, deletedStatuses, "이미 삭제된 게시글입니다.");
        }

        return post;
    }

    public Post validateBoardGroupPost(Long postId, String stockCode, String boardGroupName) {
        final Post post = getPost(postId);
        validateDefaultBoardGroupPost(postId, stockCode, boardGroupName);
        return post;
    }

    public Post validateBoardGroupPost(BasePostRequest basePostRequest, List<Status> invalidStatuses) {
        return validateBoardGroupPost(
            basePostRequest.getPostId(),
            basePostRequest.getStockCode(),
            basePostRequest.getBoardGroupName(),
            invalidStatuses
        );
    }

    private void validateDefaultBoardGroupPost(Long postId, String stockCode, String boardGroupName) {
        final BoardGroup changeStockCode = BoardGroup.fromValue(boardGroupName);
        final Post post = getPost(postId);
        validateBoardGroupAndStockCodeOfBoardGroupPost(post, stockCode, changeStockCode);
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new BadRequestException("대상 게시글이 없습니다."));
    }

    public void validateBoardGroupAndStockCodeOfBoardGroupPost(Post post, String stockCode, String boardGroupName) {
        validateBoardGroupAndStockCodeOfBoardGroupPost(post, stockCode, BoardGroup.fromValue(boardGroupName));
    }

    public void validateBoardGroupAndStockCodeOfBoardGroupPost(Post post, String stockCode, BoardGroup boardGroup) {
        if (boardGroup != post.getBoard().getGroup()) {
            throw new BadRequestException("%s에서 해당 게시글을 찾을 수 없습니다.".formatted(boardGroup.name()));
        }

        if (!post.getBoard().getStockCode().equals(stockCode)) {
            throw new BadRequestException("게시글과 %s 종목이 일치하지 않습니다.".formatted(stockCode));
        }
    }

    public Board validateBoardStockCode(Board board, String stockCode) {
        if (!board.getStockCode().equals(stockCode)) {
            throw new BadRequestException("작성할 게시글과 %s 종목이 일치하지 않습니다.".formatted(stockCode));
        }
        return board;
    }

    public void validateAuthor(User currentUser, Long authorId, String message) {
        if (isBothUserAdmin(currentUser, authorId)) {
            return;
        }
        if (!Objects.equals(currentUser.getId(), authorId)) {
            throw new BadRequestException("%s 등록자와 현재사용자가 일치하지 않습니다.".formatted(message));
        }
    }

    private boolean isBothUserAdmin(User currentUser, Long authorId) {
        return currentUser.isAdmin() && userRoleService.isAdmin(authorId);
    }

    public void validatePollBeforeAnswer(Poll poll, Long pollId) {
        if (poll == null) {
            throw new BadRequestException("게시글에 설문 정보가 존재하지 않습니다.");
        }
        if (!Objects.equals(poll.getId(), pollId)) {
            throw new BadRequestException("게시글과 설문 정보가 일치하지 않습니다.");
        }
        if (CollectionUtils.isEmpty(poll.getPollItemList())) {
            throw new BadRequestException("설문 항목이 존재하지 않습니다.");
        }
        if (DateTimeUtil.isNowBefore(poll.getTargetStartDate())) {
            throw new BadRequestException("진행하기 전 설문입니다.");
        }
        if (DateTimeUtil.isNowAfter(poll.getTargetEndDate())) {
            throw new BadRequestException("이미 종료된 설문입니다.");
        }
    }

    public void validateUpdateTargetDate(Post post, Instant targetStartDate, Instant targetEndDate) {

        if (targetEndDate == null) {
            throw new BadRequestException("수정하려는 종료일자가 없습니다.");
        }

        if (DateTimeUtil.isNowAfter(DateTimeConverter.convert(targetEndDate))) {
            throw new BadRequestException("종료일은 현재일 이후로 수정 가능합니다.");
        }

        final List<Poll> polls = post.getPolls();
        DigitalProxy postDigitalProxy = post.getDigitalProxy();
        DigitalDocument digitalDocument = post.getDigitalDocument();

        if (targetStartDate != null) {
            if (targetStartDate.isAfter(targetEndDate)) {
                throw new BadRequestException("시작일은 종료일 이후로 수정할 수 없습니다.");
            }
            return;
        }

        // 수정시 입력된 시작일이 없을때 기존에 저장된 시작일과 비교
        if (polls != null) {
            if (polls.stream().anyMatch(poll -> poll.getTargetStartDate().isAfter(DateTimeConverter.convert(targetEndDate)))) {
                throw new BadRequestException("종료일을 시작일 이전으로 수정 불가능합니다.");
            }
        }
        if (postDigitalProxy != null
            && postDigitalProxy.getTargetStartDate().isAfter(DateTimeConverter.convert(targetEndDate))) {
            throw new BadRequestException("종료일을 시작일 이전으로 수정 불가능합니다.");
        }
        if (digitalDocument != null
            && digitalDocument.getTargetStartDate().isAfter(DateTimeConverter.convert(targetEndDate))) {
            throw new BadRequestException("종료일을 시작일 이전으로 수정 불가능합니다.");
        }
    }

    public void validateUpdateTargetDate(Poll poll, Instant targetStartDate, Instant targetEndDate) {

        if (targetEndDate == null) {
            throw new BadRequestException("수정하려는 종료일자가 없습니다.");
        }

        if (DateTimeUtil.isNowAfter(DateTimeConverter.convert(targetEndDate))) {
            throw new BadRequestException("종료일은 현재일 이후로 수정 가능합니다.");
        }

        if (targetStartDate != null) {
            if (targetStartDate.isAfter(targetEndDate)) {
                throw new BadRequestException("시작일은 종료일 이후로 수정할 수 없습니다.");
            }
            return;
        }

        // 수정시 입력된 시작일이 없을때 기존에 저장된 시작일과 비교
        if (poll != null
            && poll.getTargetStartDate().isAfter(DateTimeConverter.convert(targetEndDate))) {
            throw new BadRequestException("종료일을 시작일 이전으로 수정 불가능합니다.");
        }
    }

    private void validateUpdateTargetDate(Post post, TargetStartAndEndDatePeriod period) {
        if (period == null || period.getTargetEndDate() == null) {
            return;
        }

        validateUpdateTargetDate(post, period.getTargetStartDate(), period.getTargetEndDate());
    }

    public void validateUpdateTargetDateForDigitalDocument(Post post, UpdatePostRequestDto updatePostRequestDto) {
        if (post.getDigitalDocument() != null) {
            validateUpdateTargetDate(post, updatePostRequestDto.getUpdatePostRequest().getDigitalDocument());
        }
    }

    public void validateUpdateTargetDateForDigitalProxy(Post post, UpdatePostRequestDto updatePostRequestDto) {
        if (post.getDigitalProxy() != null) {
            validateUpdateTargetDate(post, updatePostRequestDto.getUpdatePostRequest().getDigitalProxy());
        }
    }

    public void validateUpdateTargetDateWhenPollsAreNull(Post post, UpdatePostRequestDto updatePostRequestDto) {
        if (isNotPollUpdate(updatePostRequestDto)) {
            // 설문이 없는 그냥 일반적인 게시글일 경우
            validateUpdateTargetDate(post, updatePostRequestDto.getUpdatePostRequest().getUpdateTargetDate());
        }
    }

    private boolean isNotPollUpdate(UpdatePostRequestDto updatePostRequestDto) {
        return updatePostRequestDto.getUpdatePostRequest().getPolls() == null;
    }

    public void validateUpdateTargetDateForMultiplePolls(Post post, List<UpdatePollRequest> polls) {
        emptyIfNull(polls)
            .stream()
            .filter(Objects::nonNull)
            .filter(updatePollRequest -> updatePollRequest.getTargetEndDate() != null)
            .forEach(updatePollRequest ->
                post.getPolls().stream()
                    .filter(it -> Objects.equals(it.getId(), updatePollRequest.getId()))
                    .findFirst()
                    .ifPresent(poll -> validateUpdateTargetDate(
                        poll,
                        updatePollRequest.getTargetStartDate(),
                        updatePollRequest.getTargetEndDate()
                    ))
            );
    }

    public void validatePollSingle(List<CreatePollAnswerItemRequest> pollItemList, SelectionOption selectionOption) {
        if (selectionOption.isSingleItem() && pollItemList.size() != 1) {
            throw new BadRequestException("게시글의 단일 설문에 복수의 답변을 하였습니다.");
        }
    }

    public void validateUserPermission(CreatePostRequest createPostRequest, boolean isSolidarityLeader) {

        final BoardCategory boardCategoryEnum = BoardCategory.fromValue(createPostRequest.getBoardCategory());
        if (boardCategoryEnum == null) {
            throw new BadRequestException("게시판 카테고리가 존재하지 않습니다.");
        }

        if (ActUserProvider.getNoneNull().isAdmin()) {
            return;
        }

        // TODO 사실 여긴 SuperAdmin 만 가능하다, 나중에 좀 더 정확한 권한 체크로 변경해야 한다.
        if (boardCategoryEnum.getBoardGroup() == BoardGroup.GLOBALBOARD) {
            throw createBadRequestException(boardCategoryEnum);
        }

        final boolean allUserWritable = boardCategoryEnum.isAllUserWritable();
        final boolean leaderWritable = boardCategoryEnum.isLeaderWritable();

        if (isSolidarityLeader && (!allUserWritable && !leaderWritable)) {
            throw createBadRequestException(boardCategoryEnum);
        }

        if (!isSolidarityLeader && !allUserWritable) {
            throw createBadRequestException(boardCategoryEnum);
        }

        if (createPostRequest.getDigitalProxy() != null) {
            throw new BadRequestException("%s 게시판에 의결권위임을 생성할 권한이 없습니다.".formatted(boardCategoryEnum.getDisplayName()));
        }

        validatePublicToAllUsers(boardCategoryEnum, createPostRequest);
    }

    private BadRequestException createBadRequestException(BoardCategory boardCategory) {
        return new BadRequestException("%s 게시판에 글쓰기 권한이 없습니다.".formatted(boardCategory.getDisplayName()));
    }

    public void validateChangeAnonymous(Post post, Boolean isAnonymous) {
        if (post.getIsAnonymous() != isAnonymous) {
            throw new BadRequestException("게시글의 익명 여부는 변경할 수 없습니다.");
        }
    }

    private void validatePublicToAllUsers(BoardCategory boardCategory, CreatePostRequest createPostRequest) {
        if (boardCategory.isOnlyExclusiveToHolders() && !createPostRequest.getIsExclusiveToHolders()) {
            throw new BadRequestException("주주에게만 공개 할 수 있습니다.");
        }
    }

    public void validatePollAnswers(List<CreatePollAnswerItemRequest> pollAnswers, List<PollItem> pollItemList) {
        final Map<Long, PollItem> pollItemMap = pollItemList.stream()
            .collect(Collectors.toMap(PollItem::getId, Function.identity()));

        if (pollAnswers.stream().allMatch(item -> pollItemMap.containsKey(item.getPollItemId()))) {
            return;
        }

        throw new BadRequestException("게시글의 설문 항목과 일치하지 않습니다.");
    }

    public void validateCategory(CreatePostRequest createPostRequest) {
        BoardCategory boardCategory = BoardCategory.fromValue(createPostRequest.getBoardCategory());

        if (boardCategory == null) {
            throw new BadRequestException("게시글 카테고리를 확인해 주세요.");
        }

        if (BoardCategory.DIGITAL_DELEGATION == boardCategory
            && createPostRequest.getDigitalProxy() == null
            && createPostRequest.getDigitalDocument() == null) {
            throw new BadRequestException("모두싸인 혹은 전자문서 정보가 없습니다.");
        }

        if (getDigitalDocumentCategories().contains(boardCategory)
            && createPostRequest.getDigitalDocument() == null) {
            throw new BadRequestException("전자문서 정보가 없습니다.");
        }

        if (BoardCategory.SURVEYS == boardCategory) {
            if (CollectionUtils.isEmpty(createPostRequest.getPolls())) {
                throw new BadRequestException("설문 정보가 없습니다.");
            }
        }

        if (hasMultiSubModules(createPostRequest)) {
            throw new BadRequestException("2개 이상의 서브 모듈을 등록할 수 없습니다.");
        }
    }

    private boolean hasMultiSubModules(CreatePostRequest createPostRequest) {
        return CollectionUtils.isNotEmpty(createPostRequest.getPolls())
               && hasAnyDigitalProxyOrDigitalDocument(createPostRequest);
    }

    private boolean hasAnyDigitalProxyOrDigitalDocument(CreatePostRequest createPostRequest) {
        return Stream.of(
            createPostRequest.getDigitalProxy(),
            createPostRequest.getDigitalDocument()
        ).anyMatch(Objects::nonNull);
    }

    private List<BoardCategory> getDigitalDocumentCategories() {
        return List.of(BoardCategory.ETC, BoardCategory.CO_HOLDING_ARRANGEMENTS);
    }

    public void validatePostCooldown(Post latestPost, final int postCooldownSeconds) {
        long remainingSeconds = getRemainingSecondsUntilPostingEnabled(latestPost, postCooldownSeconds);

        if (remainingSeconds > 0) {
            String remainingTimeDisplay = timeDisplayFormatter.format(remainingSeconds);
            throw new TooManyRequestsException(
                "잠시 후 다시 시도해 주세요. 게시글 등록은 도배 방지를 위해 일정 시간 간격을 두고 가능합니다.(%s 남음)".formatted(remainingTimeDisplay)
            );
        }
    }

    private long getRemainingSecondsUntilPostingEnabled(Post post, final int postCooldownSeconds) {
        LocalDateTime enableNextPostingTime = post.getCreatedAt().plusSeconds(postCooldownSeconds);

        return postAndCommentAopCurrentDateTimeProvider.getSecondsUntil(enableNextPostingTime);
    }
}
