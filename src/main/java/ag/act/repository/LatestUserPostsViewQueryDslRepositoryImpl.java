package ag.act.repository;

import ag.act.entity.LatestUserPostsView;
import ag.act.entity.QLatestPostTimestamp;
import ag.act.entity.QLatestUserPostsView;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LatestUserPostsViewQueryDslRepositoryImpl extends QuerydslRepositorySupport implements LatestUserPostsViewQueryDslRepository {
    public LatestUserPostsViewQueryDslRepositoryImpl() {
        super(LatestUserPostsView.class);
    }

    @Override
    public Boolean existsUnreadPost(List<String> stockCodes, Long userId, BoardGroup boardGroup) {
        return existsUnreadPost(stockCodes, userId, boardGroup, null);
    }

    @Override
    @SuppressWarnings("localVariableName")
    public Boolean existsUnreadPost(
        List<String> stockCodes,
        Long userId,
        BoardGroup boardGroup,
        BoardCategory boardCategory
    ) {
        QLatestPostTimestamp qPostTimestamp = QLatestPostTimestamp.latestPostTimestamp;
        QLatestUserPostsView qUserPostsView = QLatestUserPostsView.latestUserPostsView;

        // Condition builders for frequently used criteria
        BooleanExpression isPostTimestampBoardGroupEqual =
            boardGroup != null
                ? qPostTimestamp.boardGroup.eq(boardGroup)
                : null;
        BooleanExpression isPostTimestampBoardCategoryEqual =
            boardCategory != null
                ? qPostTimestamp.boardCategory.eq(boardCategory)
                : null;
        BooleanExpression isStockCodeInList = qPostTimestamp.stock.code.in(stockCodes);

        BooleanExpression isUserPostsViewBoardGroupEqual =
            boardGroup != null
                ? qUserPostsView.boardGroup.eq(boardGroup)
                : qUserPostsView.boardGroup.isNull();
        BooleanExpression isUserPostsViewBoardCategoryEqual =
            boardCategory != null
                ? qUserPostsView.boardCategory.eq(boardCategory)
                : qUserPostsView.boardCategory.isNull();

        // Condition when LatestUserPostsView is not associated (no matching LatestPostTimestamp)
        BooleanExpression whenUserPostsViewIsNull =
            qUserPostsView.isNull()
                .and(isStockCodeInList)
                .and(isPostTimestampBoardGroupEqual)
                .and(isPostTimestampBoardCategoryEqual);

        // Condition when LatestUserPostsView is associated with LatestPostTimestamp
        // Checks if LatestPostTimestamp's timestamp is more recent than LatestUserPostsView's timestamp
        BooleanExpression whenUserPostsViewIsNotNull =
            qUserPostsView.isNotNull()
                .and(isStockCodeInList)
                .and(isPostTimestampBoardGroupEqual)
                .and(isPostTimestampBoardCategoryEqual)
                .and(qPostTimestamp.timestamp.after(qUserPostsView.timestamp));

        JPQLQuery<Boolean> query =
            from(qPostTimestamp)
                .leftJoin(qUserPostsView)
                .on(
                    qPostTimestamp.stock.code.eq(qUserPostsView.stock.code)
                        .and(qUserPostsView.user.id.eq(userId))
                        .and(isUserPostsViewBoardGroupEqual)
                        .and(isUserPostsViewBoardCategoryEqual)
                )
                .where(whenUserPostsViewIsNull.or(whenUserPostsViewIsNotNull))
                .select(
                    qUserPostsView.isNull()
                        .or(qPostTimestamp.timestamp.after(qUserPostsView.timestamp))
                );

        Boolean result = query.fetchFirst();

        if (result == null) {
            return false;
        }

        return result;
    }

}
