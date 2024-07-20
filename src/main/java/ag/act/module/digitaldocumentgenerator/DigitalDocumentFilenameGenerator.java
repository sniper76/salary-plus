package ag.act.module.digitaldocumentgenerator;

import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.exception.NotFoundException;
import ag.act.module.digitaldocumentgenerator.dto.DigitalDocumentFilenameDto;
import ag.act.service.post.PostService;
import ag.act.service.stock.StockService;
import ag.act.service.user.UserService;
import ag.act.util.FilenameUtil;
import org.springframework.stereotype.Service;

@Service
public class DigitalDocumentFilenameGenerator {

    private final StockService stockService;
    private final PostService postService;
    private final UserService userService;

    public DigitalDocumentFilenameGenerator(StockService stockService, PostService postService, UserService userService) {
        this.stockService = stockService;
        this.postService = postService;
        this.userService = userService;
    }

    public String generate(DigitalDocument digitalDocument, Long userId) {
        return FilenameUtil.getDigitalDocumentFilename(
            validateAndBuildDto(digitalDocument, userId)
        );
    }

    public String generateHolderListReadAndCopyFilename(DigitalDocument digitalDocument, String suffix) {
        return FilenameUtil.getHolderListReadAndCopyFilename(
            digitalDocument.getCompanyName(), suffix, "pdf"
        );
    }

    public String generateCertificationFilename(DigitalDocument digitalDocument, Long userId) {
        return FilenameUtil.getDigitalDocumentCertificationFilename(
            validateAndBuildDto(digitalDocument, userId)
        );
    }

    private DigitalDocumentFilenameDto validateAndBuildDto(DigitalDocument digitalDocument, Long userId) {
        final Stock stock = stockService.findByCode(digitalDocument.getStockCode())
            .orElseThrow(() -> new NotFoundException("전자문서의 종목을 찾을 수 없습니다."));

        final Post post = postService.findById(digitalDocument.getPostId())
            .orElseThrow(() -> new NotFoundException("전자문서의 게시글을 찾을 수 없습니다."));

        final User user = userService.getUser(userId);

        return DigitalDocumentFilenameDto.builder()
            .stockName(stock.getName())
            .postTitle(post.getTitle())
            .userName(user.getName())
            .userBirthDate(user.getBirthDate())
            .build();
    }
}
