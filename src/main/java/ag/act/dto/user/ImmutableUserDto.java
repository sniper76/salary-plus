package ag.act.dto.user;

import ag.act.entity.ActUser;
import ag.act.entity.User;

public class ImmutableUserDto extends ActUser {
    private final ActUser user;

    public ImmutableUserDto(User user) {
        this.user = user;
    }

    @Override
    public Long getId() {
        return user.getId();
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public boolean isAdmin() {
        return user.isAdmin();
    }
}
