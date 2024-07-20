package ag.act.facade.admin.campaign;

import ag.act.entity.campaign.CampaignDownload;
import ag.act.facade.digitaldocument.DigitalDocumentDownloadFacade;
import ag.act.service.admin.CampaignDownloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class CampaignDownloadFacadeTest {

    @InjectMocks
    private CampaignDownloadFacade facade;
    @Mock
    private DigitalDocumentDownloadFacade digitalDocumentDownloadFacade;
    @Mock
    private CampaignDownloadService campaignDownloadService;

    @Nested
    class CreateDigitalDocumentZipFile {

        private Long campaignId;
        private Boolean isSecured;
        private List<Long> digitalDocumentIds;
        @Mock
        private CampaignDownload campaignDownload;
        private Long downloadId;

        @BeforeEach
        void setUp() {
            campaignId = someLong();
            Long digitalDocumentId = someLong();
            digitalDocumentIds = List.of(digitalDocumentId);
            isSecured = someBoolean();
            downloadId = someLong();

            digitalDocumentIds.forEach(
                it -> willDoNothing().given(digitalDocumentDownloadFacade).cleanUpIfDigitalDocumentFinished(it)
            );

            given(campaignDownloadService.create(campaignId)).willReturn(campaignDownload);
            given(campaignDownload.getId()).willReturn(downloadId);

            willDoNothing().given(campaignDownloadService)
                .invokeZipFilesLambda(campaignId, digitalDocumentIds, downloadId, isSecured);

            given(campaignDownloadService.updateDigitalDocumentZipFileInProgress(downloadId))
                .willReturn(campaignDownload);

            facade.createDigitalDocumentZipFile(campaignId, digitalDocumentIds, isSecured);
        }

        @Test
        void shouldCallCleanupIfFinished() {
            digitalDocumentIds.forEach(
                it -> then(digitalDocumentDownloadFacade).should().cleanUpIfDigitalDocumentFinished(it)
            );
        }

        @Test
        void shouldCallCreate() {
            then(campaignDownloadService).should().create(campaignId);
        }

        @Test
        void shouldCallInvokeZipFilesLambda() {
            then(campaignDownloadService).should()
                .invokeZipFilesLambda(campaignId, digitalDocumentIds, downloadId, isSecured);
        }

        @Test
        void shouldCallUpdateDigitalDocumentZipFileInProgress() {
            then(campaignDownloadService).should()
                .updateDigitalDocumentZipFileInProgress(downloadId);
        }
    }
}
