package ag.act.module.dart;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class DartCorporationPageableFactory {

    private static final int PAGE_SIZE = 500;

    public Pageable getPageable() {
        return PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "updatedAt"));
    }
}
