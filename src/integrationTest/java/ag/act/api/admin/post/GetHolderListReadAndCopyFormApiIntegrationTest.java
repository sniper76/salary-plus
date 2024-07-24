package ag.act.api.admin.post;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.model.HolderListReadAndCopyFormResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.someStockCode;
import static ag.act.enums.digitaldocument.HolderListReadAndCopyItemType.CEO_NAME;
import static ag.act.enums.digitaldocument.HolderListReadAndCopyItemType.COMPANY_ADDRESS;
import static ag.act.enums.digitaldocument.HolderListReadAndCopyItemType.COMPANY_NAME;
import static ag.act.enums.digitaldocument.HolderListReadAndCopyItemType.DEADLINE_DATE_BY_LEADER1;
import static ag.act.enums.digitaldocument.HolderListReadAndCopyItemType.DEADLINE_DATE_BY_LEADER2;
import static ag.act.enums.digitaldocument.HolderListReadAndCopyItemType.IR_PHONE_NUMBER;
import static ag.act.enums.digitaldocument.HolderListReadAndCopyItemType.LEADER_ADDRESS;
import static ag.act.enums.digitaldocument.HolderListReadAndCopyItemType.LEADER_EMAIL;
import static ag.act.enums.digitaldocument.HolderListReadAndCopyItemType.LEADER_NAME;
import static ag.act.enums.digitaldocument.HolderListReadAndCopyItemType.REFERENCE_DATE_BY_LEADER;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class GetHolderListReadAndCopyFormApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroup}/posts/holder-list-read-and-copy-form";

    private String stockCode;
    private String leaderJwt;
    private Board board;
    private User currentLeaderUser;
    private Stock stock;
    private Solidarity solidarity;
    private String userZipCode;
    private String userAddress;
    private String userAddressDetail;
    private boolean isUserHasAddress;

    @BeforeEach
    void setUp() {
        itUtil.init();
        currentLeaderUser = itUtil.createUser();
        leaderJwt = itUtil.createJwt(currentLeaderUser.getId());
        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.HOLDER_LIST_READ_AND_COPY);
        solidarity = itUtil.createSolidarity(stock.getCode());
    }

    @Nested
    class WhenSuccess {
        private String leaderAddress;

        @BeforeEach
        void setUp() {
            userZipCode = someAlphanumericString(5);
            userAddress = someAlphanumericString(10);
            userAddressDetail = someAlphanumericString(10);

            currentLeaderUser.setZipcode(userZipCode);
            currentLeaderUser.setAddress(userAddress);
            currentLeaderUser.setAddressDetail(userAddressDetail);

            itUtil.updateUser(currentLeaderUser);
            isUserHasAddress = Boolean.TRUE;

            itUtil.createSolidarityLeader(solidarity, currentLeaderUser.getId());
        }

        @Nested
        class WhenHasSpecialCharacter {

            @BeforeEach
            void setUp() {
                currentLeaderUser.setZipcode(" " + currentLeaderUser.getZipcode() + "'    '");
                currentLeaderUser.setAddress(" " + currentLeaderUser.getAddress() + "\"  '  \" ");
                currentLeaderUser.setAddressDetail(" " + currentLeaderUser.getAddressDetail() + "\n '  '  \"  ' ");
                itUtil.updateUser(currentLeaderUser);

                leaderAddress = userZipCode + " " + userAddress + " " + userAddressDetail;
            }

            @DisplayName("주주대표 주소에 특수문자가 있는 경우")
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk());

                final HolderListReadAndCopyFormResponse formResponse = itUtil.getResult(response, HolderListReadAndCopyFormResponse.class);

                assertResponse(formResponse);
            }
        }

        @Nested
        class WhenNormal {

            @BeforeEach
            void setUp() {
                leaderAddress = "%s %s %s".formatted(
                    currentLeaderUser.getZipcode(),
                    currentLeaderUser.getAddress(),
                    currentLeaderUser.getAddressDetail()
                );
            }

            @DisplayName("주주대표 주소에 특수문자가 없는 경우")
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk());

                final HolderListReadAndCopyFormResponse formResponse = itUtil.getResult(response, HolderListReadAndCopyFormResponse.class);

                assertResponse(formResponse);
            }

            @Nested
            @DisplayName("주소가 없는 경우")
            class WhenUserAddressNotExist {
                @BeforeEach
                void setUp() {
                    currentLeaderUser.setZipcode(someThing(null, "", " "));
                    currentLeaderUser.setAddress(someThing(null, "", " "));
                    currentLeaderUser.setAddressDetail(someThing(null, "", " "));
                    itUtil.updateUser(currentLeaderUser);

                    isUserHasAddress = Boolean.FALSE;
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callApi(status().isOk());

                    final HolderListReadAndCopyFormResponse formResponse = itUtil.getResult(response, HolderListReadAndCopyFormResponse.class);

                    assertResponse(formResponse);
                }
            }

            @Nested
            @DisplayName("주소의 상세주소만 없는 경우 제외하고 있는 주소만 제공한다.")
            class WhenUserAddressDetailNotExist {

                @BeforeEach
                void setUp() {
                    currentLeaderUser.setAddressDetail(someThing(null, "", " "));
                    itUtil.updateUser(currentLeaderUser);

                    leaderAddress = "%s %s".formatted(
                        currentLeaderUser.getZipcode(),
                        currentLeaderUser.getAddress()
                    );
                }

                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callApi(status().isOk());

                    final HolderListReadAndCopyFormResponse formResponse = itUtil.getResult(response, HolderListReadAndCopyFormResponse.class);

                    assertResponse(formResponse);
                }
            }
        }

        private void assertResponse(HolderListReadAndCopyFormResponse formResponse) {
            final String actualContent = formResponse.getContent();

            assertThat(actualContent, containsString(LEADER_NAME.name()));
            assertThat(actualContent, containsString(LEADER_NAME.getTitle()));
            assertThat(actualContent, containsString(LEADER_NAME.getHolderListReadAndCopyItemAnswerType().name()));
            assertThat(actualContent, containsString(LEADER_NAME.getDataType()));
            assertThat(actualContent, containsString("item-values='[\"" + currentLeaderUser.getName() + "\"]'"));

            assertThat(actualContent, containsString(COMPANY_NAME.name()));
            assertThat(actualContent, containsString(COMPANY_NAME.getTitle()));
            assertThat(actualContent, containsString(COMPANY_NAME.getHolderListReadAndCopyItemAnswerType().name()));
            assertThat(actualContent, containsString(COMPANY_NAME.getDataType()));
            assertThat(actualContent, containsString("item-values='[\"" + stock.getName() + "\"]'"));

            assertThat(actualContent, containsString(LEADER_ADDRESS.name()));
            assertThat(actualContent, containsString(LEADER_ADDRESS.getTitle()));
            assertThat(actualContent, containsString(LEADER_ADDRESS.getHolderListReadAndCopyItemAnswerType().name()));
            assertThat(actualContent, containsString(LEADER_ADDRESS.getDataType()));
            if (isUserHasAddress) {
                assertThat(actualContent, containsString("item-values='[\"" + leaderAddress + "\"]'"));
            } else {
                assertThat(actualContent, containsString("item-values='[]'"));
            }

            assertThat(actualContent, containsString(CEO_NAME.name()));
            assertThat(actualContent, containsString(CEO_NAME.getTitle()));
            assertThat(actualContent, containsString(CEO_NAME.getHolderListReadAndCopyItemAnswerType().name()));
            assertThat(actualContent, containsString(CEO_NAME.getDataType()));
            assertThat(actualContent, containsString("item-values='[]'"));

            //TODO 회사 주소
            assertThat(actualContent, containsString(COMPANY_ADDRESS.name()));
            assertThat(actualContent, containsString(COMPANY_ADDRESS.getTitle()));
            assertThat(actualContent, containsString(COMPANY_ADDRESS.getHolderListReadAndCopyItemAnswerType().name()));
            assertThat(actualContent, containsString(COMPANY_ADDRESS.getDataType()));
            assertThat(actualContent, containsString("item-values='[]'"));

            assertThat(actualContent, containsString(IR_PHONE_NUMBER.name()));
            assertThat(actualContent, containsString(IR_PHONE_NUMBER.getTitle()));
            assertThat(actualContent, containsString(IR_PHONE_NUMBER.getHolderListReadAndCopyItemAnswerType().name()));
            assertThat(actualContent, containsString(IR_PHONE_NUMBER.getDataType()));
            assertThat(actualContent, containsString("item-values='[\"" + stock.getRepresentativePhoneNumber() + "\"]'"));

            assertThat(DEADLINE_DATE_BY_LEADER1.getDataType(), is("DATETIME"));
            assertThat(actualContent, containsString(DEADLINE_DATE_BY_LEADER1.name()));
            assertThat(actualContent, containsString(DEADLINE_DATE_BY_LEADER1.getTitle()));
            assertThat(actualContent, containsString(DEADLINE_DATE_BY_LEADER1.getHolderListReadAndCopyItemAnswerType().name()));
            assertThat(actualContent, containsString(DEADLINE_DATE_BY_LEADER1.getDataType()));
            assertThat(actualContent, containsString("item-values='[]'"));

            assertThat(DEADLINE_DATE_BY_LEADER2.getDataType(), is("DATETIME"));
            assertThat(actualContent, containsString(DEADLINE_DATE_BY_LEADER2.name()));
            assertThat(actualContent, containsString(DEADLINE_DATE_BY_LEADER2.getTitle()));
            assertThat(actualContent, containsString(DEADLINE_DATE_BY_LEADER2.getHolderListReadAndCopyItemAnswerType().name()));
            assertThat(actualContent, containsString(DEADLINE_DATE_BY_LEADER2.getDataType()));
            assertThat(actualContent, containsString("item-values='[]'"));

            assertThat(REFERENCE_DATE_BY_LEADER.getDataType(), is("TEXT"));
            assertThat(actualContent, containsString(REFERENCE_DATE_BY_LEADER.name()));
            assertThat(actualContent, containsString(REFERENCE_DATE_BY_LEADER.getTitle()));
            assertThat(actualContent, containsString(REFERENCE_DATE_BY_LEADER.getHolderListReadAndCopyItemAnswerType().name()));
            assertThat(actualContent, containsString(REFERENCE_DATE_BY_LEADER.getDataType()));
            assertThat(actualContent, containsString("item-values='[\"3월말\",\"6월말\",\"9월말\",\"12월말\"]'"));

            assertThat(actualContent, containsString(LEADER_EMAIL.name()));
            assertThat(actualContent, containsString(LEADER_EMAIL.getTitle()));
            assertThat(actualContent, containsString(LEADER_EMAIL.getHolderListReadAndCopyItemAnswerType().name()));
            assertThat(actualContent, containsString(LEADER_EMAIL.getDataType()));
            assertThat(actualContent, containsString("item-values='[\"" + currentLeaderUser.getEmail() + "\"]'"));
        }
    }

    @Nested
    class WhenError {

        @DisplayName("주주대표가 아닌경우")
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "주주대표만 가능한 기능입니다.");
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stockCode, board.getGroup().name())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(leaderJwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
