package ag.act.service.stockboardgrouppost.postpush;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostPushUpdaterResolver {

    private final List<PostPushUpdater> postPushUpdaters;

    public void update(PostPushUpdaterInput input) {
        postPushUpdaters.stream()
            .filter(it -> it.supports(input))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("PostPushUpdater not found for input: " + input))
            .update(input);
    }
}
