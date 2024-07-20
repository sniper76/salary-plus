package ag.act.service.cms;

import ag.act.entity.User;
import ag.act.exception.BadRequestException;
import ag.act.exception.ForbiddenException;
import ag.act.model.CmsLoginRequest;
import ag.act.service.user.UserRoleService;
import ag.act.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CmsLoginService {
    private static final String LOGIN_ERROR_MESSAGE = "이메일 혹은 비밀번호를 확인해주세요.";
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    public User login(CmsLoginRequest cmsLoginRequest) {
        final User user = getUser(cmsLoginRequest);
        validatePassword(cmsLoginRequest, user);
        return postLoginSetup(user);
    }

    private User getUser(CmsLoginRequest cmsLoginRequest) {
        return userService.getUserByEmail(cmsLoginRequest.getEmail())
            .orElseThrow(() -> new BadRequestException(LOGIN_ERROR_MESSAGE));
    }

    private void validatePassword(CmsLoginRequest cmsLoginRequest, User user) {
        if (!passwordEncoder.matches(cmsLoginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException(LOGIN_ERROR_MESSAGE);
        }
    }

    private User postLoginSetup(User user) {
        final boolean isAdmin = userRoleService.isAdmin(user.getId());
        final boolean isAcceptor = userRoleService.isAcceptorUser(user.getId());

        if (!isAcceptor && !isAdmin) {
            throw new ForbiddenException("인가되지 않은 접근입니다.");
        }

        user.setAcceptor(isAcceptor);
        user.setAdmin(isAdmin);
        return user;
    }
}
