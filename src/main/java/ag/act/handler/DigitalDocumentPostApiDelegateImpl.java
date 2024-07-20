package ag.act.handler;

import ag.act.api.DigitalDocumentPostApiDelegate;
import ag.act.converter.PageDataConverter;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.enums.DigitalDocumentType;
import ag.act.facade.post.PostFacade;
import ag.act.model.GetPostDigitalDocumentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@UseGuards({IsActiveUserGuard.class})
public class DigitalDocumentPostApiDelegateImpl implements DigitalDocumentPostApiDelegate {
    private final PostFacade postFacade;
    private final PageDataConverter pageDataConverter;

    @Override
    public ResponseEntity<GetPostDigitalDocumentResponse> getDigitalDocumentPosts(
        String digitalDocumentType, Integer page, Integer size, List<String> sorts
    ) {
        sorts.add(0, "sortIndex:asc");
        final PageRequest pageRequest = pageDataConverter.convert(page, size, sorts);
        return ResponseEntity.ok(postFacade.getDigitalDocumentPosts(DigitalDocumentType.fromValue(digitalDocumentType), pageRequest));
    }
}
