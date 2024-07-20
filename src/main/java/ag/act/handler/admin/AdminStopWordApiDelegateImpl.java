package ag.act.handler.admin;

import ag.act.api.AdminStopWordApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.model.CreateStopWordRequest;
import ag.act.model.GetStopWordResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.StopWordDataResponse;
import ag.act.model.UpdateStopWordRequest;
import ag.act.service.StopWordService;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@UseGuards(IsAdminGuard.class)
public class AdminStopWordApiDelegateImpl implements AdminStopWordApiDelegate {

    private final StopWordService stopWordService;
    private final PageDataConverter pageDataConverter;

    @Override
    public ResponseEntity<GetStopWordResponse> getStopWords(
        String filterType,
        String searchKeyword,
        Integer page,
        Integer size,
        List<String> sorts
    ) {
        final PageRequest pageRequest = pageDataConverter.convert(page, size, sorts);

        return ResponseEntity.ok(
            pageDataConverter.convert(
                stopWordService.getStopWords(filterType, searchKeyword, pageRequest),
                GetStopWordResponse.class
            )
        );
    }

    @Override
    public ResponseEntity<StopWordDataResponse> createStopWord(CreateStopWordRequest createStopWordRequest) {
        return ResponseEntity.ok(
            new StopWordDataResponse()
                .data(stopWordService.create(createStopWordRequest.getWord()))
        );
    }

    @Override
    public ResponseEntity<StopWordDataResponse> updateStopWord(Long id, UpdateStopWordRequest updateStopWordRequest) {
        return ResponseEntity.ok(
            new StopWordDataResponse()
                .data(stopWordService.update(id, updateStopWordRequest))
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> deleteStopWord(Long stopWordId) {

        stopWordService.delete(stopWordId);

        return SimpleStringResponseUtil.okResponse();
    }

}
