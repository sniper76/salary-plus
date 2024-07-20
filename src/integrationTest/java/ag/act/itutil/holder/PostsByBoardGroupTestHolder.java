package ag.act.itutil.holder;

import ag.act.entity.Post;
import ag.act.enums.BoardGroup;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

public class PostsByBoardGroupTestHolder {

    private boolean isInitialized = false;

    public void initialize(List<Post> posts) {
        if (isInitialized) {
            return;
        }
        isInitialized = true;

        posts.forEach(post -> {
            final BoardGroup boardGroup = post.getBoard().getGroup();
            postsByBoardGroup.add(boardGroup, post);
        });
    }

    private final MultiValueMap<BoardGroup, Post> postsByBoardGroup = new LinkedMultiValueMap<>();

    public Post addOrSet(BoardGroup boardGroup, @NotNull Post post) {
        final List<Post> posts = Optional.ofNullable(postsByBoardGroup.get(boardGroup)).orElse(List.of());

        posts.stream()
            .filter(p -> p.getId().equals(post.getId()))
            .findFirst()
            .ifPresentOrElse(
                p -> posts.set(posts.indexOf(p), post),
                () -> postsByBoardGroup.add(boardGroup, post)
            );

        return post;
    }

    public List<Post> getPosts(BoardGroup boardGroup) {
        return postsByBoardGroup.get(boardGroup);
    }
}
