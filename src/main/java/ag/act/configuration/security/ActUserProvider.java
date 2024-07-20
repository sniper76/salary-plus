package ag.act.configuration.security;

import ag.act.dto.user.ImmutableUserDto;
import ag.act.entity.ActGuest;
import ag.act.entity.ActUser;
import ag.act.entity.User;
import ag.act.exception.UnauthorizedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ActUserProvider {

    private static final ActUser GUEST = new ActGuest();

    private static User systemUser;

    public static Optional<User> get() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public static User getNoneNull() {
        return get()
            .orElseThrow(() -> new UnauthorizedException("회원을 찾을 수 없습니다."));
    }

    public static ActUser getActUser() {
        return get()
            .map(ActUser.class::cast)
            .orElse(GUEST);
    }

    public static void initSystemUser(User systemUser) {
        if (ActUserProvider.systemUser == null) {
            ActUserProvider.systemUser = systemUser;
        }
    }

    public static void cleanupSystemUser() {
        ActUserProvider.systemUser = null;
    }

    public static Long getSystemUserId() {
        return ActUserProvider.systemUser.getId();
    }

    public static ActUser getSystemUser() {
        return new ImmutableUserDto(ActUserProvider.systemUser);
    }

    public static Long getSuperAdminUserId() {
        return getSystemUserId();
    }
}
