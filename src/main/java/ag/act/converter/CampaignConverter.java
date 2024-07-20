package ag.act.converter;

import ag.act.converter.digitaldocument.DigitalDocumentDownloadResponseConverter;
import ag.act.entity.Campaign;
import ag.act.entity.Post;
import ag.act.entity.StockGroup;
import ag.act.model.CampaignResponse;
import ag.act.repository.interfaces.JoinCount;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.digitaldocument.campaign.CampaignStockMappingService;
import ag.act.service.poll.PollAnswerService;
import ag.act.service.post.PostService;
import ag.act.service.stock.StockGroupService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CampaignConverter implements Converter<Campaign, CampaignResponse> {
    private final PostService postService;
    private final PollAnswerService pollAnswerService;
    private final DigitalDocumentService digitalDocumentService;
    private final DigitalDocumentDownloadResponseConverter digitalDocumentDownloadResponseConverter;
    private final CampaignStockMappingService campaignStockMappingService;
    private final StockGroupService stockGroupService;

    public CampaignResponse convert(Campaign campaign) {
        final Long sourcePostId = campaign.getSourcePostId();
        final Post post = postService.getPost(sourcePostId);
        final Optional<JoinCount> joinCount = getJoinCount(sourcePostId, post);
        final Long mappedStocksCount = campaignStockMappingService.countByCampaignId(campaign.getId());
        final StockGroup stockGroup = stockGroupService.findByIdNoneNull(campaign.getSourceStockGroupId());

        return new CampaignResponse()
            .id(campaign.getId())
            .title(campaign.getTitle())
            .sourcePostId(campaign.getSourcePostId())
            .sourceStockGroupId(campaign.getSourceStockGroupId())
            .sourceStockGroupName(stockGroup.getName())
            .status(campaign.getStatus())
            .isDigitalDocument(post.getDigitalDocument() != null)
            .isPoll(post.getFirstPoll() != null)
            .mappedStocksCount(mappedStocksCount)
            .joinStockCount(joinCount.map(JoinCount::getStockQuantity).orElse(0L))
            .joinUserCount(joinCount.map(JoinCount::getJoinCnt).orElse(0))
            .targetEndDate(getTargetEndDate(post))
            .boardCategory(post.getBoard().getCategory().name())
            .stockQuantity(post.getBoard().getStock().getTotalIssuedQuantity())
            .campaignDownload(getCampaignDownload(campaign))
            .createdAt(DateTimeConverter.convert(campaign.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(campaign.getUpdatedAt()))
            .deletedAt(DateTimeConverter.convert(campaign.getDeletedAt()));
    }

    @Nullable
    private ag.act.model.DigitalDocumentDownloadResponse getCampaignDownload(Campaign campaign) {
        return campaign.getLatestCampaignDownload()
            .map(digitalDocumentDownloadResponseConverter::convert)
            .orElse(null);
    }

    private Optional<JoinCount> getJoinCount(Long postId, Post post) {
        if (post.getFirstPoll() != null) {
            return getPollResultSummaryDto(postId);
        }
        if (post.getDigitalDocument() != null) {
            return getDigitalDocumentResultSummaryDto(postId);
        }
        return Optional.empty();
    }

    private Instant getTargetEndDate(Post post) {
        if (post.getFirstPoll() != null) {
            return DateTimeConverter.convert(post.getFirstPoll().getTargetEndDate());
        }
        if (post.getDigitalDocument() != null) {
            return DateTimeConverter.convert(post.getDigitalDocument().getTargetEndDate());
        }
        return null;
    }

    private Optional<JoinCount> getPollResultSummaryDto(Long postId) {
        return pollAnswerService.findPollCountByPostId(postId);
    }

    private Optional<JoinCount> getDigitalDocumentResultSummaryDto(Long postId) {
        return digitalDocumentService.findDigitalDocumentCountByPostId(postId);
    }

    @Override
    public CampaignResponse apply(Campaign campaign) {
        return convert(campaign);
    }
}
