package ag.act.dto.campaign;

import ag.act.entity.Campaign;
import ag.act.entity.Post;
import ag.act.entity.StockGroup;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@EqualsAndHashCode
@Getter
@Builder
public class CampaignDetailsResponseSourceDto {
    private Campaign campaign;
    private StockGroup stockGroup;
    private Post sourcePost;
    private List<Post> campaignPosts;
    private List<SimpleCampaignPostDto> simpleCampaignPostDtos;
}
