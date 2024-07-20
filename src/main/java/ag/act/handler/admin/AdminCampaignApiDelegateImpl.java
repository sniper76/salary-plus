package ag.act.handler.admin;

import ag.act.api.AdminCampaignApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.annotation.HtmlContentTarget;
import ag.act.core.guard.IsAdminGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.admin.GetCampaignsSearchDto;
import ag.act.facade.admin.campaign.AdminCampaignFacade;
import ag.act.model.CampaignDetailsDataResponse;
import ag.act.model.CreateCampaignRequest;
import ag.act.model.GetCampaignsDataResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.UpdateCampaignRequest;
import ag.act.util.DownloadFileUtil;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@UseGuards(IsAdminGuard.class)
public class AdminCampaignApiDelegateImpl implements AdminCampaignApiDelegate {

    private final AdminCampaignFacade adminCampaignFacade;
    private final PageDataConverter pageDataConverter;

    @Override
    public ResponseEntity<CampaignDetailsDataResponse> createCampaign(@HtmlContentTarget CreateCampaignRequest createCampaignRequest) {
        return ResponseEntity.ok(
            new CampaignDetailsDataResponse().data(adminCampaignFacade.createCampaign(createCampaignRequest))
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> createCampaignDigitalDocumentZipFile(Long campaignId, Boolean isSecured) {
        adminCampaignFacade.createDigitalDocumentZipFile(campaignId, isSecured);
        return SimpleStringResponseUtil.okResponse();
    }

    @Override
    public ResponseEntity<Resource> downloadCampaignUserResponseInCsv(Long campaignId) {
        return DownloadFileUtil.ok(adminCampaignFacade.downloadCampaignUserResponseInCsv(campaignId));
    }

    @Override
    public ResponseEntity<CampaignDetailsDataResponse> getCampaignDetailsAdmin(Long campaignId) {
        return ResponseEntity.ok(
            new CampaignDetailsDataResponse().data(adminCampaignFacade.getCampaignDetails(campaignId))
        );
    }

    @Override
    public ResponseEntity<GetCampaignsDataResponse> getCampaigns(
        String searchType,
        String searchKeyword,
        String boardCategory,
        Integer page,
        Integer size,
        List<String> sorts
    ) {
        final GetCampaignsSearchDto getCampaignsSearchDto = new GetCampaignsSearchDto()
            .searchKeyword(searchKeyword)
            .searchType(searchType)
            .boardCategory(boardCategory)
            .pageRequest(pageDataConverter.convert(page, size, sorts));

        return ResponseEntity.ok(adminCampaignFacade.getCampaigns(getCampaignsSearchDto));
    }

    @Override
    public ResponseEntity<CampaignDetailsDataResponse> updateCampaign(
        Long campaignId,
        @HtmlContentTarget UpdateCampaignRequest updateCampaignRequest
    ) {
        return ResponseEntity.ok(
            new CampaignDetailsDataResponse().data(adminCampaignFacade.updateCampaign(campaignId, updateCampaignRequest))
        );
    }

    @Override
    public ResponseEntity<SimpleStringResponse> deleteCampaign(Long campaignId) {
        return ResponseEntity.ok(
            adminCampaignFacade.deleteCampaign(campaignId)
        );
    }
}
