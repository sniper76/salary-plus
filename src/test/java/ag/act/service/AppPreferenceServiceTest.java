package ag.act.service;

import ag.act.entity.AppPreference;
import ag.act.enums.AppPreferenceType;
import ag.act.exception.BadRequestException;
import ag.act.repository.AppPreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings
class AppPreferenceServiceTest {

    @InjectMocks
    private AppPreferenceService service;
    @Mock
    private AppPreferenceRepository appPreferenceRepository;

    @Nested
    class WhenNotExistAppPreference {

        private Long appPreferenceId;

        @BeforeEach
        void setUp() {
            appPreferenceId = someLong();
            given(appPreferenceRepository.findById(appPreferenceId)).willReturn(Optional.empty());
        }

        @Test
        void shouldThrowException() {
            assertException(
                BadRequestException.class,
                () -> service.getAppPreferenceDetails(appPreferenceId),
                "App Preference 정보를 찾을 수 없습니다."
            );
        }
    }

    @Nested
    class WhenUpdate {

        @Mock
        private AppPreference appPreference;
        @Mock
        private ag.act.model.AppPreferenceUpdateRequest request;

        private Long appPreferenceId;
        private AppPreferenceType type;


        @BeforeEach
        void setUp() {
            type = someEnum(AppPreferenceType.class);
            appPreferenceId = someLong();
            given(appPreferenceRepository.findById(appPreferenceId)).willReturn(Optional.of(appPreference));
        }

        @Nested
        class WhenCurrentValueNotEqual {

            @BeforeEach
            void setUp() {
                final String currentValue = someString(5);
                given(appPreference.getValue()).willReturn(currentValue);
                given(appPreference.getType()).willReturn(type);

                final String differentValue = someString(10);
                given(request.getCurrentValue()).willReturn(differentValue);
                given(request.getNewValue()).willReturn(someString(5));
            }

            @Test
            void shouldReturnException() {
                assertException(
                    BadRequestException.class,
                    () -> service.updateAppPreference(appPreferenceId, request),
                    "App Preference 값을 업데이트 할 수 없습니다. 값을 다시 확인해주세요."
                );
            }
        }

        @Nested
        class WhenValueNotMatchesPattern {

            @BeforeEach
            void setUp() {
                final String currentValue = someString(5);
                given(appPreference.getValue()).willReturn(currentValue);
                given(request.getCurrentValue()).willReturn(currentValue);

                final String newValue = someString(5);
                given(appPreference.getType()).willReturn(type);
                given(request.getNewValue()).willReturn(newValue);
            }

            @Test
            void shouldReturnException() {
                assertException(
                    BadRequestException.class,
                    () -> service.updateAppPreference(appPreferenceId, request),
                    "App Preference 값을 업데이트 할 수 없습니다. 값을 다시 확인해주세요."
                );
            }
        }
    }
}