package ag.act.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ActUser {
    private boolean isAdmin;
    private boolean isAcceptor;

    public abstract Long getId();

    public abstract String getName();

    public boolean isGuest() {
        return false;
    }
}