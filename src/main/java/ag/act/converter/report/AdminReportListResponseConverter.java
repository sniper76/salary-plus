package ag.act.converter.report;

import ag.act.converter.PageDataConverter;
import ag.act.dto.ReportItemDto;
import ag.act.dto.SimplePageDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class AdminReportListResponseConverter {
    private final ReportConverter reportConverter;
    private final PageDataConverter pageDataConverter;

    public AdminReportListResponseConverter(
        ReportConverter reportConverter, PageDataConverter pageDataConverter
    ) {
        this.reportConverter = reportConverter;
        this.pageDataConverter = pageDataConverter;
    }

    public ag.act.model.GetReportResponse convert(Page<ReportItemDto> reportPage) {
        SimplePageDto<ag.act.model.ReportListResponse> simplePage = new SimplePageDto<>(reportPage.map(reportConverter::convertResponse));
        return pageDataConverter.convert(simplePage, ag.act.model.GetReportResponse.class);
    }
}

