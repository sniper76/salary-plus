package ag.act.api.batch;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.BatchLog;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.RoleType;
import ag.act.model.Status;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class UpdateWithdrawalRequestUsersBatchIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/batch/update-withdrawal-request-users";
    private static final String BATCH_NAME = "UPDATE_WITHDRAWAL_REQUEST_USERS";

    private List<MockedStatic<?>> statics;

    private Map<String, Integer> request;
    private User user1;
    private User user2;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(DateTimeUtil.class));
        itUtil.init();

        user1 = mockWithdrawalRequestedUser();
        user2 = mockWithdrawalRequestedUser();
        mockWithdrawalRequestedUser();

        given(DateTimeUtil.isBeforeInDays(eq(user1.getUpdatedAt()), anyInt())).willReturn(true);
        given(DateTimeUtil.isBeforeInDays(eq(user2.getUpdatedAt()), anyInt())).willReturn(true);

        final int batchPeriod = 1;
        request = Map.of("batchPeriod", batchPeriod);

        String date = someString(5);
        given(DateTimeUtil.getCurrentFormattedDateTime()).willReturn(date);
        given(DateTimeUtil.getCurrentDateTimeMinusHours(batchPeriod)).willReturn(LocalDateTime.now());
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldWithdrawUsersWhoRequestedBeforeOneDay() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(batchXApiKey())
            )
            .andExpect(status().isOk())
            .andReturn();

        final ag.act.model.SimpleStringResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.SimpleStringResponse.class
        );

        assertResponse(result);
        assertResponse(itUtil.findUser(user1.getId()));
        assertResponse(itUtil.findUser(user2.getId()));
    }

    private void assertResponse(ag.act.model.SimpleStringResponse result) {
        final String expectedResult = "[Batch] %s batch successfully finished. [deletion: %s / %s]".formatted(BATCH_NAME, 2, 2);

        assertThat(result.getStatus(), is(expectedResult));

        final BatchLog batchLog = itUtil.findLastBatchLog(BATCH_NAME).orElseThrow(() -> new RuntimeException("[TEST] BatchLog를 찾을 수 없습니다."));
        assertThat(batchLog.getResult(), is(expectedResult));
    }

    private void assertResponse(User userFromDatabase) {
        assertThat(userFromDatabase.getStatus(), is(Status.DELETED_BY_USER));
        assertThat(userFromDatabase.getDeletedAt(), notNullValue());
        assertThat(userFromDatabase.getRoles(), is(empty()));
        assertThat(userFromDatabase.getUserHoldingStocks(), is(empty()));

        assertThat(itUtil.findMyDataSummaryByUserId(userFromDatabase.getId()).isEmpty(), is(true));
        assertThat(itUtil.findAllNicknameHistoriesByUserId(userFromDatabase.getId()).isEmpty(), is(true));
        assertThat(itUtil.findAllDigitalDocumentUsersByUserId(userFromDatabase.getId()).isEmpty(), is(true));
    }

    private User mockWithdrawalRequestedUser() {
        User user = itUtil.createUser();
        itUtil.createUserRole(user, someEnum(RoleType.class));

        final Stock stock = itUtil.createStock();
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);

        user.setStatus(Status.WITHDRAWAL_REQUESTED);
        return itUtil.updateUser(user);
    }
}
