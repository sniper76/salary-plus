package ag.act.service.stockboardgrouppost.search;

import ag.act.dto.GetPostsSearchDto;
import ag.act.entity.Post;
import ag.act.enums.admin.PostSearchType;
import org.springframework.data.domain.Page;

public interface StockBoardGroupPostSearchService {

    boolean supports(PostSearchType postSearchType);

    Page<Post> getBoardPosts(GetPostsSearchDto getPostsSearchDto);
}
