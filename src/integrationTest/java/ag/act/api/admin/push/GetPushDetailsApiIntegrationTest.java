package ag.act.api.admin.push;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Push;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.enums.push.PushTargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

public class GetPushDetailsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/pushes/{pushId}";

    private String jwt;
    private Push push;
    private String pushContent;
    private Long pushId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        pushContent = someAlphanumericString(30);
    }

    @Nested
    class WhenTargetAllPush {
        @BeforeEach
        void setUp() {
            push = itUtil.createPush(pushContent, PushTargetType.ALL);
            pushId = push.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.PushDetailsDataResponse result = callApiAndGetResult();

            assertResponse(result);
        }
    }

    @Nested
    class WhenTargetStockPush {
        private Stock stock;

        @BeforeEach
        void setUp() {
            stock = itUtil.createStock();
            push = itUtil.createPush(pushContent, stock, PushTargetType.STOCK);
            pushId = push.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.PushDetailsDataResponse result = callApiAndGetResult();

            assertResponse(result);
            assertThat(result.getData().getStockName(), is(stock.getName()));
        }
    }

    @Nested
    class WhenTargetStockGroupPush {
        private StockGroup stockGroup;

        @BeforeEach
        void setUp() {
            stockGroup = itUtil.createStockGroup(someAlphanumericString(30));
            push = itUtil.createPush(pushContent, stockGroup, PushTargetType.STOCK_GROUP);
            pushId = push.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final ag.act.model.PushDetailsDataResponse result = callApiAndGetResult();

            assertResponse(result);
            assertThat(result.getData().getStockGroupName(), is(stockGroup.getName()));
        }
    }

    private ag.act.model.PushDetailsDataResponse callApiAndGetResult() throws Exception {
        MvcResult result = mockMvc
            .perform(
                get(TARGET_API, pushId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            result.getResponse().getContentAsString(),
            ag.act.model.PushDetailsDataResponse.class
        );
    }

    private void assertResponse(ag.act.model.PushDetailsDataResponse dataResponse) {
        ag.act.model.PushDetailsResponse pushDetailsResponse = dataResponse.getData();

        assertThat(pushDetailsResponse.getId(), is(push.getId()));
        assertThat(pushDetailsResponse.getTitle(), is(push.getTitle()));
        assertThat(pushDetailsResponse.getContent(), is(push.getContent()));
        assertThat(pushDetailsResponse.getLinkUrl(), is(push.getLinkUrl()));
        assertThat(pushDetailsResponse.getLinkType(), is(push.getLinkType().name()));
        assertThat(pushDetailsResponse.getStockTargetType(), is(push.getPushTargetType().name()));
        assertThat(pushDetailsResponse.getStockCode(), is(push.getStockCode()));
        assertThat(pushDetailsResponse.getStockGroupId(), is(push.getStockGroupId()));
        assertThat(pushDetailsResponse.getSendType(), is(push.getSendType().name()));
        assertThat(pushDetailsResponse.getSendStatus(), is(push.getSendStatus().name()));
        assertThat(pushDetailsResponse.getTopic(), is(push.getTopic()));
        assertThat(pushDetailsResponse.getResult(), is(push.getResult()));
        assertTime(pushDetailsResponse.getTargetDatetime(), push.getTargetDatetime());
        assertTime(pushDetailsResponse.getSentStartDatetime(), push.getSentStartDatetime());
        assertTime(pushDetailsResponse.getSentEndDatetime(), push.getSentEndDatetime());
        assertTime(pushDetailsResponse.getCreatedAt(), push.getCreatedAt());
        assertTime(pushDetailsResponse.getUpdatedAt(), push.getUpdatedAt());
    }
}
