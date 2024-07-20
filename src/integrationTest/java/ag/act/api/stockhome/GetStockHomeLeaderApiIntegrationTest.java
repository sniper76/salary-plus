package ag.act.api.stockhome;

import ag.act.entity.SolidarityLeader;
import ag.act.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomThings.someThing;

public class GetStockHomeLeaderApiIntegrationTest extends GetStockHomeApiIntegrationTest {

    @Nested
    class WhenUserAlreadyAppliedForLeader {
        @BeforeEach
        void setUp() {
            itUtil.createSolidarityLeaderApplicant(solidarity.getId(), currentUser.getId());
        }

        @Test
        void shouldReturnSuccessWithAppliedLeaderStatus() throws Exception {
            ag.act.model.StockHomeResponse result = getResponse(callApi(status().isOk()));
            assertLeaderResponse(result, "Election in progress", true, null);
        }
    }

    @Nested
    class WhenAnotherLeaderAlreadyExists {
        private SolidarityLeader solidarityLeader;
        private User leader;

        @BeforeEach
        void setUp() {
            leader = itUtil.createUser();
            solidarityLeader = itUtil.createSolidarityLeader(solidarity, leader.getId());
        }

        @Nested
        class AndLeaderMessageExists {
            @BeforeEach
            void setUp() {
                solidarityLeader.setMessage("this is the leader message");
                itUtil.updateSolidarityLeader(solidarityLeader);
            }

            @Test
            void shouldReturnSuccessWithLeaderMessage() throws Exception {
                ag.act.model.StockHomeResponse result = getResponse(callApi(status().isOk()));

                String expectedLeaderMessage = "this is the leader message";
                assertLeaderResponse(result, "Elected", null, expectedLeaderMessage);
            }
        }

        @Nested
        class AndLeaderMessageNotExists {
            @BeforeEach
            void setUp() {
                solidarityLeader.setMessage(someThing(null, ""));
                itUtil.updateSolidarityLeader(solidarityLeader);
            }

            @Test
            void shouldReturnSuccessWithDefaultMessage() throws Exception {
                ag.act.model.StockHomeResponse result = getResponse(callApi(status().isOk()));

                String expectedLeaderMessage = "%s님이 주주대표로 선정되었습니다.\n주주대표 한마디가 곧 등록됩니다.".formatted(leader.getNickname());
                assertLeaderResponse(result, "Elected", null, expectedLeaderMessage);
            }
        }
    }

    private void assertLeaderResponse(
        ag.act.model.StockHomeResponse result, String expectedLeaderStatus, Boolean expectedApplied,
        String expectedLeaderMessage
    ) {
        final ag.act.model.StockHomeLeaderResponse leader = result.getLeader();

        assertThat(leader.getStatus(), is(expectedLeaderStatus));
        assertThat(leader.getApplied(), is(expectedApplied));
        assertThat(leader.getMessage(), is(expectedLeaderMessage));
        assertThat(leader.getSolidarityId(), is(solidarity.getId()));
    }
}
