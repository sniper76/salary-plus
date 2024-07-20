package ag.act.facade.virtualboard;

import ag.act.converter.post.BoardGroupPostResponseConverter;
import ag.act.dto.GetBoardGroupPostDto;
import ag.act.entity.Post;
import ag.act.service.virtualboard.VirtualBoardGroupPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VirtualBoardGroupPostFacade {
    private final VirtualBoardGroupPostService virtualBoardGroupPostService;
    private final BoardGroupPostResponseConverter boardGroupPostResponseConverter;

    public ag.act.model.GetBoardGroupPostResponse getVirtualBoardGroupPosts(
        GetBoardGroupPostDto getBoardGroupPostDto,
        PageRequest pageRequest
    ) {
        final Page<Post> boardPosts = virtualBoardGroupPostService.getBestPosts(
            getBoardGroupPostDto,
            pageRequest
        );

        return boardGroupPostResponseConverter.convert(boardPosts);
    }
}
