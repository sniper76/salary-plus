package ag.act.dto;

import ag.act.enums.ReportType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ReportItemDto {
    private Long id;
    private Long contentId;
    private String title;
    private ReportType reportType;
    private ag.act.model.ReportStatus reportStatus;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String stockCode;
    private String stockName;
    private String boardCategoryName;
    private String boardGroupName;
    private Long likeCount;
    private Long commentCount;
    private Long replyCount;
    private Long viewCount;
}
