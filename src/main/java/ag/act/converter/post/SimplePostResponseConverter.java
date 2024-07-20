package ag.act.converter.post;

import ag.act.converter.stock.SimpleStockResponseConverter;
import ag.act.dto.campaign.SimpleCampaignPostDto;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SimplePostResponseConverter {
    private final SimpleStockResponseConverter simpleStockResponseConverter;

    public ag.act.model.SimplePostResponse convertPost(Post post) {
        Stock boardStock = post.getBoard().getStock();

        return new ag.act.model.SimplePostResponse()
            .postId(post.getId())
            .stock(simpleStockResponseConverter.convert(boardStock));
    }

    public List<ag.act.model.SimplePostResponse> convertPosts(List<Post> posts) {
        if (CollectionUtils.isEmpty(posts)) {
            return null;
        }

        return posts.stream()
            .map(this::convertPost)
            .toList();
    }

    public ag.act.model.SimplePostResponse convert(SimpleCampaignPostDto simpleCampaignPostDto) {
        return new ag.act.model.SimplePostResponse()
            .postId(simpleCampaignPostDto.getPostId())
            .stock(
                new ag.act.model.SimpleStockResponse()
                    .code(simpleCampaignPostDto.getCode())
                    .name(simpleCampaignPostDto.getName())
            );
    }

    public List<ag.act.model.SimplePostResponse> convert(List<SimpleCampaignPostDto> simpleCampaignPostDtos) {
        if (CollectionUtils.isEmpty(simpleCampaignPostDtos)) {
            return null;
        }

        return simpleCampaignPostDtos.stream()
            .map(this::convert)
            .toList();
    }
}
