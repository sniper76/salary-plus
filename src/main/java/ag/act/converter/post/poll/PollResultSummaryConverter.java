package ag.act.converter.post.poll;

import ag.act.dto.poll.PollResultSummaryDto;
import ag.act.repository.interfaces.PollItemCount;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PollResultSummaryConverter {
    public PollResultSummaryDto convert(List<PollItemCount> pollItemCountList) {
        return new PollResultSummaryDto(
            pollItemCountList.stream().mapToInt(PollItemCount::getJoinCnt).sum(),
            pollItemCountList.stream().mapToLong(PollItemCount::getStockQuantity).sum()
        );
    }
}
