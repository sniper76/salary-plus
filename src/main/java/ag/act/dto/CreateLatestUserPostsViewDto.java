package ag.act.dto;

import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.PostsViewType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class CreateLatestUserPostsViewDto {
    private Stock stock;
    private User user;
    private BoardGroup boardGroup;
    private BoardCategory boardCategory;
    private PostsViewType postsViewType;
}
