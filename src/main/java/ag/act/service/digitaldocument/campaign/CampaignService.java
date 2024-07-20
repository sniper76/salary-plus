package ag.act.service.digitaldocument.campaign;

import ag.act.dto.admin.GetCampaignsSearchDto;
import ag.act.dto.campaign.SimpleCampaignPostDto;
import ag.act.entity.Campaign;
import ag.act.entity.Post;
import ag.act.enums.admin.CampaignSearchType;
import ag.act.exception.BadRequestException;
import ag.act.model.Status;
import ag.act.repository.CampaignRepository;
import ag.act.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final PostService postService;

    public Campaign save(Campaign campaign) {
        return campaignRepository.saveAndFlush(campaign);
    }

    public Page<Campaign> getCampaignPage(GetCampaignsSearchDto getCampaignsSearchDto) {
        if (getCampaignsSearchDto.isBlankSearchKeyword()) {
            return campaignRepository.findAllByBoardCategory(
                getCampaignsSearchDto.getNullableBoardCategoryName(),
                getCampaignsSearchDto.getPageRequest()
            );
        }

        if (getCampaignsSearchDto.getSearchType() == CampaignSearchType.STOCK_GROUP_NAME) {
            return campaignRepository.findAllByBoardCategoryAndStockGroupNameContaining(
                getCampaignsSearchDto.getNullableBoardCategoryName(),
                getCampaignsSearchDto.getSearchKeyword(),
                getCampaignsSearchDto.getPageRequest()
            );
        }

        return campaignRepository.findAllByBoardCategoryAndTitleContaining(
            getCampaignsSearchDto.getNullableBoardCategoryName(),
            getCampaignsSearchDto.getSearchKeyword(),
            getCampaignsSearchDto.getPageRequest()
        );
    }

    public Campaign getCampaign(Long campaignId) {
        return findCampaign(campaignId)
            .orElseThrow(() -> new BadRequestException("캠페인이 존재하지 않습니다."));
    }

    public Optional<Campaign> findCampaign(Long campaignId) {
        return campaignRepository.findById(campaignId);
    }

    public List<Long> findAllDigitalDocumentIdsBySourcePostId(Long postId) {
        return campaignRepository.findAllDigitalDocumentIdsBySourcePostId(postId);
    }

    public Campaign updateCampaignTitle(Long campaignId, String title) {
        final Campaign campaign = getCampaign(campaignId);
        campaign.setTitle(title);
        return save(campaign);
    }

    public void deleteCampaign(Long campaignId) {
        final Campaign campaign = getCampaign(campaignId);
        campaign.setStatus(Status.DELETED);
        save(campaign);

        postService.saveAll(
            getCampaignPosts(campaign)
                .stream()
                .peek(it -> it.setStatus(Status.DELETED))
                .toList()
        );
    }

    public Post getSourcePostOf(Campaign campaign) {
        return postService.findById(campaign.getSourcePostId())
            .orElseThrow(() -> new BadRequestException("캠페인의 게시글이 존재하지 않습니다."));
    }

    public List<Post> getCampaignPosts(Campaign campaign) {
        return getCampaignPosts(getSourcePostOf(campaign));
    }

    public List<Post> getCampaignPosts(Post sourcePost) {
        return Stream.concat(
            Stream.of(sourcePost),
            postService.getAllPostsBySourcePostId(sourcePost.getId()).stream()
        ).toList();
    }

    public List<SimpleCampaignPostDto> getSimpleCampaignPostDtos(Post sourcePost) {
        return Stream.concat(
            Stream.of(toSimpleCampaignPostDto(sourcePost)),
            postService.getPostsWithStockBySourcePostId(sourcePost.getId()).stream()
        ).toList();
    }

    @NotNull
    private SimpleCampaignPostDto toSimpleCampaignPostDto(Post post) {
        return new SimpleCampaignPostDto(
            post.getId(),
            post.getBoard().getStock().getCode(),
            post.getBoard().getStock().getName()
        );
    }

    public Long getSourcePostId(Long campaignId) {
        return campaignRepository.findSorucePostIdByCampaignId(campaignId)
            .orElseThrow(() -> new BadRequestException("캠페인이 존재하지 않습니다."));
    }
}
