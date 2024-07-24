package ag.act.api.stockboardgrouppost.digitaldocument;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.repository.interfaces.DigitalDocumentUserSummary;
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
import java.util.Optional;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings("VariableDeclarationUsageDistance")
class UserDeleteDigitalDocumentUserDataApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/digital-document/{digitalDocumentId}";

    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private User acceptUser;
    private User postWriteUser;
    private Post post;
    private DigitalDocument digitalDocument;
    private DigitalDocumentUser digitalDocumentUser;
    private List<DigitalDocumentItem> digitalDocumentItemList;

    @BeforeEach
    void setUp() {
        itUtil.init();
        postWriteUser = itUtil.createUser();
        user = itUtil.createUser();
        acceptUser = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();

        final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createSolidarityLeader(solidarity, acceptUser.getId());
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    @Nested
    class WhenDelete {
        private LocalDateTime targetStartDate;
        private LocalDateTime targetEndDate;
        private LocalDate referenceDate;

        @BeforeEach
        void setUp() {
            referenceDate = KoreanDateTimeUtil.getTodayLocalDate();

            final LocalDateTime now = LocalDateTime.now();
            targetStartDate = now.minusDays(3);
            targetEndDate = now.plusDays(1);

            final String pdfPath = someString(10);

            board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
            post = itUtil.createPost(board, postWriteUser.getId());
            digitalDocument = itUtil.createDigitalDocument(
                post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY,
                targetStartDate, targetEndDate, referenceDate
            );
            digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);
            digitalDocument.setDigitalDocumentItemList(digitalDocumentItemList);

            digitalDocumentUser = itUtil.createDigitalDocumentUser(digitalDocument, user, stock, pdfPath, DigitalDocumentAnswerStatus.COMPLETE);
            itUtil.createDigitalDocumentItemUserAnswerList(user.getId(), digitalDocumentItemList);

            post.setDigitalDocument(digitalDocument);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    delete(TARGET_API, digitalDocument.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.SimpleStringResponse.class
            );

            final Optional<DigitalDocumentUser> selectDigitalDocumentUser = itUtil.findDigitalDocumentUserById(digitalDocumentUser.getId());
            final Optional<DigitalDocument> selectDigitalDocument = itUtil.findByDigitalDocument(digitalDocument.getId());
            final DigitalDocumentUserSummary summary = itUtil.findDigitalDocumentSummary(digitalDocument.getId());

            assertThat(result.getStatus(), is("ok"));
            assertThat(selectDigitalDocumentUser.isPresent(), is(false));
            assertThat(
                selectDigitalDocument.get().getJoinStockSum(),
                is(summary.getSumOfStockCount())
            );
        }
    }
}
