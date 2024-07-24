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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

public class UpdateStockGroupApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stock-groups/{stockGroupId}";

    private String jwt;
    private ag.act.model.UpdateStockGroupRequest request;
    private List<String> stockCodes;
    private String stockGroupName;
    private StockGroup existingStockGroup;
    private Long stockGroupId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        existingStockGroup = itUtil.createStockGroup(someAlphanumericString(10));
        stockGroupId = existingStockGroup.getId();

        itUtil.createStockGroupMapping(itUtil.createStock().getCode(), stockGroupId);
        itUtil.createStockGroupMapping(itUtil.createStock().getCode(), stockGroupId);
        itUtil.createStockGroupMapping(itUtil.createStock().getCode(), stockGroupId);
    }

    @Nested
    class UpdateStockGroupSuccessfully {

        @Nested
        class WithStockCodes {
            @BeforeEach
            void setUp() {
                stockCodes = List.of(
                    itUtil.createStock().getCode(),
                    itUtil.createStock().getCode(),
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
        class WithInvalidStockGroupName {

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @ParameterizedTest(name = "{index} => stockGroupName=''{0}''")
            @MethodSource("valueProvider")
            void shouldReturnSuccessButNotUpdatedStockGroupName(String stockGroupName) throws Exception {
                // Given
                stockCodes = List.of(
                    itUtil.createStock().getCode(),
                    itUtil.createStock().getCode()
                );
                request = genRequest(stockGroupName, stockCodes);

                // When
                final StockGroupDataResponse result = callApiAndGetResult();

                // Then
                assertResponse(existingStockGroup.getName(), stockCodes.size(), result);
            }

            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of(""),
                    Arguments.of("  "),
                    Arguments.of("       "),
                    Arguments.of((String) null)
                );
            }
        }

        @Nested
        class WithInvalidStockGroupDescription {

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @ParameterizedTest(name = "{index} => description=''{0}''")
            @MethodSource("valueProvider")
            void shouldReturnSuccess(String description) throws Exception {
                // Given
                stockCodes = List.of(
                    itUtil.createStock().getCode(),
                    itUtil.createStock().getCode()
                );
                request = genRequest(existingStockGroup.getName(), description, stockCodes);

                // When
                final StockGroupDataResponse result = callApiAndGetResult();

                // Then
                assertResponse(existingStockGroup.getName(), stockCodes.size(), result);
            }

            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of(""),
                    Arguments.of("  "),
                    Arguments.of("       "),
                    Arguments.of((String) null)
                );
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
    class FailToUpdateStockGroup {

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

        @Nested
        class WhenNotFoundStockGroup {

            @BeforeEach
            void setUp() {
                stockCodes = List.of();
                stockGroupName = someAlphanumericString(10);
                stockGroupId = somePositiveLong(); // wrong id
                request = genRequest(stockGroupName, stockCodes);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequestException() throws Exception {
                final MvcResult response = callApiForBadRequest(status().isNotFound());

                itUtil.assertErrorResponse(response, 404, "종목그룹을 찾을 수 없습니다.");
            }
        }
    }

    private MvcResult callApiForBadRequest() throws Exception {
        return callApiForBadRequest(status().isBadRequest());
    }

    private MvcResult callApiForBadRequest(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                put(TARGET_API, stockGroupId)
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private StockGroupDataResponse callApiAndGetResult() throws Exception {
        MvcResult response = callApiForBadRequest(status().isOk());

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            StockGroupDataResponse.class
        );
    }

    private void assertResponse(String stockGroupName, Integer stockCount, StockGroupDataResponse result) {
        final StockGroup stockGroup = itUtil.findStockGroupById(existingStockGroup.getId()).orElseThrow();
        final StockGroupResponse stockGroupResponse = result.getData();

        assertThat(stockGroupResponse.getId(), is(stockGroup.getId()));
        assertThat(stockGroupResponse.getName(), is(stockGroup.getName()));
        assertThat(stockGroupResponse.getStatus(), is(stockGroup.getStatus()));
        assertThat(stockGroupResponse.getDescription(), is(stockGroup.getDescription()));
        assertTime(stockGroupResponse.getCreatedAt(), stockGroup.getCreatedAt());
        assertTime(stockGroupResponse.getUpdatedAt(), stockGroup.getUpdatedAt());
        assertTime(stockGroupResponse.getDeletedAt(), stockGroup.getDeletedAt());
        assertThat(stockGroupResponse.getStockCount(), is(stockCount));

        assertThat(stockGroup.getName(), is(stockGroupName));
    }

    private ag.act.model.UpdateStockGroupRequest genRequest(String name, List<String> stockCodes) {
        return genRequest(name, someAlphanumericString(50), stockCodes);
    }

    private ag.act.model.UpdateStockGroupRequest genRequest(String name, String description, List<String> stockCodes) {
        return new ag.act.model.UpdateStockGroupRequest()
            .name(name)
            .description(description)
            .stockCodes(stockCodes);
    }
}
