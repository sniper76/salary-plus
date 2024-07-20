package ag.act.entity;

public class ActGuest extends ActUser {

    @Override
    public boolean isGuest() {
        return true;
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
