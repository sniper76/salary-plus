package ag.act.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public enum AutomatedPushCriteria {
    LIKE {
        @Override
        public boolean canSendPush(int currentValue) {
            return currentValue == 10;
        }

        @Override
        public String getTitle() {
            return "액트베스트 선정알림";
        }

        @Override
        public String getMessage() {
            return "축하합니다! 작성하신 게시글이 액트베스트에 선정되었습니다. 지금 바로 확인해보세요!";
        }
    },
    COMMENT {
        @Override
        public boolean canSendPush(int currentValue) {
            return List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 30, 50, 100).contains(currentValue);
        }

        @Override
        public String getTitle() {
            return "새로운 댓글알림";
        }

        @Override
        public String getMessage() {
            return "작성하신 게시글에 새로운 댓글이 등록되었습니다. 지금 바로 확인해보세요!";
        }
    },
    REPLY {
        @Override
        public boolean canSendPush(int currentValue) {
            return List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 30, 50, 100).contains(currentValue);
        }

        @Override
        public String getTitle() {
            return "새로운 답글알림";
        }

        @Override
        public String getMessage() {
            return "작성하신 댓글에 새로운 답글이 등록되었습니다. 지금 바로 확인해보세요!";
        }
    };

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AutomatedPushCriteria fromValue(String value) {
        try {
            return AutomatedPushCriteria.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 AutomatedPushCriteria '%s' 타입입니다.".formatted(value));
        }
    }

    public abstract String getMessage();

    public abstract String getTitle();

    public abstract boolean canSendPush(int currentValue);
}
