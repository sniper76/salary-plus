package ag.act.handler.admin;

import ag.act.converter.PageDataConverter;
import ag.act.dto.SimplePageDto;
import ag.act.dto.user.UserSearchFilterDto;
import ag.act.enums.admin.UserFilterType;
import ag.act.enums.admin.UserSearchType;
import ag.act.exception.BadRequestException;
import ag.act.facade.auth.AuthFacade;
import ag.act.facade.user.UserFacade;
import ag.act.model.GetUserResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.assertException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class AdminUserApiDelegateImplTest {

    @InjectMocks
    private AdminUserApiDelegateImpl apiDelegate;
    @Mock
    private UserFacade userFacade;
    @Mock
    private AuthFacade authFacade;
    @Mock
    private PageDataConverter pageDataConverter;

    @Nested
    class GetUserDetailsAdmin {

        private Long userId;
        @Mock
        private ag.act.model.UserDataResponse userDataResponse;

        @BeforeEach
        void setUp() {
            userId = someLong();
            given(userFacade.getUserDataResponse(userId)).willReturn(userDataResponse);
        }

        @Test
        void shouldReturnUserDataResponse() {
            // When
            final ResponseEntity<ag.act.model.UserDataResponse> actualResponse = apiDelegate.getUserDetailsAdmin(userId);

            // Then
            assertThat(actualResponse.getBody(), is(userDataResponse));
        }
    }

    @Nested
    class WithdrawMyDataWithToken {

        @Nested
        class WhenFinpongAccessTokenIsValid {

            private ResponseEntity<ag.act.model.SimpleStringResponse> actualResponse;
            private String finpongAccessToken;

            @BeforeEach
            void setUp() {
                finpongAccessToken = someString(10);
                willDoNothing().given(authFacade).withdrawMyDataService(finpongAccessToken);

                actualResponse = apiDelegate.withdrawMyDataWithToken(finpongAccessToken);
            }

            @SuppressWarnings("ConstantConditions")
            @Test
            void shouldReturnSimpleOkayResponse() {
                assertThat(actualResponse.getBody().getStatus(), is("ok"));
            }

            @Test
            void shouldCallAuthFacadeWithdrawMyDataService() {
                then(authFacade).should().withdrawMyDataService(finpongAccessToken);
            }
        }

        @Nested
        class WhenFinpongAccessTokenIsInvalid {

            @ParameterizedTest(name = "{index} => finpongAccessToken=''{0}''")
            @MethodSource("invalidFinpongAccessTokenProvider")
            void shouldThrowBadRequestException(String finpongAccessToken) {
                assertException(
                    BadRequestException.class,
                    () -> apiDelegate.withdrawMyDataWithToken(finpongAccessToken),
                    "마이데이터 토큰을 확인해주세요."
                );
            }

            private static Stream<Arguments> invalidFinpongAccessTokenProvider() {
                return Stream.of(
                    Arguments.of(""),
                    Arguments.of("  "),
                    Arguments.of("     "),
                    Arguments.of((String) null)
                );
            }
        }
    }


    @Nested
    class GetUserList {
        private ResponseEntity<GetUserResponse> actualResponse;
        private Integer page;
        private int size;
        private List<String> sorts;
        @Mock
        private GetUserResponse getUserResponse;
        @Mock
        private PageRequest pageRequest;
        @Mock
        private SimplePageDto<ag.act.model.UserResponse> response;

        @BeforeEach
        void setUp() {
            final String filterType = UserFilterType.ADMIN.name();
            final String searchType = UserSearchType.NICKNAME.name();
            final String searchKeyword = someString(5);
            page = someIntegerBetween(1, 1000);
            size = someIntegerBetween(10, 100);
            sorts = List.of(someString(10));

            pageRequest = PageRequest.of(1, 50);

            given(pageDataConverter.convert(page, size, sorts)).willReturn(pageRequest);
            given(userFacade.getUsers(any(UserSearchFilterDto.class), any(PageRequest.class))).willReturn(response);
            given(pageDataConverter.convert(response, GetUserResponse.class)).willReturn(getUserResponse);

            actualResponse = apiDelegate.getUsers(filterType, searchType, searchKeyword, page, size, sorts);
        }

        @Test
        void shouldReturnResponse() {
            assertThat(actualResponse.getBody(), Matchers.is(getUserResponse));
        }

        @Test
        void shouldConvertPageRequest() {
            then(pageDataConverter).should().convert(page, size, sorts);
        }

        @Test
        void shouldCallUserFacade() {
            then(userFacade).should().getUsers(any(UserSearchFilterDto.class), any(PageRequest.class));
        }
    }
}
