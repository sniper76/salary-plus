package ag.act.converter.post;

import ag.act.converter.PageDataConverter;
import ag.act.dto.SimplePageDto;
import ag.act.entity.Post;
import ag.act.model.GetBoardGroupPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardGroupPostResponseConverter {
    private final PostListResponseConverter adminPostResponseConverter;
    private final PageDataConverter pageDataConverter;

    public GetBoardGroupPostResponse convert(Page<Post> pagePost) {
        SimplePageDto<ag.act.model.PostResponse> simplePage = new SimplePageDto<>(
            pagePost.map(adminPostResponseConverter::convert)
        );

        return pageDataConverter.convert(simplePage, GetBoardGroupPostResponse.class);
    }
}

