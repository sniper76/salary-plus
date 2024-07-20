package ag.act.api.admin.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DecryptColumnConverter;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.util.DateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

public class AdminUserWithStockCountCsvFileDownloadIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/users/csv-download";

    @Autowired
    private DecryptColumnConverter decryptColumnConverter;

    private String jwt;
    private User user1;
    private User user2;
    private User user3;
    private UserHoldingStock userHoldingStock1;
    private UserHoldingStock userHoldingStock2;
    private UserHoldingStock userHoldingStock3;
    private String stockCode;

    @BeforeEach
    void setUp() {
        itUtil.init();

        user1 = createUser("A" + someAlphanumericString(10));
        user2 = createUser("B" + someAlphanumericString(10));
        user3 = createUser("C" + someAlphanumericString(10));

        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
        Stock stock = itUtil.createStock();
        stockCode = stock.getCode();

        userHoldingStock1 = itUtil.createUserHoldingStock(stockCode, user1);
        userHoldingStock2 = itUtil.createUserHoldingStock(stockCode, user2);
        userHoldingStock3 = itUtil.createUserHoldingStock(stockCode, user3);
    }

    private User createUser(String userName) {
        User user = itUtil.createUserWithAddress();
        user.setName(userName);
        return itUtil.updateUser(user);
    }

    @DisplayName("Should return 200 response code when call " + TARGET_API)
    @Test
    void shouldDownloadAllData() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API, stockCode)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        final String actual = response.getResponse().getContentAsString();

        int rowNum = 0;
        final String expectedResult1 = getExpectedResult(++rowNum, user1, userHoldingStock1);
        final String expectedResult2 = getExpectedResult(++rowNum, user2, userHoldingStock2);
        final String expectedResult3 = getExpectedResult(++rowNum, user3, userHoldingStock3);

        final String expectedResult = """
            번호,이름,생년월일,주소,상세주소,우편번호,전화번호,주식수
            %s
            %s
            %s
            """.formatted(expectedResult1, expectedResult2, expectedResult3);

        assertThat(actual, is(expectedResult));
    }

    private String getExpectedResult(int rowNum, User user, UserHoldingStock userHoldingStock) {
        return String.join(",",
            List.of(
                String.valueOf(rowNum),
                user.getName(),
                DateTimeUtil.formatLocalDateTime(user.getBirthDate(), "yyyy/MM/dd"),
                user.getAddress().replaceAll(",", " "),
                user.getAddressDetail().replaceAll(",", " "),
                user.getZipcode(),
                decryptColumnConverter.convert(user.getHashedPhoneNumber()).replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3"),
                userHoldingStock.getQuantity().toString()
            ));
    }
}
