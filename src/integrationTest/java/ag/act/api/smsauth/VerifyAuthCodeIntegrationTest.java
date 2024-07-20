package ag.act.api.smsauth;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.TestUtil;
import ag.act.entity.User;
import ag.act.entity.UserVerificationHistory;
import ag.act.enums.verification.VerificationType;
import ag.act.model.AuthType;
import ag.act.model.Status;
import kcb.module.v3.exception.OkCertException;
import org.apache.commons.collections4.map.HashedMap;
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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

import static ag.act.TestUtil.someBirthDayString;
import static ag.act.TestUtil.somePhoneNumber;
import static ag.act.enums.verification.VerificationOperationType.REGISTER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class VerifyAuthCodeIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/sms/verify-auth-code";

    private ag.act.model.VerifyAuthCodeRequest verifyAuthCodeRequest;
    private String txSeqNo;
    private String telComResCd;
    private String rsltMsg;
    private String rsltCd;
    private String name;
    private String birthDate;
    private ag.act.model.Gender gender;
    private String code;
    private String telComCd;
    private String di;
    private String ci;
    private String phoneNumber;
    private Integer firstNumberOfIdentification;

    @BeforeEach
    void setUp() throws OkCertException {
        itUtil.init();

        rsltCd = "B000";
        phoneNumber = somePhoneNumber();
        txSeqNo = someString(20);
        telComResCd = someString(40);
        rsltMsg = someString(40);
        telComCd = someString(17);
        ci = someString(5);
        di = someString(9);
        name = someAlphanumericString(10);
        birthDate = someBirthDayString();
        gender = someEnum(ag.act.model.Gender.class);
        firstNumberOfIdentification = TestUtil.getFirstNumberOfIdentification(birthDate, gender);
        code = someNumericString(6);

        verifyAuthCodeRequest = genRequest();
        final String okCertResponseBody = genResponseBody(name);

        // 외부서비스는 Mocking 한다.
        given(okCertClient.callOkCert(anyString(), anyString(), anyString(), anyString(), anyString())).willReturn(okCertResponseBody);
    }

    @Nested
    class WhenVerifyAuthCodeSuccess {

        @BeforeEach
        void setUp() {
            // 안 지우면 에러가 난다. 왜 그럴까?
            itUtil.findAllUserVerificationHistories().forEach(itUtil::deleteUserVerificationHistory);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @ParameterizedTest(name = "{index} => name=''{0}'', expectedName=''{1}'', firstNumberOfIdentification=''{2}''")
        @MethodSource("valueProvider")
        void shouldReturnSuccess(String name, String expectedName, Integer firstNumberOfIdentification) throws Exception {

            verifyAuthCodeRequest = genRequest(name, firstNumberOfIdentification);
            final String okCertResponseBody = genResponseBody(name);

            // 외부서비스는 Mocking 한다.
            given(okCertClient.callOkCert(anyString(), anyString(), anyString(), anyString(), anyString())).willReturn(okCertResponseBody);

            final MvcResult response = callApi(status().isOk());

            final ag.act.model.AuthUserResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.AuthUserResponse.class
            );

            assertResponse(result, expectedName);
            assertUserFromDatabase(result, expectedName, firstNumberOfIdentification);
        }

        private static Stream<Arguments> valueProvider() {
            return Stream.of(
                Arguments.of(" 이   동   훈    ", "이동훈", someIntegerBetween(1, 4)),
                Arguments.of(" 김   동훈    ", "김동훈", someIntegerBetween(1, 4)),
                Arguments.of(" 이동   훈    ", "이동훈", someIntegerBetween(1, 4)),
                Arguments.of("     이동훈    ", "이동훈", someIntegerBetween(1, 4)),
                Arguments.of("이동훈", "이동훈", someIntegerBetween(1, 4)),
                Arguments.of(" Lucas Lee    ", "Lucas Lee", someIntegerBetween(5, 8)),
                Arguments.of(" Lucas   Lee    ", "Lucas Lee", someIntegerBetween(5, 8)),
                Arguments.of("        Lucas   Lee    ", "Lucas Lee", someIntegerBetween(5, 8)),
                Arguments.of("Lucas    Lee    ", "Lucas Lee", someIntegerBetween(5, 8)),
                Arguments.of("    LucasLee    ", "LucasLee", someIntegerBetween(5, 8)),
                Arguments.of("BAEK SUNG JUN", "BAEK SUNG JUN", someIntegerBetween(5, 8)),
                Arguments.of("Lucas Lee", "Lucas Lee", someIntegerBetween(5, 8))
            );
        }
    }

    @Nested
    class WhenUserIsAlreadyWithdrawalRequested {

        @BeforeEach
        void setUp() {
            final User user = itUtil.updateUserWithCI(itUtil.createUser(), ci);
            itUtil.withdrawRequest(user);
        }

        @DisplayName("Should return 410 response code when call " + TARGET_API)
        @Test
        void shouldReturn410() throws Exception {
            final ResultMatcher resultMatcher = status().isGone();
            final MvcResult response = callApi(resultMatcher);

            itUtil.assertErrorResponse(response, 410, "탈퇴한 회원입니다.");
        }
    }

    @Nested
    class WhenUserIsAlreadyDeleted {

        @BeforeEach
        void setUp() {
            final User user = itUtil.updateUserWithCI(itUtil.createUser(), ci);
            user.setStatus(someThing(Status.DELETED_BY_USER, Status.DELETED_BY_ADMIN));
            user.setDeletedAt(LocalDateTime.now());
            itUtil.updateUser(user);
        }

        @DisplayName("Should return 410 response code when call " + TARGET_API)
        @Test
        void shouldReturn410() throws Exception {
            final ResultMatcher resultMatcher = status().isGone();
            final MvcResult response = callApi(resultMatcher);

            itUtil.assertErrorResponse(response, 410, "탈퇴한 회원입니다.");
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(verifyAuthCodeRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(ag.act.model.AuthUserResponse result, String name) {
        assertThat(result.getUser().getName(), is(name));
        assertThat(result.getUser().getGender(), is(gender));
        assertThat(result.getUser().getBirthDate().toString(), is(TestUtil.toIso8601Format(birthDate)));
        assertThat(result.getUser().getEmail(), is(nullValue()));
        assertThat(result.getUser().getNickname(), is(nullValue()));
        assertThat(result.getUser().getLastPinNumberVerifiedAt(), is(nullValue()));
        assertThat(result.getUser().getStatus(), is(Status.PROCESSING));
        assertThat(result.getUser().getAuthType(), is(AuthType.PIN));
        assertThat(result.getToken().getAccessToken(), is(notNullValue()));
    }

    private void assertUserFromDatabase(ag.act.model.AuthUserResponse result, String name, Integer firstNumberOfIdentification) {
        final Long userId = result.getUser().getId();

        final User user = itUtil.findUser(userId);
        assertThat(user.getName(), is(name));
        assertThat(user.getGender(), is(gender));
        assertThat(user.getStatus(), is(Status.PROCESSING));
        assertThat(user.getFirstNumberOfIdentification(), is(firstNumberOfIdentification));

        final UserVerificationHistory userVerificationHistory = itUtil.findFirstVerificationHistoryRepository(userId);
        assertThat(userVerificationHistory.getUserIp(), notNullValue());
        assertThat(userVerificationHistory.getVerificationType(), is(VerificationType.SMS));
        assertThat(userVerificationHistory.getOperationType(), is(REGISTER));
        assertThat(userVerificationHistory.getUserIp(), is("127.0.0.1"));
    }

    private ag.act.model.VerifyAuthCodeRequest genRequest() {
        return genRequest(name, firstNumberOfIdentification);
    }

    private ag.act.model.VerifyAuthCodeRequest genRequest(String name, Integer firstNumberOfIdentification) {
        ag.act.model.VerifyAuthCodeRequest verifyAuthCodeRequest = new ag.act.model.VerifyAuthCodeRequest();
        verifyAuthCodeRequest.setPhoneNumber(phoneNumber);
        verifyAuthCodeRequest.provider(someThing("01", "02", "03", "04", "05", "06"));
        verifyAuthCodeRequest.setGender(gender.toString());
        verifyAuthCodeRequest.setFirstNumberOfIdentification(firstNumberOfIdentification.toString());
        verifyAuthCodeRequest.setBirthDate(birthDate);
        verifyAuthCodeRequest.setName(name);
        verifyAuthCodeRequest.setCode(code);
        verifyAuthCodeRequest.setTxSeqNo(txSeqNo);
        return verifyAuthCodeRequest;
    }

    private String genResponseBody(String name) {
        final Map<String, String> body = new HashedMap<>();
        body.put("RSLT_CD", rsltCd);
        body.put("RSLT_MSG", rsltMsg);
        body.put("TX_SEQ_NO", txSeqNo);
        body.put("TEL_COM_RES_CD", telComResCd);
        body.put("TEL_COM_CD", telComCd);
        body.put("TEL_NO", phoneNumber);
        body.put("CI", ci);
        body.put("DI", di);
        body.put("CI_UPDATE", someString(14));
        body.put("RSLT_BIRTHDAY", birthDate);
        body.put("RSLT_NTV_FRNR_CD", someString(1));
        body.put("RSLT_NAME", name);
        body.put("RSLT_SEX_CD", gender.name());

        return objectMapperUtil.toRequestBody(body);
    }
}
