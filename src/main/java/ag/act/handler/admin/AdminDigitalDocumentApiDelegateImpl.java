package ag.act.handler.admin;

import ag.act.api.AdminDigitalDocumentApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.IsCmsUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.SimplePageDto;
import ag.act.dto.digitaldocument.DigitalDocumentUserSearchDto;
import ag.act.facade.digitaldocument.DigitalDocumentDownloadFacade;
import ag.act.facade.stock.StockReferenceDateFacade;
import ag.act.model.CreateStockReferenceDateRequest;
import ag.act.model.DigitalDocumentUserDetailsDataResponse;
import ag.act.model.DigitalDocumentUserDetailsResponse;
import ag.act.model.PreviewDigitalDocumentRequest;
import ag.act.model.SimpleStringResponse;
import ag.act.model.StockReferenceDateDataResponse;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.digitaldocument.DigitalDocumentUserService;
import ag.act.util.DownloadFileUtil;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@UseGuards({IsActiveUserGuard.class})
public class AdminDigitalDocumentApiDelegateImpl implements AdminDigitalDocumentApiDelegate {
    private final DigitalDocumentService digitalDocumentService;
    private final DigitalDocumentUserService digitalDocumentUserService;
    private final DigitalDocumentDownloadFacade digitalDocumentDownloadFacade;
    private final PageDataConverter pageDataConverter;
    private final StockReferenceDateFacade stockReferenceDateFacade;

    @Override
    public ResponseEntity<Resource> previewDigitalDocument(PreviewDigitalDocumentRequest previewDigitalDocumentRequest) {
        return DownloadFileUtil.ok(digitalDocumentService.previewDigitalDocument(previewDigitalDocumentRequest));
    }

    @Override
    public ResponseEntity<SimpleStringResponse> createDigitalDocumentZipFile(Long digitalDocumentId, Boolean isSecured) {
        digitalDocumentDownloadFacade.createDigitalDocumentZipFile(digitalDocumentId, isSecured);
        return SimpleStringResponseUtil.okResponse();
    }

    public ResponseEntity<Resource> downloadDigitalDocumentUserResponseInCsv(Long digitalDocumentId) {
        return DownloadFileUtil.ok(digitalDocumentDownloadFacade.downloadUserResponseInCsv(digitalDocumentId));
    }

    @UseGuards({IsCmsUserGuard.class})
    @Override
    public ResponseEntity<DigitalDocumentUserDetailsDataResponse> getDigitalDocumentUsers(
        Long digitalDocumentId,
        String searchType,
        String searchKeyword,
        Integer page,
        Integer size,
        List<String> sorts
    ) {
        final SimplePageDto<DigitalDocumentUserDetailsResponse> digitalDocumentUsers = digitalDocumentUserService.getDigitalDocumentUsers(
            new DigitalDocumentUserSearchDto(
                digitalDocumentId,
                searchType,
                searchKeyword,
                pageDataConverter.convert(page, size, sorts)
            )
        );

        return ResponseEntity.ok(pageDataConverter.convert(digitalDocumentUsers, DigitalDocumentUserDetailsDataResponse.class));
    }

    @Override
    public ResponseEntity<StockReferenceDateDataResponse> updateDigitalDocumentReferenceDate(
        Long digitalDocumentId, Long referenceDateId, CreateStockReferenceDateRequest createStockReferenceDateRequest
    ) {
        return ResponseEntity.ok(
            new ag.act.model.StockReferenceDateDataResponse().data(
                stockReferenceDateFacade.updateDigitalDocumentStockReferenceDate(
                    digitalDocumentId, referenceDateId, createStockReferenceDateRequest
                )
            )
        );
    }
}
