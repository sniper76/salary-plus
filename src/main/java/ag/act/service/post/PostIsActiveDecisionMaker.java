package ag.act.service.post;

import ag.act.entity.ActUser;
import ag.act.model.Status;
import org.springframework.stereotype.Component;

@Component
public class PostIsActiveDecisionMaker {

    public Status getIsActiveStatus(ActUser user, Boolean isActive) {
        if (user.isAdmin()) {
            return isActive ? Status.ACTIVE : Status.INACTIVE_BY_ADMIN;
        }
        return Status.ACTIVE;
    }
}
