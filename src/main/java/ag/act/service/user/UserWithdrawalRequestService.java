package ag.act.service.user;

import ag.act.entity.User;
import ag.act.facade.auth.AuthFacade;
import ag.act.module.mydata.MyDataSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserWithdrawalRequestService {
    private final UserService userService;
    private final AuthFacade authFacade;
    private final MyDataSummaryService myDataSummaryService;
    private final UserWithdrawalRequestValidator userWithdrawalRequestValidator;

    public void requestWithdrawal(Long userId) {
        final User user = getUser(userId);

        userWithdrawalRequestValidator.validate(user);

        userService.withdrawRequest(user);

        if (hasMyDataSummary(user.getId())) {
            authFacade.withdrawMyDataService(user);
        }
    }

    private boolean hasMyDataSummary(Long userId) {
        return myDataSummaryService.findByUserId(userId).isPresent();
    }

    private User getUser(Long userId) {
        return userService.getUser(userId);
    }
}
