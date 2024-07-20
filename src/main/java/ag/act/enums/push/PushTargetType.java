package ag.act.enums.push;

import ag.act.entity.Push;
import ag.act.enums.AppLinkType;
import ag.act.exception.BadRequestException;
import ag.act.model.CreatePushRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum PushTargetType {
    ALL("전체") {
        @Override
        public void setDataByTargetType(Push push, CreatePushRequest createPushRequest) {
            push.setTopic(PushTopic.NOTICE.name());
        }

        @Override
        public void validateAppLinkType(AppLinkType linkType) {
            if (linkType == AppLinkType.STOCK_HOME) {
                throw new BadRequestException("타켓이 그룹이거나 종목전체 일때 링크(종목홈)을 선택할 수 없습니다.");
            }
        }
    },
    STOCK("종목") {
        @Override
        public void setDataByTargetType(Push push, CreatePushRequest createPushRequest) {
            push.setStockCode(createPushRequest.getStockCode().trim());
        }
    },
    STOCK_GROUP("종목그룹") {
        @Override
        public void setDataByTargetType(Push push, CreatePushRequest createPushRequest) {
            push.setStockGroupId(createPushRequest.getStockGroupId());
        }

        @Override
        public void validateAppLinkType(AppLinkType linkType) {
            if (linkType == AppLinkType.STOCK_HOME) {
                throw new BadRequestException("타켓이 그룹이거나 종목전체 일때 링크(종목홈)을 선택할 수 없습니다.");
            }
        }
    },
    AUTOMATED_AUTHOR("개인자동푸쉬발송") {
        @Override
        public void setDataByTargetType(Push push, CreatePushRequest createPushRequest) {
            // ignore
        }

        @Override
        public void validateAppLinkType(AppLinkType linkType) {
            // ignore
        }
    },
    UNKNOWN("알수없음") {
        @Override
        public void setDataByTargetType(Push push, CreatePushRequest createPushRequest) {
            // ignore
        }

        @Override
        public void validateAppLinkType(AppLinkType linkType) {
            // ignore
        }
    },
    ;

    private final String displayName;

    PushTargetType(String displayName) {
        this.displayName = displayName;
    }

    public static PushTargetType fromValue(String targetTypeName) {
        try {
            return PushTargetType.valueOf(targetTypeName.trim().toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("지원하지 않는 PushTargetType '%s' 타입입니다.".formatted(targetTypeName));
        }
    }

    public abstract void setDataByTargetType(Push push, ag.act.model.CreatePushRequest createPushRequest);

    public void validateAppLinkType(AppLinkType linkType) {
        //normal case
    }
}
