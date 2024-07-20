package ag.act.service.stock;

import ag.act.core.infra.ServerEnvironment;
import ag.act.enums.SlackChannel;
import ag.act.util.SlackMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockChangeNotifier {

    private static final String MESSAGE_TEMPLATE = "[%s(%s)] 종목의 주식 발행수가 과도하게 변경되었습니다.\n변경전 주식 발행수: %s\n변경후 주식 발행수: %s\n확인이 필요합니다.";

    private final SlackMessageSender slackMessageSender;
    private final ServerEnvironment serverEnvironment;

    public void notify(String stockName, String stockCode, long originalStockQuantity, long newStockQuantity) {
        sendSlackMessage(MESSAGE_TEMPLATE.formatted(stockName, stockCode, originalStockQuantity, newStockQuantity));
    }

    private void sendSlackMessage(String message) {
        if (serverEnvironment.isProd()) {
            slackMessageSender.sendSlackMessage(message, SlackChannel.ACT_STOCK_CHANGE_ALERT);
        }
    }
}
