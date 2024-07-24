package ag.act.api.push;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.enums.push.UserPushAgreementGroupType;
import ag.act.model.UserPushAgreementItem;
import ag.act.model.UserPushAgreementItemsDataResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetUserPushAgreementsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/push-agreements";
    private static final String CMS_GROUP_TITLE = "액트공지 / 종목알림 / 추천글";
    private static final String AUTHOR_GROUP_TITLE = "액트베스트 진입 / 새글";
    private static final String ALL_TITLE = "푸시 알림 받기(전체)";
    private static final List<String> CMS_AGREEMENT_TYPE_NAMES = List.of(
        "ACT_NOTICE",
        "STOCK_NOTICE",
        "RECOMMENDATION"
    );
    private static final List<String> AUTHOR_AGREEMENT_TYPE_NAMES = List.of(
        "ACT_BEST_ENTER",
        "NEW_COMMENT"
    );
    private static final String SUB_ITEM_TYPE_NAME = "SUB";
    private static final String ALL_ITEM_TYPE_NAME = "ALL";

    private String jwt;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
    }

    private void assertUserPushAgreementItem(
        UserPushAgreementItem item,
        String title,
        List<String> agreementTypeNames,
        boolean agreeToReceive,
        String userPushAgreementItemTypeName
    ) {
        assertThat(item.getTitle(), is(title));
        assertThat(item.getAgreementTypes(), is(agreementTypeNames));
        assertThat(item.getValue(), is(agreeToReceive));
        assertThat(item.getItemType(), is(userPushAgreementItemTypeName));
    }

    @Nested
    class WhenNoAgreementData {
        @Test
        void shouldReturnAllAgreed() throws Exception {
            List<ag.act.model.UserPushAgreementItem> response = callApiAndGetResult().getData();

            assertThat(response.size(), is(3));

            assertUserPushAgreementItem(
                response.get(0),
                ALL_TITLE,
                Stream.concat(
                    CMS_AGREEMENT_TYPE_NAMES.stream(),
                    AUTHOR_AGREEMENT_TYPE_NAMES.stream()
                ).toList(),
                true,
                ALL_ITEM_TYPE_NAME
            );
            assertUserPushAgreementItem(
                response.get(1),
                CMS_GROUP_TITLE,
                CMS_AGREEMENT_TYPE_NAMES,
                true,
                SUB_ITEM_TYPE_NAME
            );
            assertUserPushAgreementItem(
                response.get(2),
                AUTHOR_GROUP_TITLE,
                AUTHOR_AGREEMENT_TYPE_NAMES,
                true,
                SUB_ITEM_TYPE_NAME
            );
        }
    }

    @Nested
    class WhenDisagreeAll {
        @BeforeEach
        void setUp() {
            Arrays.stream(UserPushAgreementGroupType.values()).forEach(groupType ->
                groupType.getAgreementTypes().forEach(type ->
                    itUtil.createUserPushAgreement(user, type, false)
                )
            );
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            List<ag.act.model.UserPushAgreementItem> response = callApiAndGetResult().getData();

            assertThat(response.size(), is(3));

            assertUserPushAgreementItem(
                response.get(0),
                ALL_TITLE,
                Stream.concat(
                    CMS_AGREEMENT_TYPE_NAMES.stream(),
                    AUTHOR_AGREEMENT_TYPE_NAMES.stream()
                ).toList(),
                false,
                ALL_ITEM_TYPE_NAME
            );
            assertUserPushAgreementItem(
                response.get(1),
                CMS_GROUP_TITLE,
                CMS_AGREEMENT_TYPE_NAMES,
                false,
                SUB_ITEM_TYPE_NAME
            );
            assertUserPushAgreementItem(
                response.get(2),
                AUTHOR_GROUP_TITLE,
                AUTHOR_AGREEMENT_TYPE_NAMES,
                false,
                SUB_ITEM_TYPE_NAME
            );
        }
    }

    @Nested
    class WhenPartialAgreement {
        @BeforeEach
        void setUp() {
            UserPushAgreementGroupType.CMS.getAgreementTypes().forEach(type ->
                itUtil.createUserPushAgreement(user, type, false)
            );

            UserPushAgreementGroupType.AUTHOR.getAgreementTypes().forEach(type ->
                itUtil.createUserPushAgreement(user, type, true)
            );
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            List<ag.act.model.UserPushAgreementItem> response = callApiAndGetResult().getData();

            assertThat(response.size(), is(3));

            assertUserPushAgreementItem(
                response.get(0),
                ALL_TITLE,
                Stream.concat(
                    CMS_AGREEMENT_TYPE_NAMES.stream(),
                    AUTHOR_AGREEMENT_TYPE_NAMES.stream()
                ).toList(),
                true,
                ALL_ITEM_TYPE_NAME
            );
            assertUserPushAgreementItem(
                response.get(1),
                CMS_GROUP_TITLE,
                CMS_AGREEMENT_TYPE_NAMES,
                false,
                SUB_ITEM_TYPE_NAME
            );
            assertUserPushAgreementItem(
                response.get(2),
                AUTHOR_GROUP_TITLE,
                AUTHOR_AGREEMENT_TYPE_NAMES,
                true,
                SUB_ITEM_TYPE_NAME
            );
        }
    }

    private UserPushAgreementItemsDataResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.UserPushAgreementItemsDataResponse.class
        );
    }
}
