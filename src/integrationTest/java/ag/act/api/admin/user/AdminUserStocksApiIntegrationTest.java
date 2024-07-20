package ag.act.api.admin.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.MyDataSummary;
import ag.act.entity.User;
import ag.act.entity.mydata.JsonMyDataStock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;

public class AdminUserStocksApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/users/{userId}/stocks";

    private String jwt;
    private Map<String, Object> params;
    private Integer pageNumber;

    private User user;
    private MyDataSummary myDataSummary;
    private Long userId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        int stockListSize = someIntegerBetween(4, 6);
        user = itUtil.createUser();
        User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
        userId = user.getId();
        myDataSummary = itUtil.createMyDataSummaryWithStockListSize(user, stockListSize);

    }

    @Nested
    class WhenRetrieveUserStocks {

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "userId", user.getId(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final long totalElements = myDataSummary.getJsonMyData().getJsonMyDataStockList().size();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(ag.act.model.GetUserStockResponse result, long totalElements) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.UserStockResponse> userStockResponses = result.getData();

                assertPaging(paging, totalElements);
                assertThat(userStockResponses.size(), is(SIZE));
                assertPostResponse(myDataSummary.getJsonMyData().getJsonMyDataStockList().get(0), userStockResponses.get(0));
                assertPostResponse(myDataSummary.getJsonMyData().getJsonMyDataStockList().get(1), userStockResponses.get(1));
            }
        }

        @Nested
        class AndSecondPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = Map.of(
                    "userId", user.getId(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnUsers() throws Exception {
                final long totalElements = myDataSummary.getJsonMyData().getJsonMyDataStockList().size();
                assertResponse(callApiAndGetResult(), totalElements);
            }

            private void assertResponse(ag.act.model.GetUserStockResponse result, long totalElements) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.UserStockResponse> userStockResponses = result.getData();

                assertPaging(paging, totalElements);
                assertThat(userStockResponses.size(), is(SIZE));
                assertPostResponse(myDataSummary.getJsonMyData().getJsonMyDataStockList().get(2), userStockResponses.get(0));
                assertPostResponse(myDataSummary.getJsonMyData().getJsonMyDataStockList().get(3), userStockResponses.get(1));
            }
        }
    }

    private ag.act.model.GetUserStockResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API, userId)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.GetUserStockResponse.class
        );
    }

    private void assertPostResponse(JsonMyDataStock jsonMyDataStock, ag.act.model.UserStockResponse userStockResponse) {
        assertThat(userStockResponse.getCode(), is(jsonMyDataStock.getCode()));
        assertThat(userStockResponse.getName(), is(jsonMyDataStock.getName()));
        assertThat(userStockResponse.getQuantity(), is(jsonMyDataStock.getQuantity()));
        assertThat(userStockResponse.getReferenceDate(), is(jsonMyDataStock.getReferenceDate()));
        assertThat(userStockResponse.getReferenceDate(), is(jsonMyDataStock.getRegisterDate()));
    }

    private void assertPaging(ag.act.model.Paging paging, long totalElements) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is(CREATED_AT_DESC));
    }
}
