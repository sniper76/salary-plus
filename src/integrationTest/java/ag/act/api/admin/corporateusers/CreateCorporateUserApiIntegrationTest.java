package ag.act.api.admin.corporateusers;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.model.CorporateUserDataResponse;
import ag.act.model.CorporateUserDetailsResponse;
import ag.act.model.CorporateUserRequest;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.stream.Stream;

import static ag.act.TestUtil.someCorporateNo;
import static ag.act.TestUtil.someCorporateNoOverLength;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class CreateCorporateUserApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/corporate-users";
    private String adminJwt;
    private CorporateUserRequest request;

    @BeforeEach
    void setUp() {
        User adminUser = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(adminUser.getId());
    }

    private MvcResult callApi(ResultMatcher resultMatcher, CorporateUserRequest request) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(adminJwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private CorporateUserRequest genRequest(String no, String name) {
        return new CorporateUserRequest()
            .corporateNo(no)
            .corporateName(name)
            .status(Status.ACTIVE.name());
    }

    @Nested
    class WhenCreate {

        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of(someCorporateNo()),
                Arguments.of("1231212345"),
                Arguments.of("1234-5678-9000"),
                Arguments.of("1234-5678-90-00"),
                Arguments.of("123456789000"),
                Arguments.of("12345678-9000"),
                Arguments.of("1-2234234234234-3-3-3-3-3-2342342343-3-3-3-3"),
                Arguments.of("111111-111111112121212-1212124140140124012-4-124-12-4-124-1-241")
            );
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @ParameterizedTest(name = "{index} => corporateNo=''{0}''")
        @MethodSource("valueProvider")
        void shouldReturn(String corporateNo) throws Exception {
            request = genRequest(corporateNo, someString(10));
            final MvcResult response = callApi(status().isOk(), request);
            final CorporateUserDataResponse result = itUtil.getResult(response, CorporateUserDataResponse.class);

            assertResponse(result.getData());
        }

        private void assertResponse(CorporateUserDetailsResponse result) {
            final Long userId = result.getUserId();

            final User corporateUser = itUtil.findUser(userId);
            assertThat(corporateUser.getEmail(), is(containsString(request.getCorporateNo())));
            assertThat(corporateUser.getName(), is(request.getCorporateName().trim()));
            assertThat(corporateUser.getNickname(), is(request.getCorporateName().trim()));
            assertThat(result.getCorporateNo(), is(request.getCorporateNo()));
            assertThat(result.getCorporateName(), is(request.getCorporateName()));

            assertThat(itUtil.findAllNicknameHistoriesByUserId(userId).isEmpty(), is(false));
        }
    }

    @Nested
    class WhenAlreadyExistCorporateNo {

        @BeforeEach
        void setUp() {
            final String corporateNo = "123123-1234568";
            itUtil.createCorporateUser(corporateNo, someString(10));
            request = genRequest(corporateNo, someString(10));
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturn() throws Exception {
            final MvcResult response = callApi(status().isBadRequest(), request);

            itUtil.assertErrorResponse(response, 400, "이미 존재하는 법인사업자입니다.");
        }
    }

    @Nested
    class WhenCorporateNoPatternError {

        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of("ABCDEFGHIJ"),
                Arguments.of("1231A-12C45"),
                Arguments.of("-1231-1245"),
                Arguments.of("1231-1245-"),
                Arguments.of("-1231-1245-"),
                Arguments.of("1231A--12C45"),
                Arguments.of("여기부터-에러-케이스"),
                Arguments.of("11111-"),
                Arguments.of("-12312-1231231-23-1231"),
                Arguments.of("12312-1231231-23-1231a"),
                Arguments.of("1111----1111111-11121313131313"),
                Arguments.of("sdfjsldf-sdfljsdfls"),
                Arguments.of("123123-123123123-"),
                Arguments.of("-392340234-2342342-2342"),
                Arguments.of("-sfjsdfisdi"),
                Arguments.of("sijfsf-"),
                Arguments.of("1231213132dd-dd0d02030"),
                Arguments.of(someCorporateNoOverLength())
            );
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @ParameterizedTest(name = "{index} => corporateNo=''{0}''")
        @MethodSource("valueProvider")
        void shouldReturn(String corporateNo) throws Exception {
            request = genRequest(corporateNo, someString(10));

            final MvcResult response = callApi(status().isBadRequest(), request);

            itUtil.assertErrorResponse(response, 400, "법인사업자번호 형식이 맞지 않습니다.");
        }
    }
}
