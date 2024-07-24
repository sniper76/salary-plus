package ag.act.api.admin.stockgroup;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

public class DeleteStockGroupApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stock-groups/{stockGroupId}";

    private String jwt;
    private Long stockGroupId;
    private StockGroup existingStockGroup;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
    }

    @Nested
    class DeleteStockGroupSuccessfully {

        @BeforeEach
        void setUp() {
            existingStockGroup = itUtil.createStockGroup(someAlphanumericString(10));

            stockGroupId = existingStockGroup.getId();

            itUtil.createStockGroupMapping(itUtil.createStock().getCode(), stockGroupId);
            itUtil.createStockGroupMapping(itUtil.createStock().getCode(), stockGroupId);
            itUtil.createStockGroupMapping(itUtil.createStock().getCode(), stockGroupId);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.SimpleStringResponse result = callApiAndGetResult();
            StockGroup deletedStockGroup = itUtil.findStockGroupById(existingStockGroup.getId()).get();

            assertResponse(result.getStatus());
            assertThat(deletedStockGroup.getStatus(), is(Status.DELETED));
        }
    }

    @Nested
    class FailToDeleteStockGroup {

        @Nested
        class WhenNotFoundStockGroup {

            @BeforeEach
            void setUp() {
                stockGroupId = somePositiveLong(); // wrong id
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequestException() throws Exception {
                final MvcResult response = callApi(status().isNotFound());

                itUtil.assertErrorResponse(response, 404, "종목그룹을 찾을 수 없습니다.");
            }
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                delete(TARGET_API, stockGroupId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private ag.act.model.SimpleStringResponse callApiAndGetResult() throws Exception {
        MvcResult response = callApi(status().isOk());

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.SimpleStringResponse.class
        );
    }

    private void assertResponse(String status) {
        assertThat(status, is("ok"));
    }
}
