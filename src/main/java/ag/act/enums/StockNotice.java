package ag.act.enums;

import ag.act.model.NoticeLevel;
import lombok.Getter;

@Getter
public enum StockNotice {

    BLOCK_SOLIDARITY_LEADER(NoticeLevel.CRITICAL, "해당 종목의 주주대표를 차단한 상태입니다.\n게시글, 댓글 보기의 제한이 있을 수 있습니다.")
    ;

    private final NoticeLevel noticeLevel;
    private final String message;

    StockNotice(ag.act.model.NoticeLevel noticeLevel, String message) {
        this.noticeLevel = noticeLevel;
        this.message = message;
    }
}
