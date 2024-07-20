package ag.act.repository.notification;

import ag.act.dto.NotificationDto;
import ag.act.entity.QPost;
import ag.act.entity.notification.QNotification;
import ag.act.enums.notification.NotificationCategory;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("MemberName")
public class NotificationRepositoryImpl extends QuerydslRepositorySupport implements NotificationRepositoryCustom {
    private final JPAQueryFactory query;
    private final QNotification qNotification = QNotification.notification;
    private final QPost qPost = QPost.post;

    public NotificationRepositoryImpl(JPAQueryFactory query) {
        super(NotificationDto.class);
        this.query = query;
    }

    @Override
    public Page<NotificationDto> findAllBySearchConditions(String categoryName, String postTitle, LocalDateTime createdAt, Pageable pageable) {
        JPAQuery<NotificationDto> jpaQuery = query.select(
                Projections.fields(
                    NotificationDto.class,
                    qNotification.id,
                    qNotification.postId,
                    qNotification.category,
                    qNotification.type,
                    qNotification.createdAt,
                    qPost.title.as("postTitle"),
                    qPost.board.category.as("boardCategory"),
                    qPost.board.group.as("boardGroup"),
                    qPost.board.stock.code.as("stockCode"),
                    qPost.board.stock.name.as("stockName")
                ))
            .from(qNotification)
            .join(qPost).on(qNotification.postId.eq(qPost.id))
            .where(qNotification.createdAt.goe(createdAt)
                .and(qNotification.category.isNull()
                    .or(qNotification.category.eq(NotificationCategory.valueOf(categoryName))))
                .and(qPost.title.isNull()
                    .or(qPost.title.contains(postTitle)))
            )
            .orderBy(qNotification.createdAt.desc());

        List<NotificationDto> contents = jpaQuery.offset((long) pageable.getPageNumber() * pageable.getPageSize())
            .limit(pageable.getPageSize())
            .stream()
            .toList();

        return new PageImpl<>(contents, pageable, jpaQuery.stream().count());
    }
}
