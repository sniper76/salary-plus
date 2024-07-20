package ag.act.handler.user;

import ag.act.api.UserPushAgreementApiDelegate;
import ag.act.converter.push.UserPushAgreementItemConverter;
import ag.act.service.push.UserPushAgreementService;
import ag.act.util.SimpleStringResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPushAgreementApiDelegateImpl implements UserPushAgreementApiDelegate {
    private final UserPushAgreementService userPushAgreementService;
    private final UserPushAgreementItemConverter userPushAgreementItemConverter;

    @Override
    public ResponseEntity<ag.act.model.UserPushAgreementItemsDataResponse> getUserPushAgreements() {
        return ResponseEntity.ok(userPushAgreementService.getUserPushAgreements());
    }

    @Override
    public ResponseEntity<ag.act.model.SimpleStringResponse> updateUserPushAgreements(
        List<ag.act.model.UserPushAgreementItem> userPushAgreementItems
    ) {
        userPushAgreementService.updateUserPushAgreementStatus(
            userPushAgreementItemConverter.convert(userPushAgreementItems)
        );

        return SimpleStringResponseUtil.okResponse();
    }
}
