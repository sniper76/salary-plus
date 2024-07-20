package ag.act.enums;

import ag.act.exception.BadRequestException;
import lombok.Getter;

@Getter
public enum SlackChannel {
    ACT_BATCH_ALERT("act-batch-alert"),
    ACT_INTEGRATION_TEST("act-integration-test"),
    ACT_SOLIDARITY_LEADER_ALERT("act-solidarity-leader-alert"),
    ACT_SOLIDARITY_LEADER_APPLICANT_ALERT("act-solidarity-leader-applicant-alert"),
    ACT_MARKANY_ALERT("act-markany-alert"),
    ACT_STOCK_CHANGE_ALERT("act-stock-change-alert"),
    ACT_EMAIL_ALERT("act-email-alert"),
    ;

    private final String channelName;

    SlackChannel(String channelName) {
        this.channelName = channelName;
    }

    public static SlackChannel fromValue(String slackChannelName) {
        try {
            return SlackChannel.valueOf(slackChannelName.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("지원하지 않는 SlackChannel %s 입니다.".formatted(slackChannelName));
        }
    }
}
