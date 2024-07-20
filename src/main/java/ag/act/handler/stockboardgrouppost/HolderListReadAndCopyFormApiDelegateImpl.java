package ag.act.handler.stockboardgrouppost;

import ag.act.api.HolderListReadAndCopyFormApiDelegate;
import ag.act.core.guard.IsSolidarityLeaderGuard;
import ag.act.core.guard.UseGuards;
import ag.act.model.HolderListReadAndCopyFormResponse;
import ag.act.service.stockboardgrouppost.holderlistreadandcopy.HolderListReadAndCopyFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolderListReadAndCopyFormApiDelegateImpl implements HolderListReadAndCopyFormApiDelegate {

    private final HolderListReadAndCopyFormService holderListReadAndCopyFormService;

    @Override
    @UseGuards({IsSolidarityLeaderGuard.class})
    public ResponseEntity<HolderListReadAndCopyFormResponse> getHolderListReadAndCopyForm(
        String stockCode,
        String boardGroupName
    ) {
        return ResponseEntity.ok(
            holderListReadAndCopyFormService.getHolderListReadAndCopyForm(
                stockCode, boardGroupName
            )
        );
    }
}
