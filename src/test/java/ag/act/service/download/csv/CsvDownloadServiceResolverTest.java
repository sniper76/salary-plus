package ag.act.service.download.csv;

import ag.act.entity.Campaign;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.service.digitaldocument.campaign.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static ag.act.TestUtil.shuffleAndGet;
import static ag.act.TestUtil.someBoardCategoryExcluding;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@SuppressWarnings("rawtypes")
@MockitoSettings(strictness = Strictness.LENIENT)
class CsvDownloadServiceResolverTest {

    private CsvDownloadServiceResolver resolver;
    @Mock
    private DigitalDocumentCsvDownloadProcessor digitalDocumentCsvDownloadProcessor;
    @Mock
    private CampaignService campaignService;
    @Mock
    private PollCsvDownloadProcessor pollCsvDownloadProcessor;
    private DigitalDocumentCsvDownloadService digitalDocumentCsvDownloadService;
    private CampaignDigitalDocumentCsvDownloadService campaignDigitalDocumentCsvDownloadService;
    private CampaignPollCsvDownloadService campaignPollCsvDownloadService;
    private final DefaultCsvDownloadService defaultCsvDownloadService = new DefaultCsvDownloadService();

    @BeforeEach
    void setUp() {
        digitalDocumentCsvDownloadService = new DigitalDocumentCsvDownloadService(digitalDocumentCsvDownloadProcessor);
        campaignDigitalDocumentCsvDownloadService = new CampaignDigitalDocumentCsvDownloadService(
            campaignService,
            digitalDocumentCsvDownloadProcessor
        );
        campaignPollCsvDownloadService = new CampaignPollCsvDownloadService(
            campaignService,
            pollCsvDownloadProcessor
        );

        resolver = new CsvDownloadServiceResolver(
            shuffleAndGet(
                defaultCsvDownloadService,
                digitalDocumentCsvDownloadService,
                campaignDigitalDocumentCsvDownloadService,
                campaignPollCsvDownloadService
            ),
            defaultCsvDownloadService
        );
    }

    @Nested
    class SupportCampaign {

        @Mock
        private Campaign campaign;

        @Test
        void shouldReturnCampaignCsvDownloadService() {
            // Given
            CsvDownloadSourceProvider<Campaign> sourceProvider = new CsvDownloadSourceProvider<>(BoardCategory.ETC, campaign);

            // When
            CsvDownloadService actual = resolver.resolve(sourceProvider);

            // Then
            assertThat(actual, is(campaignDigitalDocumentCsvDownloadService));
        }

        @Test
        void shouldReturnCampaignPollCsvDownloadService() {
            // Given
            CsvDownloadSourceProvider<Campaign> sourceProvider = new CsvDownloadSourceProvider<>(BoardCategory.SURVEYS, campaign);

            // When
            CsvDownloadService actual = resolver.resolve(sourceProvider);

            // Then
            assertThat(actual, is(campaignPollCsvDownloadService));
        }
    }

    @Nested
    class SupportDigitalDocument {

        @Test
        void shouldReturnDigitalDocumentCsvDownloadService() {
            // Given
            List<Long> ids = List.of(1L, 2L, 3L);
            CsvDownloadSourceProvider<List<Long>> sourceProvider = new CsvDownloadSourceProvider<>(
                someThing(
                    BoardCategory.ETC,
                    BoardCategory.DIGITAL_DELEGATION,
                    BoardCategory.CO_HOLDING_ARRANGEMENTS
                ),
                ids
            );

            // When
            CsvDownloadService actual = resolver.resolve(sourceProvider);

            // Then
            assertThat(actual, is(digitalDocumentCsvDownloadService));
        }
    }

    @Nested
    class FailToFindCsvDownloadService {

        @Mock
        private Campaign campaign;

        @Test
        void shouldReturnDefaultCsvDownloadService() {
            // Given
            CsvDownloadSourceProvider<Campaign> sourceProvider = new CsvDownloadSourceProvider<>(
                someBoardCategoryExcluding(
                    someThing(BoardGroup.values()),
                    BoardCategory.ETC,
                    BoardCategory.SURVEYS
                ),
                campaign
            );

            // When
            CsvDownloadService actual = resolver.resolve(sourceProvider);

            // Then
            assertThat(actual, is(defaultCsvDownloadService));
        }
    }
}
