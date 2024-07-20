package ag.act.api.home;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.api.home.util.HomeApiIntegrationTestHelper;
import ag.act.entity.User;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;

public class GetMySolidarityApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/home/my-solidarity";
    private String jwt;

    @BeforeEach
    void setUp() {

        itUtil.init();
        final HomeApiIntegrationTestHelper homeTestHelper = itUtil.getHomeTestHelper();
        final User user = itUtil.createUser(someNumericString(6));
        jwt = itUtil.createJwt(user.getId());

        homeTestHelper.mockUserHoldingStock(user, "000000", 0, Status.ACTIVE);
        homeTestHelper.mockUserHoldingStock(user, "000001", 1, Status.ACTIVE);
        homeTestHelper.mockUserHoldingStock(user, "000002", "에코프로", 100000, Status.ACTIVE);
        homeTestHelper.mockUserHoldingStock(user, "000003", "삼성전자우", 100000, Status.ACTIVE);
        homeTestHelper.mockUserHoldingStock(user, "000004", "삼성전자", 100000, Status.ACTIVE);
    }

    @DisplayName("Should return 200 response my solidarity " + TARGET_API)
    @Test
    void shouldReturnSuccess() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        final ag.act.model.MySolidarityDataArrayResponse result =
            objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.MySolidarityDataArrayResponse.class
            );

        assertThat(result.getData().size(), is(5));

        assertThat(result.getData().get(0).getCode(), is("000000"));
        assertThat(result.getData().get(1).getCode(), is("000001"));
        assertThat(result.getData().get(2).getName(), is("삼성전자"));
        assertThat(result.getData().get(3).getName(), is("삼성전자우"));
        assertThat(result.getData().get(4).getName(), is("에코프로"));
    }
}
