package ag.act.util;

import ag.act.core.infra.ServerEnvironment;
import ag.act.enums.SlackChannel;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SlackMessageSender {
    private final String slackToken;
    private final ServerEnvironment serverEnvironment;

    public SlackMessageSender(
        @Value("${slack.token}") String slackToken,
        ServerEnvironment serverEnvironment
    ) {
        this.slackToken = slackToken;
        this.serverEnvironment = serverEnvironment;
    }

    @Async("threadPoolTaskExecutor")
    public void sendSlackMessage(String message, SlackChannel slackChannel) {
        try {
            MethodsClient methods = Slack.getInstance().methods(slackToken);

            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(slackChannel.getChannelName())
                .text("[%s] %s".formatted(serverEnvironment.getActEnvironment(), message))
                .build();

            methods.chatPostMessage(request);
            log.info("message:{} sent to channel:{}", message, slackChannel.getChannelName());

        } catch (SlackApiException | IOException e) {
            log.error("Failed to send slack message: ", e);
        }
    }
}
