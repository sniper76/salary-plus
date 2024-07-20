package ag.act.service.download.csv;

import ag.act.entity.Campaign;
import ag.act.enums.BoardCategory;
import ag.act.exception.BadRequestException;
import ag.act.service.digitaldocument.campaign.CampaignService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CampaignDigitalDocumentCsvDownloadService implements CsvDownloadService<Campaign> {
    private final CampaignService campaignService;
    private final DigitalDocumentCsvDownloadProcessor digitalDocumentCsvDownloadProcessor;

    @Override
    public boolean isSupport(CsvDownloadSourceProvider<Campaign> sourceProvider) {
        return sourceProvider.getBoardCategory() == BoardCategory.ETC
            && sourceProvider.getSourceType() == Campaign.class;
    }

    @Override
    public void download(HttpServletResponse response, CsvDownloadSourceProvider<Campaign> sourceProvider) {
        digitalDocumentCsvDownloadProcessor.download(response, getAllDigitalDocumentIds(sourceProvider.getSource()));
    }

    private List<Long> getAllDigitalDocumentIds(Campaign campaign) {
        final List<Long> digitalDocumentIds = campaignService.findAllDigitalDocumentIdsBySourcePostId(campaign.getSourcePostId());

        if (CollectionUtils.isEmpty(digitalDocumentIds)) {
            throw new BadRequestException("캠페인의 전자문서가 존재하지 않습니다.");
        }

        return digitalDocumentIds;
    }
}
