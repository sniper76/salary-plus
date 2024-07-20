package ag.act.api.smsauth;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.TestUtil;
import ag.act.model.Gender;
import ag.act.model.SendAuthRequest;
import ag.act.model.SendAuthResponse;
import kcb.module.v3.exception.OkCertException;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static ag.act.TestUtil.someBirthDayString;
import static ag.act.TestUtil.somePhoneNumber;
import static ag.act.TestUtil.somePhoneProvider;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class SendAuthRequestIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/sms/send-auth-request";

    private SendAuthRequest sendAuthRequest;
    private String txSeqNo;
    private String telComResCd;
    private String rsltMsg;
    private String rsltCd;

    @BeforeEach
    void setUp() throws OkCertException {

        itUtil.init();

        txSeqNo = someString(30);
        telComResCd = someString(40);
        rsltMsg = someString(40);
        rsltCd = "B000";
        sendAuthRequest = genRequest();

        final String okCertResponseBody = genResponse();

        // 외부서비스는 Mocking 한다.
        given(okCertClient.callOkCert(anyString(), anyString(), anyString(), anyString(), anyString())).willReturn(okCertResponseBody);
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldReturnSuccess() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(sendAuthRequest))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final SendAuthResponse result = objectMapperUtil.readValue(response.getResponse().getContentAsString(), SendAuthResponse.class);

        assertThat(result.getResultCode(), is(rsltCd));
        assertThat(result.getResultMessage(), is(rsltMsg));
        assertThat(result.getTxSeqNo(), is(txSeqNo));
    }

    @SuppressWarnings("deprecation")
    private ag.act.model.SendAuthRequest genRequest() {
        final Gender gender = someEnum(Gender.class);
        final String birthDate = someBirthDayString();

        ag.act.model.SendAuthRequest sendAuthRequest = new ag.act.model.SendAuthRequest();
        sendAuthRequest.setPhoneNumber(somePhoneNumber());
        sendAuthRequest.provider(somePhoneProvider());
        sendAuthRequest.setGender(gender.toString());
        sendAuthRequest.setBirthDate(birthDate);
        sendAuthRequest.setFirstNumberOfIdentification(TestUtil.getFirstNumberOfIdentification(birthDate, gender).toString());
        sendAuthRequest.setName(someAlphanumericString(10));
        return sendAuthRequest;
    }

    private String genResponse() {
        final Map<String, String> body = new HashedMap<>();
        body.put("TEL_COM_CD", someString(17));
        body.put("TX_SEQ_NO", txSeqNo);
        body.put("MDL_TKN", someString(35));
        body.put("TEL_COM_RES_CD", telComResCd);
        body.put("RSLT_MSG", rsltMsg);
        body.put("RSLT_CD", rsltCd);

        return objectMapperUtil.toRequestBody(body);
    }
}
