package ag.act.service;

import ag.act.dto.popup.PopupSearchDto;
import ag.act.entity.Popup;
import ag.act.enums.popup.PopupSearchType;
import ag.act.enums.popup.PopupStatus;
import ag.act.model.Status;
import ag.act.repository.PopupRepository;
import ag.act.specification.PopupSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class PopupServiceTest {

    @InjectMocks
    private PopupService service;

    @Mock
    private PopupRepository popupRepository;

    @Nested
    class GetPopupList {

        @Mock
        private PopupSearchDto popupSearchDto;
        @Mock
        private Pageable pageRequest;
        @Mock
        private Page<Popup> popupPage;
        private Page<Popup> actualPopupPage;

        @Nested
        class WhenSearchKeywordIsBlank {

            @BeforeEach
            void setUp() {
                PopupStatus popupStatus = someEnum(PopupStatus.class);
                Specification<Popup> popupSpecification = switch (popupStatus) {
                    case READY -> PopupSpecification.isFuture();
                    case PROCESSING -> PopupSpecification.isInProgress();
                    case COMPLETE -> PopupSpecification.isPast();
                    case ALL -> PopupSpecification.empty();
                };

                given(popupSearchDto.isKeywordBlank()).willReturn(true);
                given(popupSearchDto.getTargetDatetimeSpecification()).willReturn(popupSpecification);
                given(popupRepository.findAll(
                    ArgumentMatchers.<Specification<Popup>>any(),
                    any(Pageable.class)
                )).willReturn(popupPage);

                actualPopupPage = service.getPopupList(popupSearchDto, pageRequest);
            }

            @Test
            void shouldReturnPopupPage() {
                assertThat(actualPopupPage, is(popupPage));
            }

            @Test
            void shouldCallGetTargetDatetimeSpecification() {
                then(popupSearchDto).should().getTargetDatetimeSpecification();
            }

            @Test
            void shouldCallPopupRepository() {
                then(popupRepository).should().findAll(
                    ArgumentMatchers.<Specification<Popup>>any(),
                    any(Pageable.class)
                );
            }
        }

        @Nested
        class WhenSearchTypeIsTitle {
            private String searchKeyword;

            @Mock
            private Specification<Popup> titleContainsPopupSpecification;
            @Mock
            private Specification<Popup> datetimeTargetPopupSpecification;
            @Mock
            private Specification<Popup> combinedSpecification;


            @BeforeEach
            void setUp() {
                searchKeyword = someString(5);

                given(popupSearchDto.getPopupSearchType()).willReturn(PopupSearchType.TITLE);
                given(popupSearchDto.isKeywordBlank()).willReturn(false);
                given(popupSearchDto.getKeyword()).willReturn(searchKeyword);
                given(popupSearchDto.getTitleContainsSpecification()).willReturn(titleContainsPopupSpecification);
                given(popupSearchDto.getTargetDatetimeSpecification()).willReturn(datetimeTargetPopupSpecification);
                given(titleContainsPopupSpecification.and(datetimeTargetPopupSpecification)).willReturn(combinedSpecification);
                given(popupRepository.findAll(ArgumentMatchers.<Specification<Popup>>any(), any(Pageable.class))).willReturn(popupPage);

                actualPopupPage = service.getPopupList(popupSearchDto, pageRequest);
            }

            @Test
            void shouldReturnPopupPage() {
                assertThat(actualPopupPage, is(popupPage));
            }

            @Test
            void shouldCallGetTitleContainsSpecification() {
                then(popupSearchDto).should().getTitleContainsSpecification();
            }

            @Test
            void shouldCallGetTargetDatetimeSpecification() {
                then(popupSearchDto).should().getTargetDatetimeSpecification();
            }

            @Test
            void shouldCallPopupRepository() {
                then(popupRepository).should().findAll(ArgumentMatchers.<Specification<Popup>>any(), any(Pageable.class));
                given(popupRepository.findAll(eq(titleContainsPopupSpecification), any(Pageable.class))).willReturn(popupPage);
            }
        }
    }

    @Nested
    class DeletePopup {
        @Mock
        private Popup popup;
        private Long popupId;

        @BeforeEach
        void setUp() {
            popupId = someLong();
            given(popup.getId()).willReturn(popupId);
            given(popupRepository.findByIdAndStatus(popupId, Status.ACTIVE)).willReturn(Optional.of(popup));
        }

        @Nested
        class WhenReserved {
            @Test
            void shouldDelete() {
                // Given
                given(popup.getTargetStartDatetime()).willReturn(LocalDateTime.now().plusYears(1));

                // When
                service.deletePopup(popupId);

                // Then
                then(popupRepository).should().deleteById(popupId);

            }

        }

        @Nested
        class WhenAlreadyShowing {
            @Test
            void shouldChangeStatus() {
                // Given
                given(popup.getTargetStartDatetime()).willReturn(LocalDateTime.now().minusYears(1));

                // When
                service.deletePopup(popupId);

                // Then
                then(popup).should().setStatus(Status.DELETED_BY_ADMIN);
            }
        }
    }
}
