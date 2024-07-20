package ag.act.service.push;

import ag.act.converter.push.PushRequestConverter;
import ag.act.dto.admin.AutomatedAuthorPushDto;
import ag.act.dto.push.PushSearchDto;
import ag.act.entity.Post;
import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.enums.AppLinkType;
import ag.act.enums.AutomatedPushContentType;
import ag.act.enums.AutomatedPushCriteria;
import ag.act.enums.push.PushSearchType;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
import ag.act.model.CreatePushRequest;
import ag.act.model.PushRequest;
import ag.act.repository.PushRepository;
import ag.act.service.post.PostService;
import ag.act.service.stock.StockGroupService;
import ag.act.service.stock.StockService;
import ag.act.service.stockboardgrouppost.PostPushTargetDateTimeManager;
import ag.act.util.DateTimeUtil;
import ag.act.util.QueryUtil;
import ag.act.validator.PushValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PushService {
    private final PushRepository pushRepository;
    private final StockService stockService;
    private final StockGroupService stockGroupService;
    private final PushValidator pushValidator;
    private final PushRequestConverter pushRequestConverter;
    private final PostService postService;
    private final PostPushTargetDateTimeManager postPushTargetDateTimeManager;

    public Page<AutomatedAuthorPushDto> getAutomatedPushes(PushSearchDto pushSearchDto, Pageable pageable) {

        if (pushSearchDto.isEmpty()) {
            return pushRepository.findAllByPushTargetType(PushTargetType.AUTOMATED_AUTHOR, pageable);
        }

        if (pushSearchDto.getPushSearchType() == PushSearchType.PUSH_TITLE) {
            return pushRepository.findAllByPushTargetTypeAndTitleContaining(
                PushTargetType.AUTOMATED_AUTHOR, QueryUtil.toLikeString(pushSearchDto.getKeyword()), pageable
            );
        }

        if (pushSearchDto.getPushSearchType() == PushSearchType.AUTHOR_NAME) {
            return pushRepository.findAllByPushTargetTypeAndNameContaining(
                PushTargetType.AUTOMATED_AUTHOR, QueryUtil.toLikeString(pushSearchDto.getKeyword()), pageable
            );
        }

        if (pushSearchDto.getPushSearchType() == PushSearchType.AUTHOR_NICKNAME) {
            return pushRepository.findAllByPushTargetTypeAndNicknameContaining(
                PushTargetType.AUTOMATED_AUTHOR, QueryUtil.toLikeString(pushSearchDto.getKeyword()), pageable
            );
        }

        return pushRepository.findAllByPushTargetTypeAndContentContaining(
            PushTargetType.AUTOMATED_AUTHOR, QueryUtil.toLikeString(pushSearchDto.getKeyword()), pageable
        );
    }

    public Page<Push> getPushList(PushSearchDto pushSearchDto, Pageable pageable) {

        if (pushSearchDto.isEmpty()) {
            return pushRepository.findAllByPushTargetTypeNot(PushTargetType.AUTOMATED_AUTHOR, pageable);
        }

        if (pushSearchDto.getPushSearchType() == PushSearchType.PUSH_TITLE) {
            return pushRepository.findAllByPushTargetTypeNotAndTitleContaining(
                PushTargetType.AUTOMATED_AUTHOR, pushSearchDto.getKeyword(), pageable
            );
        }

        if (pushSearchDto.getPushSearchType() == PushSearchType.STOCK_NAME) {
            return pushRepository.findAllByPushTargetTypeNotAndStockNameContaining(
                PushTargetType.AUTOMATED_AUTHOR, pushSearchDto.getKeyword(), pageable
            );
        }

        if (pushSearchDto.getPushSearchType() == PushSearchType.STOCK_GROUP_NAME) {
            return pushRepository.findAllByPushTargetTypeNotAndStockGroupNameContaining(
                PushTargetType.AUTOMATED_AUTHOR, pushSearchDto.getKeyword(), pageable
            );
        }

        return pushRepository.findAllByPushTargetTypeNotAndContentContaining(
            PushTargetType.AUTOMATED_AUTHOR, pushSearchDto.getKeyword(), pageable
        );
    }

    public Optional<Push> createPushForGlobalEvent(
        PushRequest pushRequest, Long postId, Instant targetDatetime
    ) {
        if (pushRequest == null) {
            return Optional.empty();
        }
        return Optional.of(createPush(new CreatePushRequest()
            .title(pushRequest.getTitle())
            .content(pushRequest.getContent())
            .postId(postId)
            .linkType(AppLinkType.LINK.name())
            .stockTargetType(PushTargetType.ALL.name())
            .sendType(PushSendType.SCHEDULE.name())
            .targetDatetime(
                postPushTargetDateTimeManager.generatePushTargetDateTime(targetDatetime)
            )
        ));
    }

    public Optional<Push> createPushForStock(PushRequest pushRequest, Long postId, Instant targetDatetime, String stockCode) {
        final CreatePushRequest createPushRequest = new CreatePushRequest()
            .title(pushRequest.getTitle())
            .content(pushRequest.getContent())
            .postId(postId)
            .linkType(AppLinkType.LINK.name())
            .stockTargetType(PushTargetType.STOCK.name())
            .stockCode(stockCode)
            .sendType(PushSendType.IMMEDIATELY.name())
            .targetDatetime(
                postPushTargetDateTimeManager.generatePushTargetDateTime(targetDatetime)
            );

        return Optional.of(createPush(createPushRequest));
    }

    public Push createPush(CreatePushRequest createPushRequest) {
        pushValidator.validate(createPushRequest);

        final Push savedPush = pushRepository.save(
            pushRequestConverter.convert(
                createPushRequest,
                getPost(createPushRequest.getPostId())
            )
        );

        savedPush.setStock(getStockIfTargetTypeIsStock(savedPush));
        savedPush.setStockGroup(getStockGroupIfTargetTypeIsStockGroup(savedPush));

        return savedPush;
    }

    private Post getPost(Long postId) {
        if (postId == null) {
            return null;
        }
        return postService.getPostNotDeleted(postId);
    }

    private StockGroup getStockGroupIfTargetTypeIsStockGroup(Push push) {
        if (push.getPushTargetType() == PushTargetType.STOCK_GROUP) {
            return stockGroupService.findById(push.getStockGroupId())
                .orElseThrow(() -> new RuntimeException("그룹종목 아이디를 확인해주세요."));
        }
        return null;
    }

    private Stock getStockIfTargetTypeIsStock(Push push) {
        if (push.getPushTargetType() == PushTargetType.STOCK) {
            return stockService.findByCode(push.getStockCode())
                .orElseThrow(() -> new RuntimeException("종목코드를 확인해주세요."));
        }
        return null;
    }

    public Optional<Push> findPush(Long pushId) {
        if (pushId == null) {
            return Optional.empty();
        }
        return pushRepository.findById(pushId);
    }

    public void deletePush(Long pushId) {
        pushRepository.delete(
            pushValidator.validateForDeleteAndGet(
                findPush(pushId)
            )
        );
    }

    public List<Push> getPushListToSend() {
        return pushRepository.findAllBySendStatusAndTargetDatetimeLessThanEqualOrderByTargetDatetimeAsc(
            PushSendStatus.READY,
            LocalDateTime.now()
        );
    }

    public Push updatePushToProcessing(Push push) {
        push.setSendStatus(PushSendStatus.PROCESSING);
        push.setSentStartDatetime(LocalDateTime.now());
        return pushRepository.save(push);
    }

    public Push updatePushToComplete(Push push) {
        push.setSendStatus(PushSendStatus.COMPLETE);
        push.setSentEndDatetime(LocalDateTime.now());
        return pushRepository.save(push);
    }

    public List<Push> getPushes(
        Long contentId, AutomatedPushContentType contentType, AutomatedPushCriteria criteria, LocalDateTime targetDateTime
    ) {
        return pushRepository.findAllThatHaveAutomatedAuthorPush(
            contentId, contentType, criteria, targetDateTime
        );
    }

    public void updatePush(Push originalPush, PushRequest pushRequest, Instant activeStartDate) {
        if (pushRequest != null) {
            originalPush.setTitle(pushRequest.getTitle());
            originalPush.setContent(pushRequest.getContent());
        }

        final LocalDateTime requestActiveStartDate = postPushTargetDateTimeManager.generatePushTargetDateTimeForLocalDateTime(
            activeStartDate
        );
        if (!DateTimeUtil.isSimilarLocalDateTime(requestActiveStartDate, originalPush.getTargetDatetime())) {
            originalPush.setTargetDatetime(requestActiveStartDate);
        }

        pushRepository.save(originalPush);
    }

    public void unregisterAllReadyPushesByPostId(Long postId) {
        pushRepository.deleteAllByPostIdAndSendStatus(postId, PushSendStatus.READY);
    }
}
