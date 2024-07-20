package ag.act.converter.post;

import ag.act.entity.DigitalProxy;
import ag.act.entity.Post;
import ag.act.model.CreatePostRequest;
import ag.act.service.digitaldocument.modusign.DigitalProxyModuSignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DigitalProxyCreateRequestConverter {
    private final DigitalProxyModuSignService digitalProxyModuSignService;

    public DigitalProxy convert(CreatePostRequest createPostRequest, Post post) {
        final DigitalProxy digitalProxy = digitalProxyModuSignService.makeDigitalProxy(createPostRequest);
        digitalProxy.setPost(post);

        return digitalProxy;
    }
}
