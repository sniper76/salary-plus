package ag.act.facade.post;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.PageDataConverter;
import ag.act.converter.PostDigitalDocumentResponseConverter;
import ag.act.dto.PostDuplicateDto;
import ag.act.dto.SimplePageDto;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserProfile;
import ag.act.entity.User;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.BadRequestException;
import ag.act.model.GetPostDigitalDocumentResponse;
import ag.act.model.PostCopyRequest;
import ag.act.model.PostDigitalDocumentResponse;
import ag.act.service.post.PostImageService;
import ag.act.service.post.PostService;
import ag.act.service.post.PostUserProfileService;
import ag.act.service.post.duplication.PostDuplicateService;
import ag.act.service.stock.StockGroupMappingService;
import ag.act.validator.post.PostDuplicateValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostFacade {
    private final PostService postService;
    private final PostDuplicateService postDuplicateService;
    private final StockGroupMappingService stockGroupMappingService;
    private final PostDuplicateValidator postDuplicateValidator;
    private final PostUserProfileService postUserProfileService;
    private final PostImageService postImageService;
    private final PageDataConverter pageDataConverter;
    private final PostDigitalDocumentResponseConverter postDigitalDocumentResponseConverter;

    public int duplicatePosts(Long postId, Long stockGroupId) {
        return duplicatePosts(postId, stockGroupMappingService.getAllStockCodes(stockGroupId));
    }

    public int duplicatePosts(Long postId, List<String> stockCodes) {
        postDuplicateValidator.validateStockCodes(stockCodes);

        return postDuplicateService.duplicatePosts(
            getPostDuplicateDto(postId, stockCodes)
        );
    }

    public ag.act.model.PostDetailsDataResponse duplicatePost(Long postId, PostCopyRequest postCopyRequest) {
        return postDuplicateService.duplicatePost(
            getPostDuplicateDto(postId, List.of(postCopyRequest.getStockCode()))
        );
    }

    private PostDuplicateDto getPostDuplicateDto(Long postId, List<String> stockCodes) {
        final Post post = postDuplicateValidator.validatePostAndGet(postService.getPost(postId));
        final Board board = post.getBoard();

        final PostUserProfile postUserProfile = postUserProfileService.getPostUserProfileNotNull(postId);
        final List<PostImage> postImages = postImageService.findNotDeletedAllByPostId(postId);

        return new PostDuplicateDto(stockCodes, post, board, postUserProfile, postImages);
    }

    public Optional<Post> findPostById(Long postId) {
        return postService.findById(postId);
    }

    public GetPostDigitalDocumentResponse getDigitalDocumentPosts(DigitalDocumentType type, PageRequest pageRequest) {
        if (type != DigitalDocumentType.DIGITAL_PROXY) {
            throw new BadRequestException("전자문서 의결권위임만 조회가 가능합니다.");
        }

        final User user = ActUserProvider.getNoneNull();
        final Page<Post> pagePost = postService.getDigitalDocumentPosts(type, user.getId(), pageRequest);

        SimplePageDto<PostDigitalDocumentResponse> simplePage = new SimplePageDto<>(
            pagePost.map(postDigitalDocumentResponseConverter::convert)
        );

        return pageDataConverter.convert(simplePage, GetPostDigitalDocumentResponse.class);
    }
}
