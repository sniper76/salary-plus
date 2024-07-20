package ag.act.configuration.urlmatcher.postendpoint;

import ag.act.enums.BoardCategory;
import ag.act.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardCategoryFinder {
    private final PostService postService;

    public BoardCategory findBoardCategory(Long postId) {
        return postService.getPost(postId).getBoard().getCategory();
    }
}
