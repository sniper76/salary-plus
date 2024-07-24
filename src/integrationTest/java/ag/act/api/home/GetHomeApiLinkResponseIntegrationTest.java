package ag.act.api.home;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.LinkResponse;
import ag.act.model.MySolidarityResponse;
import ag.act.util.KoreanDateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Random;

import static ag.act.TestUtil.someLocalDateTimeInTheFutureMinutesBetween;
import static ag.act.TestUtil.someLocalDateTimeInThePastMinutesBetween;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetHomeApiLinkResponseIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/home";
    private static final int ONE_DAY_BY_MINUTES = 60 * 24;
    private String jwt;
    private User user;
    private Stock stock;
    private Board board;
    private Post post;

    @BeforeEach
    void setUp() {
        itUtil.init();
        dbCleaner.clean();

        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock);
        post = itUtil.createPost(board, user.getId());

        itUtil.getHomeTestHelper()
            .mockUserHoldingStock(user, stock.getCode(), 0, ag.act.model.Status.ACTIVE);
    }

    private ag.act.model.HomeResponse getResponse(MvcResult response)
        throws JsonProcessingException, UnsupportedEncodingException {

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.HomeResponse.class
        );
    }

    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private void mockDigitalProxyInProgress() {
        final LocalDateTime targetStartDate = someLocalDateTimeInThePastMinutesBetween(10, ONE_DAY_BY_MINUTES);
        final LocalDateTime targetEndDate = someLocalDateTimeInTheFutureMinutesBetween(10, ONE_DAY_BY_MINUTES);

        itUtil.createDigitalProxy(post, targetStartDate, targetEndDate);
    }

    private void mockDigitalProxyNotInProgress() {
        final Random random = new Random();

        if (random.nextBoolean()) {
            mockDigitalProxyWithPastDateTimes();
        } else {
            mockDigitalProxyWithFutureDateTimes();
        }
    }

    private void mockDigitalProxyWithPastDateTimes() {
        post = itUtil.createPost(board, user.getId());
        final LocalDateTime targetStartDate = someLocalDateTimeInThePastMinutesBetween(20, ONE_DAY_BY_MINUTES);
        final LocalDateTime targetEndDate = targetStartDate.plusMinutes(10);

        itUtil.createDigitalProxy(post, targetStartDate, targetEndDate);
    }

    private void mockDigitalProxyWithFutureDateTimes() {
        final LocalDateTime targetStartDate = someLocalDateTimeInTheFutureMinutesBetween(20, ONE_DAY_BY_MINUTES);
        final LocalDateTime targetEndDate = targetStartDate.plusMinutes(10);

        itUtil.createDigitalProxy(post, targetStartDate, targetEndDate);
    }

    private void mockDigitalDocumentNotInProgress() {
        final Random random = new Random();

        if (random.nextBoolean()) {
            mockDigitalDocumentWithPastDateTimes();
        } else {
            mockDigitalDocumentWithFutureDateTimes();
        }
    }

    private void mockDigitalDocumentWithPastDateTimes() {
        post = itUtil.createPost(board, user.getId());
        final User acceptUser = itUtil.createUser();
        final LocalDateTime targetStartDate = someLocalDateTimeInThePastMinutesBetween(20, ONE_DAY_BY_MINUTES);
        final LocalDateTime targetEndDate = targetStartDate.plusMinutes(10);

        itUtil.createDigitalDocument(
            post, stock, acceptUser, DigitalDocumentType.ETC_DOCUMENT,
            targetStartDate, targetEndDate,
            KoreanDateTimeUtil.getTodayLocalDate()
        );
    }

    private void mockDigitalDocumentWithFutureDateTimes() {
        post = itUtil.createPost(board, user.getId());
        final User acceptUser = itUtil.createUser();
        final LocalDateTime targetStartDate = someLocalDateTimeInTheFutureMinutesBetween(10, ONE_DAY_BY_MINUTES);
        final LocalDateTime targetEndDate = targetStartDate.plusMinutes(10);

        itUtil.createDigitalDocument(
            post, stock, acceptUser, DigitalDocumentType.ETC_DOCUMENT,
            targetStartDate, targetEndDate,
            KoreanDateTimeUtil.getTodayLocalDate()
        );
    }

    private DigitalDocument mockDigitalDocumentInProgress() {
        post = itUtil.createPost(board, user.getId());
        final User acceptUser = itUtil.createUser();
        final LocalDateTime targetStartDate = someLocalDateTimeInThePastMinutesBetween(10, ONE_DAY_BY_MINUTES);
        final LocalDateTime targetEndDate = someLocalDateTimeInTheFutureMinutesBetween(10, ONE_DAY_BY_MINUTES);

        return itUtil.createDigitalDocument(
            post, stock, acceptUser, DigitalDocumentType.ETC_DOCUMENT,
            targetStartDate, targetEndDate,
            KoreanDateTimeUtil.getTodayLocalDate()
        );
    }

    private void mockDigitalDocumentUserCompleted(DigitalDocument digitalDocument) {
        DigitalDocumentUser digitalDocumentUser = itUtil.createDigitalDocumentUser(digitalDocument, user, stock);
        digitalDocumentUser.setDigitalDocumentAnswerStatus(DigitalDocumentAnswerStatus.COMPLETE);

        itUtil.updateDigitalDocumentUser(digitalDocumentUser);
    }

    private void mockDigitalDocumentUserNotCompleted(DigitalDocument digitalDocument) {
        DigitalDocumentUser digitalDocumentUser = itUtil.createDigitalDocumentUser(digitalDocument, user, stock);
        digitalDocumentUser.setDigitalDocumentAnswerStatus(DigitalDocumentAnswerStatus.SAVE);

        itUtil.updateDigitalDocumentUser(digitalDocumentUser);
    }

    private void assertLinkResponse(ag.act.model.HomeResponse result, String title, String url, String color) {
        assertThat(result.getMySolidarity().size(), is(1));

        MySolidarityResponse mySolidarityResponse = result.getMySolidarity().get(0);
        assertThat(mySolidarityResponse.getLinks().size(), is(1));

        LinkResponse linkResponse = mySolidarityResponse.getLinks().get(0);
        assertThat(linkResponse.getTitle(), is(title));
        assertThat(linkResponse.getUrl(), is(url));
        assertThat(linkResponse.getColor(), is(color));
    }

    @Nested
    class WhenDigitalProxyInProgress {
        @BeforeEach
        void setUp() {
            mockDigitalProxyInProgress();
        }

        @Test
        void shouldReturnActiveLinkResponse() throws Exception {
            final ag.act.model.HomeResponse result = getResponse(callApi());
            assertLinkResponse(result, "전자문서", "/stock/%s/action".formatted(stock.getCode()), "#355CE9");
        }
    }

    @Nested
    class WhenDigitalProxyNotInProgress {
        @BeforeEach
        void setUp() {
            mockDigitalProxyNotInProgress();
        }

        @Nested
        class AndTwoDigitalDocumentsAreNotInProgress {
            @BeforeEach
            void setUp() {
                mockDigitalDocumentNotInProgress();
                mockDigitalDocumentNotInProgress();
            }

            @Test
            void shouldNotReturnAnyLinkResponse() throws Exception {
                final ag.act.model.HomeResponse result = getResponse(callApi());
                assertThat(result.getMySolidarity().size(), is(1));
                assertThat(result.getMySolidarity().get(0).getLinks(), is(nullValue()));
            }
        }

        @Nested
        class AndTwoDigitalDocumentsAreInProgress {
            private DigitalDocument digitalDocument1;
            private DigitalDocument digitalDocument2;

            @BeforeEach
            void setUp() {
                digitalDocument1 = mockDigitalDocumentInProgress();
                digitalDocument2 = mockDigitalDocumentInProgress();

                mockAnotherUserCompletedDigitalDocument(digitalDocument1);
                mockAnotherUserCompletedDigitalDocument(digitalDocument2);
            }

            @Nested
            class AndOnlyOneDigitalDocumentUserExists {
                @BeforeEach
                void setUp() {
                    mockDigitalDocumentUserCompleted(digitalDocument1);
                }

                @Test
                void shouldReturnActiveLinkResponse() throws Exception {
                    final ag.act.model.HomeResponse result = getResponse(callApi());
                    assertLinkResponse(result, "전자문서", "/stock/%s/action".formatted(stock.getCode()), "#355CE9");
                }
            }

            @Nested
            class AndOneDigitalDocumentUserIsNotComplete {
                @BeforeEach
                void setUp() {
                    mockDigitalDocumentUserCompleted(digitalDocument1);
                    mockDigitalDocumentUserNotCompleted(digitalDocument2);
                }

                @Test
                void shouldReturnActiveLinkResponse() throws Exception {
                    final ag.act.model.HomeResponse result = getResponse(callApi());
                    assertLinkResponse(result, "전자문서", "/stock/%s/action".formatted(stock.getCode()), "#355CE9");
                }
            }

            @Nested
            class AndTwoDigitalDocumentUserIsAllComplete {
                @BeforeEach
                void setUp() {
                    mockDigitalDocumentUserCompleted(digitalDocument1);
                    mockDigitalDocumentUserCompleted(digitalDocument2);
                }

                @Test
                void shouldReturnInActiveCompletedLinkResponse() throws Exception {
                    final ag.act.model.HomeResponse result = getResponse(callApi());
                    assertLinkResponse(result, "전자문서(완료)", "/stock/%s/action".formatted(stock.getCode()), "#494b51");
                }
            }

            private void mockAnotherUserCompletedDigitalDocument(DigitalDocument digitalDocument) {
                User anotherUser = itUtil.createUser();
                DigitalDocumentUser digitalDocumentUser = itUtil.createDigitalDocumentUser(digitalDocument, anotherUser, stock);
                digitalDocumentUser.setDigitalDocumentAnswerStatus(DigitalDocumentAnswerStatus.COMPLETE);

                itUtil.updateDigitalDocumentUser(digitalDocumentUser);
            }
        }
    }
}
