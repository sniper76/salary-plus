package ag.act.service.post;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class MostRecentThreePostPageableFactory {

    private static final int RECENT_THREE = 3;

    public Pageable getPageable() {
        return PageRequest.of(0, RECENT_THREE, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
