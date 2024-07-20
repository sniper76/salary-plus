package ag.act.service.download.csv;

import ag.act.entity.Campaign;
import ag.act.entity.Post;
import ag.act.enums.BoardCategory;
import ag.act.service.digitaldocument.campaign.CampaignService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CampaignPollCsvDownloadService implements CsvDownloadService<Campaign> {
    private final CampaignService campaignService;
    private final PollCsvDownloadProcessor pollCsvDownloadProcessor;

    @Override
    public boolean isSupport(CsvDownloadSourceProvider<Campaign> sourceProvider) {
        return sourceProvider.getBoardCategory() == BoardCategory.SURVEYS
            && sourceProvider.getSourceType() == Campaign.class;
    }

    @Override
    public void download(HttpServletResponse response, CsvDownloadSourceProvider<Campaign> sourceProvider) {
        pollCsvDownloadProcessor.download(response, getCampaignPosts(sourceProvider.getSource()));
    }

    private List<Post> getCampaignPosts(Campaign campaign) {
        return campaignService.getCampaignPosts(campaign);
    }
}
