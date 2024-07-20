package ag.act.api.admin.digitaldocument;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.admin.UserSearchType;
import ag.act.model.DigitalDocumentUserDetailsResponse;
import ag.act.model.Paging;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class GetDigitalDocumentUsersApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/digital-document/{digitalDocumentId}/users";

    private String jwt;
    private Map<String, Object> params;
    private Integer pageNumber;
    private Stock stock;
    private User acceptUser;
    private LocalDate referenceDate;
    private Long digitalDocumentId;
    private User user1;
    private User user2;
    private User user3;
    private DigitalDocumentUser digitalDocumentUser1;
    private DigitalDocumentUser digitalDocumentUser2;
    private DigitalDocumentUser digitalDocumentUser3;
    private String givenPartialUserName1;
    private String givenPartialUserName2;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        givenPartialUserName1 = someAlphanumericString(10);
        givenPartialUserName2 = someAlphanumericString(10);

        user1 = createUser("A_" + givenPartialUserName1);
        user2 = createUser("B_" + givenPartialUserName2);
        user3 = createUser("C_" + someAlphanumericString(10));

        stock = itUtil.createStock();
        acceptUser = createAcceptUserAsLeader();
        referenceDate = KoreanDateTimeUtil.getTodayLocalDate();

        final DigitalDocument digitalDocument = createDigitalDocument();
        digitalDocumentId = digitalDocument.getId();
        itUtil.createDigitalDocumentItemList(digitalDocument);

        digitalDocumentUser1 = createDigitalDocumentUserAndAnswer(user1, digitalDocument);
        digitalDocumentUser2 = createDigitalDocumentUserAndAnswer(user2, digitalDocument);
        digitalDocumentUser3 = createDigitalDocumentUserAndAnswer(user3, digitalDocument);

        itUtil.createMyDataSummary(user1, stock.getCode(), referenceDate);
        itUtil.createMyDataSummary(user2, stock.getCode(), referenceDate);
        itUtil.createMyDataSummary(user3, stock.getCode(), referenceDate);
    }

    private User createUser(String userName) {
        final User user = itUtil.createUserWithAddress();
        user.setName(userName);
        return itUtil.updateUser(user);
    }

    private User createAcceptUserAsLeader() {
        final User acceptUser = itUtil.createAcceptorUser();
        final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createSolidarityLeader(solidarity, acceptUser.getId());

        return acceptUser;
    }

    private DigitalDocumentUser createDigitalDocumentUserAndAnswer(User user, DigitalDocument document) {
        final String pdfPath = "%s/%s".formatted(user.getId(), someString(10));
        itUtil.createUserHoldingStock(stock.getCode(), user);
        return itUtil.createDigitalDocumentUser(document, user, stock, pdfPath, DigitalDocumentAnswerStatus.COMPLETE);
    }

    private DigitalDocument createDigitalDocument() {
        final User postWriteUser = itUtil.createAdminUser();
        final LocalDateTime targetStartDate = LocalDateTime.now().minusDays(3);
        final LocalDateTime targetEndDate = LocalDateTime.now().plusDays(1);

        final Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        final Post post = itUtil.createPost(board, postWriteUser.getId());
        final DigitalDocument digitalDocument = itUtil.createDigitalDocument(
            post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY, targetStartDate, targetEndDate, referenceDate
        );
        post.setDigitalDocument(digitalDocument);

        return digitalDocument;
    }

    @Nested
    class WhenSearchAllDigitalDocumentUsers {

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnDigitalDocumentUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(ag.act.model.DigitalDocumentUserDetailsDataResponse result) {
                final Paging paging = result.getPaging();
                final List<DigitalDocumentUserDetailsResponse> userDetailsResponses = result.getData();

                assertPaging(paging, 3L);
                assertThat(userDetailsResponses.size(), is(SIZE));
                assertUserResponse(user1, digitalDocumentUser1, userDetailsResponses.get(0));
                assertUserResponse(user2, digitalDocumentUser2, userDetailsResponses.get(1));
            }
        }

        @Nested
        class AndSecondPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = Map.of(
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnDigitalDocumentUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(ag.act.model.DigitalDocumentUserDetailsDataResponse result) {
                final Paging paging = result.getPaging();
                final List<DigitalDocumentUserDetailsResponse> userDetailsResponses = result.getData();

                assertPaging(paging, 3L);
                assertThat(userDetailsResponses.size(), is(1));
                assertUserResponse(user3, digitalDocumentUser3, userDetailsResponses.get(0));
            }
        }
    }

    @Nested
    class WhenSearchSpecificUserName {

        @Nested
        class AndFirstUser {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "searchType", UserSearchType.NAME,
                    "searchKeyword", givenPartialUserName1,
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnDigitalDocumentUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(ag.act.model.DigitalDocumentUserDetailsDataResponse result) {
                final Paging paging = result.getPaging();
                final List<DigitalDocumentUserDetailsResponse> userDetailsResponses = result.getData();

                assertPaging(paging, 1L);
                assertThat(userDetailsResponses.size(), is(1));
                assertUserResponse(user1, digitalDocumentUser1, userDetailsResponses.get(0));
            }
        }

        @Nested
        class AndSecondUser {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "searchType", UserSearchType.NAME,
                    "searchKeyword", givenPartialUserName2,
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnDigitalDocumentUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(ag.act.model.DigitalDocumentUserDetailsDataResponse result) {
                final Paging paging = result.getPaging();
                final List<DigitalDocumentUserDetailsResponse> userDetailsResponses = result.getData();

                assertPaging(paging, 1L);
                assertThat(userDetailsResponses.size(), is(1));
                assertUserResponse(user2, digitalDocumentUser2, userDetailsResponses.get(0));
            }
        }

        @Nested
        class AndNotFoundStockGroup {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "searchType", UserSearchType.NAME,
                    "searchKeyword", someNumericString(10),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnDigitalDocumentUsers() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(ag.act.model.DigitalDocumentUserDetailsDataResponse result) {
                final Paging paging = result.getPaging();
                final List<DigitalDocumentUserDetailsResponse> userDetailsResponses = result.getData();

                assertPaging(paging, 0L);
                assertThat(userDetailsResponses.size(), is(0));
            }
        }
    }

    private ag.act.model.DigitalDocumentUserDetailsDataResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API, digitalDocumentId)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return itUtil.getResult(response, ag.act.model.DigitalDocumentUserDetailsDataResponse.class);
    }

    private void assertUserResponse(User user, DigitalDocumentUser digitalDocumentUser, DigitalDocumentUserDetailsResponse userDetailsResponse) {
        assertThat(userDetailsResponse.getUserId(), is(user.getId()));
        assertThat(userDetailsResponse.getDigitalDocumentId(), is(digitalDocumentUser.getDigitalDocumentId()));
        assertThat(userDetailsResponse.getId(), is(digitalDocumentUser.getId()));
        assertThat(userDetailsResponse.getName(), is(digitalDocumentUser.getName()));
        assertThat(userDetailsResponse.getGender(), is(digitalDocumentUser.getGender().name()));
        assertThat(userDetailsResponse.getBirthDate(), is(DateTimeConverter.convert(digitalDocumentUser.getBirthDate())));
        assertThat(userDetailsResponse.getAddress(), is(digitalDocumentUser.getAddress()));
        assertThat(userDetailsResponse.getAddressDetail(), is(digitalDocumentUser.getAddressDetail()));
        assertThat(userDetailsResponse.getZipcode(), is(digitalDocumentUser.getZipcode()));
        assertThat(userDetailsResponse.getPhoneNumber(), is(itUtil.decrypt(digitalDocumentUser.getHashedPhoneNumber())));
        assertThat(userDetailsResponse.getIssuedNumber(), is(digitalDocumentUser.getIssuedNumber()));
    }

    private void assertPaging(Paging paging, long totalElements) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts().get(0), is("name:ASC"));
    }
}
