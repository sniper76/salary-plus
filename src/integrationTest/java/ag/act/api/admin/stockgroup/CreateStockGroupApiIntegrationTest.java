package ag.act.api.admin.stockgroup;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.model.StockGroupDataResponse;
import ag.act.model.StockGroupResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class CreateStockGroupApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stock-groups";

    private String jwt;
    private ag.act.model.CreateStockGroupRequest request;
    private List<String> stockCodes;
    private String stockGroupName;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
    }

    @Nested
    class CreateStockGroupSuccessfully {

        @Nested
        class WithStockCodes {
            @BeforeEach
            void setUp() {
                stockCodes = List.of(
                    itUtil.createStock().getCode(),
                    itUtil.createStock().getCode(),
                    itUtil.createStock().getCode()
                );
                stockGroupName = someAlphanumericString(10);
                request = genRequest(stockGroupName, stockCodes);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final StockGroupDataResponse result = callApiAndGetResult();
                assertResponse(stockGroupName, stockCodes.size(), result);
            }
        }

        @Nested
        class WithoutStockCodes {
            @BeforeEach
            void setUp() {
                stockCodes = List.of();
                stockGroupName = someAlphanumericString(10);
                request = genRequest(stockGroupName, stockCodes);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final StockGroupDataResponse result = callApiAndGetResult();
                assertResponse(stockGroupName, stockCodes.size(), result);
            }
        }
    }

    @Nested
    class FailToCreateStockGroup {

        @Nested
        class WhenNotFoundStockCode {

            private String notFoundStockCode;

            @BeforeEach
            void setUp() {
                notFoundStockCode = someStockCode();

                stockCodes = List.of(
                    itUtil.createStock().getCode(),
                    notFoundStockCode,
                    itUtil.createStock().getCode()
                );
                stockGroupName = someAlphanumericString(10);
                request = genRequest(stockGroupName, stockCodes);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequestException() throws Exception {
                final MvcResult response = callApiForBadRequest();

                itUtil.assertErrorResponse(response, 400, "해당 종목코드를 찾을 수 없습니다. (%s)".formatted(notFoundStockCode));
            }
        }

        @Nested
        class WhenStockGroupNameIsAlreadyExist {

            @BeforeEach
            void setUp() {

                stockCodes = List.of(
                    itUtil.createStock().getCode(),
                    itUtil.createStock().getCode()
                );
                stockGroupName = someAlphanumericString(10);
                request = genRequest(stockGroupName, stockCodes);

                itUtil.createStockGroup(stockGroupName); // same group name is already exist
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequestException() throws Exception {
                final MvcResult response = callApiForBadRequest();

                itUtil.assertErrorResponse(response, 400, "동일한 종목그룹이 존재합니다.");
            }
        }
    }

    private MvcResult callApiForBadRequest() throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    private StockGroupDataResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            StockGroupDataResponse.class
        );
    }

    private void assertResponse(String stockGroupName, Integer stockCount, StockGroupDataResponse result) {
        final StockGroup stockGroup = itUtil.findStockGroupByName(stockGroupName).orElseThrow();
        final StockGroupResponse stockGroupResponse = result.getData();

        assertThat(stockGroupResponse.getId(), is(stockGroup.getId()));
        assertThat(stockGroupResponse.getName(), is(stockGroup.getName()));
        assertThat(stockGroupResponse.getStatus(), is(stockGroup.getStatus()));
        assertThat(stockGroupResponse.getDescription(), is(stockGroup.getDescription()));
        assertTime(stockGroupResponse.getCreatedAt(), stockGroup.getCreatedAt());
        assertTime(stockGroupResponse.getUpdatedAt(), stockGroup.getUpdatedAt());
        assertTime(stockGroupResponse.getDeletedAt(), stockGroup.getDeletedAt());
        assertThat(stockGroupResponse.getStockCount(), is(stockCount));
    }

    private ag.act.model.CreateStockGroupRequest genRequest(String name, List<String> stockCodes) {
        return new ag.act.model.CreateStockGroupRequest()
            .name(name)
            .description(someString(50))
            .stockCodes(stockCodes);
    }
}
