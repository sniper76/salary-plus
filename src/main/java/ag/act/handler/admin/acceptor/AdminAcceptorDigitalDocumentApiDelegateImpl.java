package ag.act.handler.admin.acceptor;

import ag.act.api.AdminAcceptorDigitalDocumentApiDelegate;
import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.PageDataConverter;
import ag.act.core.guard.IsActiveUserGuard;
import ag.act.core.guard.UseGuards;
import ag.act.dto.GetPostDigitalDocumentSearchDto;
import ag.act.enums.DigitalDocumentType;
import ag.act.facade.admin.AdminAcceptorFacade;
import ag.act.model.GetPostDigitalDocumentDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@UseGuards({IsActiveUserGuard.class})
public class AdminAcceptorDigitalDocumentApiDelegateImpl implements AdminAcceptorDigitalDocumentApiDelegate {
    private final AdminAcceptorFacade adminAcceptorFacade;
    private final PageDataConverter pageDataConverter;

    @Override
    public ResponseEntity<GetPostDigitalDocumentDataResponse> getDigitalDocumentPostsByAcceptor(
        String digitalDocumentType,
        String searchType,
        String searchKeyword,
        Integer page,
        Integer size,
        List<String> sorts
    ) {
        final GetPostDigitalDocumentSearchDto getSearchDto = GetPostDigitalDocumentSearchDto.builder()
            .searchKeyword(searchKeyword)
            .searchType(searchType)
            .acceptorId(ActUserProvider.getNoneNull().getId())
            .digitalDocumentType(DigitalDocumentType.fromValue(digitalDocumentType))
            .pageRequest(pageDataConverter.convert(page, size, sorts))
            .build();


        return ResponseEntity.ok(adminAcceptorFacade.getDigitalDocumentPosts(getSearchDto));
    }
}
