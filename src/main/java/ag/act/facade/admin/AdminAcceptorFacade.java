package ag.act.facade.admin;


import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.PageDataConverter;
import ag.act.converter.post.PostResponseConverter;
import ag.act.core.guard.IsAcceptorUserRoleGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.GetPostDigitalDocumentSearchDto;
import ag.act.dto.SimplePageDto;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.model.GetPostDigitalDocumentDataResponse;
import ag.act.service.post.PostService;
import ag.act.validator.GetDigitalDocumentOfAcceptorRequestValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminAcceptorFacade {
    private final PostService postService;
    private final PageDataConverter pageDataConverter;
    private final PostResponseConverter postResponseConverter;
    private final GetDigitalDocumentOfAcceptorRequestValidator getDigitalDocumentOfAcceptorRequestValidator;

    @UseGuards({IsAcceptorUserRoleGuard.class})
    public GetPostDigitalDocumentDataResponse getDigitalDocumentPosts(GetPostDigitalDocumentSearchDto searchDto) {

        getDigitalDocumentOfAcceptorRequestValidator.validate(searchDto);

        final Page<Post> pagePost = postService.getDigitalDocumentPostsOfAcceptor(searchDto);
        final User currentUser = ActUserProvider.getNoneNull();

        return pageDataConverter.convert(
            new SimplePageDto<>(pagePost.map(post -> postResponseConverter.convert(post, Boolean.TRUE, currentUser))),
            GetPostDigitalDocumentDataResponse.class
        );
    }
}
