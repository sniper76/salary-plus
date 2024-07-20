package ag.act.api.home;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.api.home.util.HomeApiIntegrationTestHelper;
import ag.act.entity.User;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class UpdateMySolidarityApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/home/my-solidarity";
    private HomeApiIntegrationTestHelper homeTestHelper;
    private User user;
    private String jwt;
    private ag.act.model.UpdateMySolidarityRequest request;

    @BeforeEach
    void setUp() {
        itUtil.init();
        dbCleaner.clean();

        homeTestHelper = itUtil.getHomeTestHelper();
        user = itUtil.createUser(someNumericString(6));
        jwt = itUtil.createJwt(user.getId());
    }

    @Nested
    class UpdateMySolidaritySuccess {
        @BeforeEach
        void setUp() {
            homeTestHelper.mockUserHoldingStock(user, "000000", 0, Status.ACTIVE);
            homeTestHelper.mockUserHoldingStock(user, "000001", 1, Status.ACTIVE);
            homeTestHelper.mockUserHoldingStock(user, "000002", 2, Status.ACTIVE);
            homeTestHelper.mockUserHoldingStock(user, "000003", 10000, Status.INACTIVE_BY_ADMIN);

            request = genUpdateMySolidarityRequest();
        }

        private ag.act.model.UpdateMySolidarityRequest genUpdateMySolidarityRequest() {
            ag.act.model.UpdateMySolidarityRequest request = new ag.act.model.UpdateMySolidarityRequest();
            request.setData(List.of("000002", "000001", "000000"));
            return request;
        }

        @DisplayName("Should return 200 response with updated solidarities " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_API)
                        .content(objectMapperUtil.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.MySolidarityDataArrayResponse result =
                objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    ag.act.model.MySolidarityDataArrayResponse.class
                );

            assertResponse(result);
        }

        private void assertResponse(ag.act.model.MySolidarityDataArrayResponse result) {
            assertThat(result.getData().size(), is(4));

            assertThat(result.getData().get(0).getCode(), is("000002"));
            assertThat(result.getData().get(0).getDisplayOrder(), is(0));

            assertThat(result.getData().get(1).getCode(), is("000001"));
            assertThat(result.getData().get(1).getDisplayOrder(), is(1));

            assertThat(result.getData().get(2).getCode(), is("000000"));
            assertThat(result.getData().get(2).getDisplayOrder(), is(2));

            assertThat(result.getData().get(3).getCode(), is("000003"));
            assertThat(result.getData().get(3).getDisplayOrder(), is(10000));

        }
    }

    @Nested
    class WhenNotMyStockIncluded {
        @BeforeEach
        void setUp() {
            homeTestHelper.mockUserHoldingStock(user, "000000", 0, Status.ACTIVE);
            homeTestHelper.mockUserHoldingStock(user, "000001", 1, Status.ACTIVE);

            request = genUpdateMySolidarityRequest();
        }

        private ag.act.model.UpdateMySolidarityRequest genUpdateMySolidarityRequest() {
            ag.act.model.UpdateMySolidarityRequest request = new ag.act.model.UpdateMySolidarityRequest();
            request.setData(List.of("000002", someThing("000000", "000001")));
            return request;
        }

        @DisplayName("Should return 400 response when not my stock included " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_API)
                        .content(objectMapperUtil.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, "처리할 수 없는 종목 코드가 존재합니다. (000002)");
        }
    }

    @Nested
    class WhenNotAllActiveStocksIncluded {
        @BeforeEach
        void setUp() {
            homeTestHelper.mockUserHoldingStock(user, "000000", 0, Status.ACTIVE);
            homeTestHelper.mockUserHoldingStock(user, "000001", 1, Status.INACTIVE_BY_ADMIN);

            request = genUpdateMySolidarityRequest();
        }

        private ag.act.model.UpdateMySolidarityRequest genUpdateMySolidarityRequest() {
            ag.act.model.UpdateMySolidarityRequest request = new ag.act.model.UpdateMySolidarityRequest();
            request.setData(List.of("000001"));
            return request;
        }

        @DisplayName("Should return 400 response when not all stocks included " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_API)
                        .content(objectMapperUtil.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, "처리할 수 없는 종목 코드가 존재합니다. (000001)");
        }
    }

    @Nested
    class WhenDuplicatedStocksIncluded {
        @BeforeEach
        void setUp() {
            homeTestHelper.mockUserHoldingStock(user, "000000", 0, Status.ACTIVE);
            homeTestHelper.mockUserHoldingStock(user, "000001", 1, Status.ACTIVE);

            request = genUpdateMySolidarityRequest();
        }

        private ag.act.model.UpdateMySolidarityRequest genUpdateMySolidarityRequest() {
            ag.act.model.UpdateMySolidarityRequest request = new ag.act.model.UpdateMySolidarityRequest();
            request.setData(List.of("000000", "000000"));
            return request;
        }

        @DisplayName("Should return 400 response when duplicated stocks included " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_API)
                        .content(objectMapperUtil.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, "중복된 종목 코드가 존재합니다.");
        }
    }
}
