package ag.act.module.digitaldocumentgenerator.validator;

import ag.act.entity.User;
import ag.act.exception.InternalServerException;
import ag.act.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class DigitalDocumentAcceptorFillValidator {

    private final UserService userService;

    public DigitalDocumentAcceptorFillValidator(UserService userService) {
        this.userService = userService;
    }

    public void validate(Long acceptUserId) {
        final User acceptor = userService.getUser(acceptUserId);
        validateAcceptor(acceptor);
    }

    private void validateAcceptor(User acceptor) {
        if (StringUtils.isBlank(acceptor.getName())) {
            throw new InternalServerException("수임인의 이름이 존재하지 않습니다.");
        }
        if (acceptor.getBirthDate() == null) {
            throw new InternalServerException("수임인의 생년월일이 존재하지 않습니다.");
        }
        if (acceptor.getGender() == null) {
            throw new InternalServerException("수임인의 성별값이 존재하지 않습니다.");
        }
    }
}
