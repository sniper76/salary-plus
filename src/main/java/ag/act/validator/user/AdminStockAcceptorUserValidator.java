package ag.act.validator.user;

import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.service.admin.stock.acceptor.StockAcceptorUserService;
import ag.act.service.digitaldocument.DigitalDocumentService;
import ag.act.service.user.UserRoleService;
import ag.act.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminStockAcceptorUserValidator {
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final StockAcceptorUserService stockAcceptorUserService;
    private final DigitalDocumentService digitalDocumentService;

    public User validate(String code, Long userId) {
        if (stockAcceptorUserService.findByStockCodeAndUserId(code, userId).isPresent()) {
            throw new BadRequestException("해당 종목에 이미 수임인으로 선정된 사용자입니다.");
        }

        return userService.getUser(userId);
    }

    public User validateForDelete(String code, Long userId) {
        if (!userRoleService.isAcceptorUser(userId)) {
            throw new BadRequestException("해당 종목에 수임인으로 선정되지 않은 사용자입니다.");
        }

        if (digitalDocumentService.existsProcessingByStockCodeAndAcceptor(code, userId)) {
            throw new BadRequestException("해당 종목에 수임인으로 진행중인 전자문서가 존재합니다.");
        }

        return userService.getUser(userId);
    }
}
