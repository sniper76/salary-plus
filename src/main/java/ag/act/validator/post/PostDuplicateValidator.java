package ag.act.validator.post;

import ag.act.entity.Post;
import ag.act.enums.DigitalDocumentType;
import ag.act.exception.BadRequestException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostDuplicateValidator {

    public void validateStockCodes(List<String> stockCodes) {
        if (CollectionUtils.isEmpty(stockCodes)) {
            throw new BadRequestException("종목 그룹에 등록된 종목이 없습니다.");
        }
    }

    public Post validatePostAndGet(Post post) {
        if (post.getDigitalProxy() != null) {
            throw new BadRequestException("모두싸인 의결권위임 게시글은 복제할 수 없습니다.");
        }

        if (post.getDigitalDocument() != null
            && post.getDigitalDocument().getType() != DigitalDocumentType.ETC_DOCUMENT) {
            throw new BadRequestException("의결권위임 기타문서 게시글만 복제할 수 있습니다.");
        }

        return post;
    }
}
