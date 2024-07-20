package ag.act.converter.push;

import ag.act.converter.DateTimeConverter;
import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.model.PushDetailsResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PushResponseConverter {
    public List<PushDetailsResponse> convert(List<Push> pushList) {

        return pushList.stream().map(this::convert).toList();
    }

    public PushDetailsResponse convert(Push push) {
        if (push == null) {
            return null;
        }
        return new PushDetailsResponse()
            .id(push.getId())
            .title(push.getTitle())
            .content(push.getContent())
            .stockCode(push.getStockCode())
            .stockName(getStockName(push))
            .stockGroupId(push.getStockGroupId())
            .stockGroupName(getStockGroupName(push))
            .postId(push.getPostId())
            .linkUrl(push.getLinkUrl())
            .linkType(push.getLinkType().name())
            .stockTargetType(push.getPushTargetType().name())
            .sendType(push.getSendType().name())
            .topic(push.getTopic())
            .sendStatus(push.getSendStatus().name())
            .result(push.getResult())
            .targetDatetime(DateTimeConverter.convert(push.getTargetDatetime()))
            .sentStartDatetime(DateTimeConverter.convert(push.getSentStartDatetime()))
            .sentEndDatetime(DateTimeConverter.convert(push.getSentEndDatetime()))
            .createdAt(DateTimeConverter.convert(push.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(push.getUpdatedAt()));
    }

    private String getStockGroupName(Push push) {
        return Optional.ofNullable(push.getStockGroup())
            .map(StockGroup::getName)
            .orElse(null);
    }

    private String getStockName(Push push) {
        return Optional.ofNullable(push.getStock())
            .map(Stock::getName)
            .orElse(null);
    }
}
