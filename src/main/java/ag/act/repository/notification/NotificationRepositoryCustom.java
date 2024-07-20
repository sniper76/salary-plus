package ag.act.repository.notification;

import ag.act.dto.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface NotificationRepositoryCustom {
    Page<NotificationDto> findAllBySearchConditions(String categoryName, String postTitle, LocalDateTime createdAt, Pageable pageable);
}
