package ag.act.entity;

import ag.act.model.Status;

public interface ActEntity {
    default ag.act.model.Status getStatus() {
        return Status.ACTIVE;
    }

    default Long getId() {
        return null;
    }

    default boolean isActive() {
        return getStatus() == Status.ACTIVE;
    }
}
