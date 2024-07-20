package ag.act.converter;

import ag.act.converter.post.PostResponseConverter;
import ag.act.converter.post.SimplePostResponseConverter;
import ag.act.dto.campaign.CampaignDetailsResponseSourceDto;
import ag.act.dto.campaign.SimpleCampaignPostDto;
import ag.act.entity.Campaign;
import ag.act.entity.Post;
import ag.act.entity.StockGroup;
import ag.act.model.CampaignDetailsResponse;
import ag.act.model.SimplePostResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CampaignDetailsResponseConverter {
    private final PostResponseConverter postResponseConverter;
    private final SimplePostResponseConverter simplePostResponseConverter;

    public CampaignDetailsResponse convert(CampaignDetailsResponseSourceDto campaignDetailsResponseSourceDto) {

        final Campaign campaign = campaignDetailsResponseSourceDto.getCampaign();
        final StockGroup stockGroup = campaignDetailsResponseSourceDto.getStockGroup();
        final Post sourcePost = campaignDetailsResponseSourceDto.getSourcePost();

        return new CampaignDetailsResponse()
            .sourceStockGroupId(stockGroup.getId())
            .sourceStockGroupName(stockGroup.getName())
            .sourcePost(postResponseConverter.convert(sourcePost))
            .campaignPosts(getSimplePostResponses(campaignDetailsResponseSourceDto))
            .id(campaign.getId())
            .title(campaign.getTitle())
            .status(campaign.getStatus())
            .createdAt(DateTimeConverter.convert(campaign.getCreatedAt()))
            .updatedAt(DateTimeConverter.convert(campaign.getUpdatedAt()))
            .deletedAt(DateTimeConverter.convert(campaign.getDeletedAt()))
            ;
    }

    private List<SimplePostResponse> getSimplePostResponses(CampaignDetailsResponseSourceDto campaignDetailsResponseSourceDto) {
        final List<Post> campaignPosts = campaignDetailsResponseSourceDto.getCampaignPosts();
        if (CollectionUtils.isNotEmpty(campaignPosts)) {
            return simplePostResponseConverter.convertPosts(campaignPosts);
        }

        final List<SimpleCampaignPostDto> simpleCampaignPostDtos = campaignDetailsResponseSourceDto.getSimpleCampaignPostDtos();
        if (CollectionUtils.isNotEmpty(simpleCampaignPostDtos)) {
            return simplePostResponseConverter.convert(simpleCampaignPostDtos);
        }

        return null;
    }
}
