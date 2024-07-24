package ag.act.api.home;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.enums.LinkType;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

public class UpdateHomeLinkApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/links/{linkType}";
    private static final String VALID_LINK_URL_EXAMPLE = "https://blog.naver.com/conduit_act/223233886536";
    private static final String LINK_TYPE_RANKING_NAME = LinkType.RANKING.name();
    private static final String INVALID_LINK_TYPE_EXAMPLE = someAlphanumericString(10);

    private String jwt;
    private ag.act.model.UpdateHomeLinkRequest request;
    private String linkTypeString;
    private String newLinkUrl;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
    }

    @Nested
    class WhenUpdateSuccess {

        @Nested
        class AndValidUrl {

            @BeforeEach
            void setUp() {
                linkTypeString = LINK_TYPE_RANKING_NAME;
                newLinkUrl = VALID_LINK_URL_EXAMPLE;
                request = genRequest(newLinkUrl);
            }

            @Test
            @DisplayName("Should return 200 response with updated home link " + TARGET_API)
            void shouldReturnSuccess() throws Exception {
                ag.act.model.HomeLinkResponse result = getDataResponse(callApi(status().isOk()));

                assertThat(result.getLinkUrl(), is(newLinkUrl));
                assertThat(result.getStatus(), is(Status.ACTIVE));
            }
        }

        @Nested
        class AndValidUrlButHaveSomeSpace {

            @BeforeEach
            void setUp() {
                linkTypeString = LINK_TYPE_RANKING_NAME;
                newLinkUrl = "   " + VALID_LINK_URL_EXAMPLE + "  ";
                request = genRequest(newLinkUrl);
            }

            @Test
            @DisplayName("Should return 200 response with updated home link " + TARGET_API)
            void shouldReturnSuccess() throws Exception {
                ag.act.model.HomeLinkResponse result = getDataResponse(callApi(status().isOk()));

                assertThat(result.getLinkUrl(), is(newLinkUrl.trim()));
                assertThat(result.getStatus(), is(Status.ACTIVE));
            }
        }
    }

    @Nested
    class WhenNotFoundMatchingLinkType {

        @BeforeEach
        void setUp() {
            linkTypeString = INVALID_LINK_TYPE_EXAMPLE;
            newLinkUrl = VALID_LINK_URL_EXAMPLE;
            request = genRequest(newLinkUrl);
        }

        @Test
        void shouldReturnBadRequestException() throws Exception {
            final MvcResult mvcResult = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(mvcResult, 400, "존재하지 않는 링크 타입 이름입니다.");
        }
    }

    @Nested
    class WhenInvalidLinkUrl {
        @BeforeEach
        void setUp() {
            linkTypeString = LINK_TYPE_RANKING_NAME;
            newLinkUrl = someAlphanumericString(30);
            request = genRequest(newLinkUrl);
        }

        @Test
        void shouldReturnBadRequestException() throws Exception {
            final MvcResult mvcResult = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(mvcResult, 400, "url 형식이 잘못되었습니다. 올바른 url을 입력해주세요.");
        }
    }

    @Nested
    class WhenLinkUrlNull {
        @BeforeEach
        void setUp() {
            linkTypeString = LINK_TYPE_RANKING_NAME;
            request = genRequest(null);
        }

        @Test
        void shouldReturnBadRequestException() throws Exception {
            final MvcResult mvcResult = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(mvcResult, 400, "url을 확인해주세요.");
        }
    }

    @Nested
    class WhenLinkUrlEmpty {
        @BeforeEach
        void setUp() {
            linkTypeString = LINK_TYPE_RANKING_NAME;
            request = genRequest("");
        }

        @Test
        void shouldReturnBadRequestException() throws Exception {
            final MvcResult mvcResult = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(mvcResult, 400, "url을 확인해주세요.");
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API, linkTypeString)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private ag.act.model.HomeLinkResponse getDataResponse(MvcResult response) throws Exception {
        return itUtil.getResult(response, ag.act.model.HomeLinkResponse.class);
    }

    private ag.act.model.UpdateHomeLinkRequest genRequest(String linkUrl) {
        return new ag.act.model.UpdateHomeLinkRequest()
            .linkUrl(linkUrl);
    }
}
