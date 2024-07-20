package ag.act.facade.admin.campaign;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.CampaignConverter;
import ag.act.converter.CampaignDetailsResponseConverter;
import ag.act.converter.PageDataConverter;
import ag.act.dto.SimplePageDto;
import ag.act.dto.admin.GetCampaignsSearchDto;
import ag.act.dto.campaign.CampaignDetailsResponseSourceDto;
import ag.act.dto.campaign.SimpleCampaignPostDto;
import ag.act.dto.download.DownloadFile;
import ag.act.dto.post.CreatePostRequestDto;
import ag.act.entity.Campaign;
import ag.act.entity.Post;
import ag.act.entity.StockGroup;
import ag.act.exception.BadRequestException;
import ag.act.facade.post.PostFacade;
import ag.act.model.CampaignDetailsResponse;
import ag.act.model.CampaignResponse;
import ag.act.model.CreateCampaignRequest;
import ag.act.model.GetCampaignsDataResponse;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import ag.act.model.UpdateCampaignRequest;
import ag.act.service.digitaldocument.campaign.CampaignService;
import ag.act.service.digitaldocument.campaign.CampaignStockMappingService;
import ag.act.service.post.PostService;
import ag.act.service.stock.StockGroupMappingService;
import ag.act.service.stock.StockGroupService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostCreateService;
import ag.act.util.SimpleStringResponseUtil;
import ag.act.validator.document.CampaignValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class AdminCampaignFacade {
    private final StockBoardGroupPostCreateService stockBoardGroupPostCreateService;
    private final StockGroupMappingService stockGroupMappingService;
    private final StockGroupService stockGroupService;
    private final PostFacade postFacade;
    private final PostService postService;
    private final CampaignValidator campaignValidator;
    private final CampaignService campaignService;
    private final CampaignStockMappingService campaignStockMappingService;
    private final CampaignDetailsResponseConverter campaignDetailsResponseConverter;
    private final CampaignDownloadFacade campaignDownloadFacade;
    private final CampaignConverter campaignConverter;
    private final PageDataConverter pageDataConverter;

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public CampaignDetailsResponse createCampaign(CreateCampaignRequest createCampaignRequest) {

        campaignValidator.validate(createCampaignRequest);

        final List<String> uniqueStockCodes = getUniqueStockCodes(createCampaignRequest.getStockGroupId());

        final PostDetailsDataResponse postDetailsDataResponse = createPost(
            createCampaignRequest,
            uniqueStockCodes.stream().findFirst().get()
        );

        final Long postId = postDetailsDataResponse.getData().getId();
        final Campaign savedCampaign = createCampaign(createCampaignRequest, postId);
        campaignStockMappingService.createMappings(savedCampaign.getId(), uniqueStockCodes);
        postFacade.duplicatePosts(postId, uniqueStockCodes);

        return campaignDetailsResponseConverter.convert(
            CampaignDetailsResponseSourceDto.builder()
                .campaign(savedCampaign)
                .stockGroup(getSourceStockGroup(createCampaignRequest.getStockGroupId()))
                .sourcePost(getPostOf(savedCampaign))
                .build()
        );
    }

    private Campaign createCampaign(CreateCampaignRequest createCampaignRequest, Long sourcePostId) {
        return campaignService.save(Campaign.builder()
            .title(createCampaignRequest.getTitle())
            .sourceStockGroupId(createCampaignRequest.getStockGroupId())
            .sourcePostId(sourcePostId)
            .status(Status.ACTIVE)
            .build()
        );
    }

    public CampaignDetailsResponse getCampaignDetails(Long campaignId) {
        final Campaign savedCampaign = getCampaignNoneNull(campaignId);
        final StockGroup sourceStockGroup = getSourceStockGroup(savedCampaign.getSourceStockGroupId());
        final Post sourcePost = getPostOf(savedCampaign);
        final List<SimpleCampaignPostDto> simpleCampaignPosts = campaignService.getSimpleCampaignPostDtos(sourcePost);

        return campaignDetailsResponseConverter.convert(
            CampaignDetailsResponseSourceDto.builder()
                .campaign(savedCampaign)
                .stockGroup(sourceStockGroup)
                .sourcePost(sourcePost)
                .simpleCampaignPostDtos(simpleCampaignPosts)
                .build()
        );
    }

    public GetCampaignsDataResponse getCampaigns(GetCampaignsSearchDto getCampaignsSearchDto) {
        final Page<Campaign> campaignPage = campaignService.getCampaignPage(getCampaignsSearchDto);

        final SimplePageDto<CampaignResponse> campaignList = new SimplePageDto<>(campaignPage.map(campaignConverter));

        return pageDataConverter.convert(campaignList, GetCampaignsDataResponse.class);
    }

    private StockGroup getSourceStockGroup(Long stockGroupId) {
        return stockGroupService.findById(stockGroupId)
            .orElseThrow(() -> new BadRequestException("종목그룹이 존재하지 않습니다."));
    }

    private PostDetailsDataResponse createPost(CreateCampaignRequest createCampaignRequest, String firstStockCode) {
        return stockBoardGroupPostCreateService.createBoardGroupPost(
            new CreatePostRequestDto(
                firstStockCode,
                createCampaignRequest.getBoardGroupName(),
                createCampaignRequest.getCreatePostRequest(),
                ActUserProvider.getNoneNull()
            )
        );
    }

    private List<String> getUniqueStockCodes(Long stockGroupId) {
        final List<String> allStockCodes = stockGroupMappingService.getAllStockCodes(stockGroupId);
        if (CollectionUtils.isEmpty(allStockCodes)) {
            throw new BadRequestException("해당 종목그룹에 매핑된 종목이 없습니다.");
        }
        return allStockCodes;
    }

    public void createDigitalDocumentZipFile(Long campaignId, Boolean isSecured) {
        campaignDownloadFacade.createDigitalDocumentZipFile(
            campaignId,
            getAllDigitalDocumentIds(getCampaignWithPost(campaignId)),
            isSecured
        );
    }

    private Campaign getCampaignWithPost(Long campaignId) {
        final Campaign campaign = getCampaignNoneNull(campaignId);

        validatePostOf(campaign);

        return campaign;
    }

    private Campaign getCampaignNoneNull(Long campaignId) {
        return campaignService.getCampaign(campaignId);
    }

    private void validatePostOf(Campaign campaign) {
        getPostOf(campaign);
    }

    private Post getPostOf(Campaign campaign) {
        return campaignService.getSourcePostOf(campaign);
    }

    private List<Long> getAllDigitalDocumentIds(Campaign campaign) {
        final List<Long> digitalDocumentIds = campaignService.findAllDigitalDocumentIdsBySourcePostId(campaign.getSourcePostId());

        if (CollectionUtils.isEmpty(digitalDocumentIds)) {
            throw new BadRequestException("캠페인의 전자문서가 존재하지 않습니다.");
        }

        return digitalDocumentIds;
    }

    public DownloadFile downloadCampaignUserResponseInCsv(Long campaignId) {
        return campaignDownloadFacade.downloadUserResponseInCsv(campaignId);
    }

    public CampaignDetailsResponse updateCampaign(Long campaignId, UpdateCampaignRequest updateCampaignRequest) {
        // 캠페인 제목 수정
        final Campaign savedCampaign = campaignService.updateCampaignTitle(campaignId, updateCampaignRequest.getTitle());
        postService.updatePostListByCampaign(savedCampaign.getSourcePostId(), updateCampaignRequest);

        final StockGroup sourceStockGroup = getSourceStockGroup(savedCampaign.getSourceStockGroupId());
        final Post sourcePost = getPostOf(savedCampaign);

        return campaignDetailsResponseConverter.convert(
            CampaignDetailsResponseSourceDto.builder()
                .campaign(savedCampaign)
                .stockGroup(sourceStockGroup)
                .sourcePost(sourcePost)
                .build()
        );
    }

    public SimpleStringResponse deleteCampaign(Long campaignId) {
        campaignService.deleteCampaign(campaignId);
        return SimpleStringResponseUtil.ok();
    }
}
