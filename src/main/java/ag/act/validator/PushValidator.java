package ag.act.validator;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.Push;
import ag.act.enums.AppLinkType;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.model.CreatePushRequest;
import ag.act.service.post.PostService;
import ag.act.service.stock.StockGroupService;
import ag.act.service.stock.StockService;
import ag.act.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PushValidator {
    private final StockService stockService;
    private final StockGroupService stockGroupService;
    private final PostService postService;

    public void validate(CreatePushRequest createPushRequest) {
        validateTitle(createPushRequest);
        validateTargetType(createPushRequest);
        validateContent(createPushRequest);
        validateSendType(createPushRequest.getSendType());
        validatePostId(createPushRequest);
        validateTargetDatetime(
            PushSendType.fromValue(createPushRequest.getSendType()),
            createPushRequest.getTargetDatetime()
        );
    }

    private void validateTitle(CreatePushRequest createPushRequest) {
        if (StringUtils.isBlank(createPushRequest.getTitle())) {
            throw new BadRequestException("제목을 확인해주세요.");
        }
    }

    private void validateContent(CreatePushRequest createPushRequest) {
        if (StringUtils.isBlank(createPushRequest.getContent())) {
            throw new BadRequestException("내용을 확인해주세요.");
        }
    }

    private void validateTargetType(CreatePushRequest createPushRequest) {
        final PushTargetType stockTargetType = PushTargetType.fromValue(createPushRequest.getStockTargetType());

        validateStockType(createPushRequest, stockTargetType);
        validateStockGroupType(createPushRequest, stockTargetType);
    }

    private void validateStockGroupType(CreatePushRequest createPushRequest, PushTargetType stockTargetType) {
        if (stockTargetType != PushTargetType.STOCK_GROUP) {
            return;
        }

        if (createPushRequest.getStockGroupId() == null) {
            throw new BadRequestException("종목그룹 아이디를 확인해주세요.");
        }

        stockGroupService.findById(createPushRequest.getStockGroupId())
            .orElseThrow(() -> new BadRequestException("종목그룹 아이디를 확인해주세요."));

    }

    private void validateStockType(CreatePushRequest createPushRequest, PushTargetType stockTargetType) {
        if (stockTargetType != PushTargetType.STOCK) {
            return;
        }

        if (StringUtils.isBlank(createPushRequest.getStockCode())) {
            throw new BadRequestException("종목코드를 확인해주세요.");
        }

        stockService.findByCode(createPushRequest.getStockCode())
            .orElseThrow(() -> new BadRequestException("종목코드를 확인해주세요."));
    }

    private void validateTargetDatetime(PushSendType pushSendType, Instant targetDatetime) {
        if (pushSendType == PushSendType.IMMEDIATELY) {
            return;
        }

        if (targetDatetime == null) {
            throw new BadRequestException("발송시간을 확인해주세요.");
        }

        if (DateTimeUtil.isNowAfter(DateTimeConverter.convert(targetDatetime))) {
            throw new BadRequestException("발송시간은 현재시간 이후로 설정 가능합니다.");
        }
    }

    private void validateSendType(String sendTypeName) {
        try {
            PushSendType.fromValue(sendTypeName);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("발송타입을 확인해주세요.");
        }
    }

    private void validatePostId(CreatePushRequest createPushRequest) {
        AppLinkType appLinkType = AppLinkType.fromValue(createPushRequest.getLinkType());

        if (AppLinkType.LINK == appLinkType) {
            Long postId = createPushRequest.getPostId();
            if (postId == null) {
                throw new BadRequestException("게시물 아이디를 확인해주세요.");
            }

            postService.findById(postId)
                .orElseThrow(() -> new BadRequestException("해당 게시글을 찾을 수 없습니다."));
        }
    }

    public Push validateForDeleteAndGet(Optional<Push> pushOptional) {

        final Push push = pushOptional
            .orElseThrow(() -> new NotFoundException("해당 푸시를 찾을 수 없습니다."));

        if (push.getSendType() == PushSendType.IMMEDIATELY) {
            throw new BadRequestException("발송 예약된 푸시만 삭제 가능합니다.");
        }

        if (push.getSendStatus() != PushSendStatus.READY) {
            throw new BadRequestException("발송대기중인 푸시만 삭제 가능합니다.");
        }

        return push;
    }
}
