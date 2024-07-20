package ag.act.service.stockboardgrouppost.postpush;

import ag.act.util.StatusUtil;
import org.springframework.stereotype.Component;

@Component
public class InactiveToInactivePostPushUpdater implements PostPushUpdater {

    @Override
    public boolean supports(PostPushUpdaterInput input) {
        return StatusUtil.getInactiveStatuses().contains(input.postOriginalStatus()) && !input.isActiveForUpdate();
    }

    @Override
    public void update(PostPushUpdaterInput input) {
        // do nothing
    }
}
