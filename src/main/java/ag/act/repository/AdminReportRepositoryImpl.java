package ag.act.repository;

import ag.act.dto.ReportItemDto;
import ag.act.entity.QBoard;
import ag.act.entity.QComment;
import ag.act.entity.QPost;
import ag.act.entity.QReport;
import ag.act.entity.QStock;
import ag.act.enums.ReportType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("MemberName")
public class AdminReportRepositoryImpl extends QuerydslRepositorySupport implements AdminReportRepository {

    private final JPAQueryFactory query;
    private final QPost qPost = QPost.post;
    private final QBoard qBoard = QBoard.board;
    private final QStock qStock = QStock.stock;
    private final QComment qComment = QComment.comment;
    private final QReport qReport = QReport.report;

    public AdminReportRepositoryImpl(JPAQueryFactory query) {
        super(ReportItemDto.class);
        this.query = query;
    }

    @Override
    public long countByReport(ReportType reportType, ag.act.model.ReportStatus reportStatus) {

        JPAQuery<Long> jpaQuery = query
            .select(qReport.count())
            .from(qReport)
            .where(qReport.type.eq(reportType));

        if (reportStatus != null) {
            jpaQuery.where(qReport.reportStatus.eq(reportStatus));
        }

        return jpaQuery.fetchFirst();
    }

    @Override
    public List<ReportItemDto> findByReportPosts(ReportType reportType, ag.act.model.ReportStatus reportStatus, int size, int page) {

        JPAQuery<ReportItemDto> jpaQuery = query.select(
                Projections.fields(
                    ReportItemDto.class,
                    qReport.id,
                    qReport.contentId,
                    qPost.title,
                    qReport.type.as("reportType"),
                    qReport.reportStatus,
                    qReport.reason,
                    qReport.createdAt,
                    qReport.updatedAt,
                    qBoard.stockCode,
                    qStock.name.as("stockName"),
                    qBoard.category.stringValue().as("boardCategoryName"),
                    qBoard.group.stringValue().as("boardGroupName"),
                    qPost.likeCount,
                    qPost.commentCount,
                    qPost.viewCount
                )
            ).from(qReport)
            .leftJoin(qPost).on(qReport.contentId.eq(qPost.id))
            .leftJoin(qBoard).on(qPost.boardId.eq(qBoard.id))
            .leftJoin(qStock).on(qBoard.stockCode.eq(qStock.code))
            .where(qReport.type.eq(reportType));

        addListCondition(reportStatus, jpaQuery);
        jpaQuery.orderBy(qReport.createdAt.desc()).offset((long) page * size).limit(size).fetch();

        return jpaQuery.stream().toList();
    }

    @Override
    public List<ReportItemDto> findByReportComments(ReportType reportType, ag.act.model.ReportStatus reportStatus, int size, int page) {

        JPAQuery<ReportItemDto> jpaQuery = query.select(
                Projections.fields(
                    ReportItemDto.class,
                    qReport.id,
                    qReport.contentId,
                    qPost.title,
                    qReport.type.as("reportType"),
                    qReport.reportStatus,
                    qReport.reason,
                    qReport.createdAt,
                    qReport.updatedAt,
                    qBoard.stockCode,
                    qStock.name.as("stockName"),
                    qBoard.category.stringValue().as("boardCategoryName"),
                    qBoard.group.stringValue().as("boardGroupName"),
                    qPost.likeCount,
                    qComment.replyCommentCount.as("replyCount")
                )
            ).from(qReport)
            .leftJoin(qComment).on(qReport.contentId.eq(qComment.id))
            .leftJoin(qPost).on(qComment.postId.eq(qPost.id))
            .leftJoin(qBoard).on(qPost.boardId.eq(qBoard.id))
            .leftJoin(qStock).on(qBoard.stockCode.eq(qStock.code))
            .where(qReport.type.eq(reportType));

        addListCondition(reportStatus, jpaQuery);
        jpaQuery.orderBy(qReport.createdAt.desc()).offset((long) page * size).limit(size).fetch();

        return jpaQuery.stream().toList();
    }

    private void addListCondition(ag.act.model.ReportStatus reportStatus, JPAQuery<ReportItemDto> jpaQuery) {
        if (reportStatus != null) {
            jpaQuery.where(qReport.reportStatus.eq(reportStatus));
        }
    }
}
