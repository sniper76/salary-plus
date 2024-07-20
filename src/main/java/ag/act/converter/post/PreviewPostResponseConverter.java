package ag.act.converter.post;

import ag.act.entity.Post;
import ag.act.model.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PreviewPostResponseConverter {
    private final PostResponseConverter postResponseConverter;

    public List<PostResponse> convert(List<Post> posts) {
        return posts.stream()
            .map(this::convert)
            .toList();
    }

    public PostResponse convert(Post post) {
        return postResponseConverter.convert(post, Boolean.FALSE);
    }
}
