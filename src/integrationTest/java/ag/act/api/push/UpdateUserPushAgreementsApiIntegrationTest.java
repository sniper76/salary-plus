package ag.act.api.push;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.entity.UserPushAgreement;
import ag.act.enums.push.UserPushAgreementType;
import ag.act.model.UserPushAgreementItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UpdateUserPushAgreementsApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/users/push-agreements";
    private static final String ACT_NOTICE = "ACT_NOTICE";
    private static final String ACT_BEST_ENTER = "ACT_BEST_ENTER";
    private static final boolean ACT_NOTICE_AGREE = true;
    private static final boolean ACT_BEST_ENTER_AGREE = false;

    private final List<ag.act.model.UserPushAgreementItem> request = List.of(
        new UserPushAgreementItem()
            .agreementTypes(List.of(ACT_NOTICE))
            .value(ACT_NOTICE_AGREE),
        new UserPushAgreementItem()
            .agreementTypes(List.of(ACT_BEST_ENTER))
            .value(ACT_BEST_ENTER_AGREE)
    );
    private String jwt;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
    }

    private void callApi() throws Exception {
        mockMvc.perform(
                patch(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk());
    }

    @Nested
    class WhenNoPreviousAgreement {
        @Test
        void shouldReturnAllAgreed() throws Exception {
            callApi();

            UserPushAgreement actNoticeAgreement = itUtil.findUserPushAgreement(
                user, UserPushAgreementType.ACT_NOTICE
            );
            UserPushAgreement actBestEnterAgreement = itUtil.findUserPushAgreement(
                user, UserPushAgreementType.ACT_BEST_ENTER
            );

            assertThat(actNoticeAgreement.getAgreeToReceive(), is(ACT_NOTICE_AGREE));
            assertThat(actBestEnterAgreement.getAgreeToReceive(), is(ACT_BEST_ENTER_AGREE));
        }
    }

    @Nested
    class WhenPreviousAgreementExist {
        @BeforeEach
        void setUp() {
            itUtil.createUserPushAgreement(user, UserPushAgreementType.ACT_NOTICE, false);
            itUtil.createUserPushAgreement(user, UserPushAgreementType.ACT_BEST_ENTER, true);
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            callApi();

            UserPushAgreement actNoticeAgreement = itUtil.findUserPushAgreement(
                user, UserPushAgreementType.ACT_NOTICE
            );
            UserPushAgreement actBestEnterAgreement = itUtil.findUserPushAgreement(
                user, UserPushAgreementType.ACT_BEST_ENTER
            );

            assertThat(actNoticeAgreement.getAgreeToReceive(), is(ACT_NOTICE_AGREE));
            assertThat(actBestEnterAgreement.getAgreeToReceive(), is(ACT_BEST_ENTER_AGREE));
        }
    }
}
