package ag.act.facade;

import ag.act.converter.popup.PopupResponseConverter;
import ag.act.dto.SimplePageDto;
import ag.act.dto.popup.PopupSearchDto;
import ag.act.entity.Popup;
import ag.act.exception.NotFoundException;
import ag.act.facade.popup.PopupDetailsResponseEnricher;
import ag.act.facade.popup.PopupFacade;
import ag.act.model.PopupDetailsResponse;
import ag.act.service.PopupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.somePage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class PopupFacadeTest {

    @InjectMocks
    private PopupFacade facade;
    @Mock
    private PopupService popupService;
    @Mock
    private PopupResponseConverter popupResponseConverter;
    @Mock
    private PopupDetailsResponseEnricher popupDetailsResponseEnricher;

    @Nested
    class GetPopupListItems {
        @Mock
        private PopupSearchDto popupSearchDto;
        @Mock
        private Pageable pageable;
        @Mock
        private Page<Popup> popupPage;
        @SuppressWarnings("rawtypes")
        private Page mappedPopupPage;
        private SimplePageDto<PopupDetailsResponse> actualResponse;

        @SuppressWarnings("unchecked")
        @BeforeEach
        void setUp() {
            mappedPopupPage = somePage(List.<Popup>of());

            given(popupService.getPopupList(popupSearchDto, pageable)).willReturn(popupPage);
            given(popupPage.getContent()).willReturn(List.of());
            given(popupPage.map(any())).willReturn(mappedPopupPage);
            given(popupResponseConverter.convert(any(Popup.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

            actualResponse = facade.getPopupListItems(popupSearchDto, pageable);
        }

        @Test
        void shouldReturnPopupListItemResponse() {
            assertThat(actualResponse.getContent(), is(mappedPopupPage.getContent()));
        }

        @Test
        void shouldCallPopupServiceToGetPopupList() {
            then(popupService).should().getPopupList(popupSearchDto, pageable);
        }
    }

    @Nested
    class GetPopupDetails {
        @Mock
        private Popup popup;
        private Long popupId;
        @Mock
        private PopupDetailsResponse popupDetailsResponse;

        @BeforeEach
        void setUp() {
            popupId = someLong();
        }

        @Nested
        class WhenFound {

            private PopupDetailsResponse actualResponse;

            @BeforeEach
            void setUp() {
                given(popupService.getPopup(popupId)).willReturn(Optional.of(popup));
                given(popupResponseConverter.convert(popup)).willReturn(popupDetailsResponse);
                given(popupDetailsResponseEnricher.enrichStockInfo(popup, popupDetailsResponse))
                    .willReturn(popupDetailsResponse);

                actualResponse = facade.getPopupDetails(popupId);
            }

            @Test
            void shouldReturnPopupDetailsResponse() {
                assertThat(actualResponse, is(popupDetailsResponse));
            }

            @Test
            void shouldCallPopupServiceToGetPopup() {
                then(popupService).should().getPopup(popupId);
            }

            @Test
            void shouldCallPopupResponseConverterToConvertPopup() {
                then(popupResponseConverter).should().convert(popup);
            }

            @Test
            void shouldCallPopupDetailsResponseEnricherToEnrichStockInfo() {
                then(popupDetailsResponseEnricher).should().enrichStockInfo(popup, popupDetailsResponse);
            }
        }

        @Nested
        class WhenNotFound {
            @Test
            void shouldThrowNotFoundException() {
                // Given
                given(popupService.getPopup(popupId)).willReturn(Optional.empty());

                // When // Then
                assertException(
                    NotFoundException.class,
                    () -> facade.getPopupDetails(popupId),
                    "해당 팝업을 찾을 수 없습니다."
                );
            }
        }
    }
}
