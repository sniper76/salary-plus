package ag.act.configuration.initial;

import ag.act.entity.LinkUrl;
import ag.act.enums.LinkType;
import ag.act.model.Status;
import ag.act.repository.LinkUrlRepository;
import ag.act.validator.UrlValidator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@MockitoSettings(strictness = Strictness.LENIENT)
public class LinkUrlsLoaderTest {
    @InjectMocks
    private LinkUrlsLoader linkUrlsLoader;

    @Mock
    private LinkUrlRepository linkUrlRepository;

    @Mock
    private UrlValidator urlValidator;

    private static final int LINK_TYPES_CNT = LinkType.values().length;

    @Nested
    class WhenLinkNotExist {
        @Test
        void shouldSaveLinks() {
            // Given
            willDoNothing().given(urlValidator).validate(any(String.class));
            given(linkUrlRepository.findByLinkTypeAndStatus(any(LinkType.class), any(Status.class)))
                .willReturn(Optional.empty());

            // When
            linkUrlsLoader.load();

            // Then
            then(linkUrlRepository).should(times(LINK_TYPES_CNT)).save(any(LinkUrl.class));
        }
    }

    @Nested
    class WhenLinksAlreadyExist {
        @Test
        void shouldSaveLinks() {
            // Given
            willDoNothing().given(urlValidator).validate(any(String.class));
            given(linkUrlRepository.findByLinkTypeAndStatus(any(LinkType.class), any(Status.class)))
                .willReturn(Optional.of(new LinkUrl()));

            // When
            linkUrlsLoader.load();

            // Then
            then(linkUrlRepository).should(never()).save(any(LinkUrl.class));
        }
    }
}
