package ag.act.api.admin.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.model.GetUserDummyStockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.someLocalDate;
import static ag.act.TestUtil.someStockCode;
import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetUserDummyStocksApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/users/{userId}/dummy-stock";
    private static final int TOTAL_ELEMENTS = 4;
    private String jwt;
    private Map<String, Object> params;
    private Integer pageNumber;
    private User user;
    private Long userId;
    private Stock stock1;
    private Stock stock2;
    private Stock stock3;
    private Stock stock4;
    private String stockCode1;
    private String stockCode2;
    private String stockCode3;
    private String stockCode4;
    private LocalDate registerDate1;
    private LocalDate registerDate2;
    private LocalDate registerDate3;
    private LocalDate registerDate4;
    private UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate1;
    private UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate2;
    private UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate3;
    private UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate4;

    @BeforeEach
    void setUp() {
        itUtil.init();

        final User currentAdminUser = itUtil.someAdminUser();
        jwt = itUtil.createJwt(currentAdminUser.getId());

        user = itUtil.createAdminUser();
        userId = user.getId();

        stockCode1 = someStockCode();
        stock1 = mockStock(stockCode1);
        registerDate1 = mockUserHoldingStockAndGetRegisterDate(stockCode1);
        userHoldingStockOnReferenceDate1 = mockUserHoldingStockOnReferenceDate(stockCode1);

        stockCode2 = someStockCode();
        stock2 = mockStock(stockCode2);
        registerDate2 = mockUserHoldingStockAndGetRegisterDate(stockCode2);
        userHoldingStockOnReferenceDate2 = mockUserHoldingStockOnReferenceDate(stockCode2);

        stockCode3 = someStockCode();
        stock3 = mockStock(stockCode3);
        registerDate3 = mockUserHoldingStockAndGetRegisterDate(stockCode3);
        userHoldingStockOnReferenceDate3 = mockUserHoldingStockOnReferenceDate(stockCode3);

        stockCode4 = someStockCode();
        stock4 = mockStock(stockCode4);
        registerDate4 = mockUserHoldingStockAndGetRegisterDate(stockCode4);
        userHoldingStockOnReferenceDate4 = mockUserHoldingStockOnReferenceDate(stockCode4);
    }

    private LocalDate mockUserHoldingStockAndGetRegisterDate(String stockCode) {
        return itUtil.createDummyUserHoldingStock(stockCode, user).getCreatedAt().toLocalDate();
    }

    private Stock mockStock(String stockCode) {
        return itUtil.createStock(stockCode);
    }

    private UserHoldingStockOnReferenceDate mockUserHoldingStockOnReferenceDate(String stockCode) {
        LocalDate referenceDate = someLocalDate();
        itUtil.createStockReferenceDate(stockCode, referenceDate);
        return itUtil.createUserHoldingStockOnReferenceDate(stockCode, userId, referenceDate);
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
                assertResponse(callApiAndGetResult(), TOTAL_ELEMENTS);
            }

            private void assertResponse(ag.act.model.GetUserDummyStockResponse result, long totalElements) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.UserDummyStockResponse> userDummyStockResponses = result.getData();
                
                assertPaging(paging, totalElements);
                assertThat(userDummyStockResponses.size(), is(SIZE));
                assertPostResponse(stock4, userHoldingStockOnReferenceDate4, registerDate4, userDummyStockResponses.get(0));
                assertPostResponse(stock3, userHoldingStockOnReferenceDate3, registerDate3, userDummyStockResponses.get(1));
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
            void shouldReturnSuccess() throws Exception {
                assertResponse(callApiAndGetResult(), TOTAL_ELEMENTS);
            }

            private void assertResponse(ag.act.model.GetUserDummyStockResponse result, long totalElements) {
                final ag.act.model.Paging paging = result.getPaging();
                final List<ag.act.model.UserDummyStockResponse> userDummyStockResponses = result.getData();

                assertPaging(paging, totalElements);
                assertThat(userDummyStockResponses.size(), is(SIZE));
                assertPostResponse(stock2, userHoldingStockOnReferenceDate2, registerDate2, userDummyStockResponses.get(0));
                assertPostResponse(stock1, userHoldingStockOnReferenceDate1, registerDate1, userDummyStockResponses.get(1));
            }
        }
    }

    private GetUserDummyStockResponse callApiAndGetResult() throws Exception {
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
            GetUserDummyStockResponse.class
        );
    }

    private void assertPostResponse(
            Stock stock,
            UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate,
            LocalDate registerDate,
            ag.act.model.UserDummyStockResponse userDummyStockResponse
    ) {
        assertThat(userDummyStockResponse.getCode(), is(userHoldingStockOnReferenceDate.getStockCode()));
        assertThat(userDummyStockResponse.getName(), is(stock.getName()));
        assertThat(userDummyStockResponse.getQuantity(), is(userHoldingStockOnReferenceDate.getQuantity()));
        assertThat(userDummyStockResponse.getReferenceDate(), is(userHoldingStockOnReferenceDate.getReferenceDate()));
        assertThat(userDummyStockResponse.getRegisterDate(), is(registerDate));
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