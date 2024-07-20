package ag.act.service.stockboardgrouppost.search;

import ag.act.dto.GetPostsSearchDto;
import ag.act.entity.Post;
import ag.act.enums.admin.PostSearchType;
import ag.act.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@SuppressWarnings("LineLength")
@RequiredArgsConstructor
@Service
public class StockBoardGroupPostSearchByTitleService implements StockBoardGroupPostSearchService {

    private final PostRepository postRepository;

    @Override
    public boolean supports(PostSearchType postSearchType) {
        return postSearchType == PostSearchType.TITLE;
    }

    @Override
    public Page<Post> getBoardPosts(GetPostsSearchDto getPostsSearchDto) {
        return postRepository.findAllByTitleContainingAndBoardCategoryInAndStatusInAndIsActiveBetween(
            getPostsSearchDto.getSearchKeyword(),
            getPostsSearchDto.getBoardCategories(),
            getPostsSearchDto.getStatuses(),
            getPostsSearchDto.getSearchStartDate(),
            getPostsSearchDto.getSearchEndDate(),
            getPostsSearchDto.getPageRequest()
        );
    }
}
