package ag.act.service.stockboardgrouppost.search;

import ag.act.dto.GetPostsSearchDto;
import ag.act.entity.Post;
import ag.act.enums.admin.PostSearchType;
import ag.act.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StockBoardGroupPostSearchByTitleAndContentService implements StockBoardGroupPostSearchService {

    private final PostRepository postRepository;

    @Override
    public boolean supports(PostSearchType postSearchType) {
        return postSearchType == PostSearchType.TITLE_AND_CONTENT;
    }

    @Override
    public Page<Post> getBoardPosts(GetPostsSearchDto getPostsSearchDto) {
        return postRepository.findAllByStatusInAndBoardCategoryInAndTitleContainingOrContentContainingAndActiveDateBetween(
            getPostsSearchDto.getStatuses(),
            getPostsSearchDto.getBoardCategories(),
            getPostsSearchDto.getSearchKeyword(),
            getPostsSearchDto.getSearchStartDate(),
            getPostsSearchDto.getSearchEndDate(),
            getPostsSearchDto.getPageRequest()
        );
    }
}
