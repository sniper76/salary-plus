package ag.act.api.stockboardgrouppost.digitaldocument;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalAnswerType;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ag.act.TestUtil.someFilename;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;

@SuppressWarnings("VariableDeclarationUsageDistance")
class UserCreateDigitalDocumentMultiThreadApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/digital-document/{digitalDocumentId}";
    private final LocalDate referenceDate = KoreanDateTimeUtil.getTodayLocalDate();
    private Stock stock;
    private Board board;
    private User acceptUser;
    private User postWriteUser;
    private Post post;
    private DigitalDocument digitalDocument;
    private List<DigitalDocumentItem> digitalDocumentItemList;

    @BeforeEach
    void setUp() {
        itUtil.init();
        postWriteUser = itUtil.createUser();
        acceptUser = itUtil.createUser();
        stock = itUtil.createStock();

        itUtil.createSolidarity(stock.getCode());
        itUtil.createStockAcceptorUser(stock.getCode(), acceptUser);
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        post = itUtil.createPost(board, postWriteUser.getId());

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime targetStartDate = now.minusDays(3);
        final LocalDateTime targetEndDate = now.plusDays(1);

        ag.act.model.JsonAttachOption jsonAttachOption = new ag.act.model.JsonAttachOption()
            .idCardImage(AttachOptionType.OPTIONAL.name())
            .signImage(AttachOptionType.OPTIONAL.name())
            .bankAccountImage(AttachOptionType.OPTIONAL.name())
            .hectoEncryptedBankAccountPdf(AttachOptionType.NONE.name());
        digitalDocument = itUtil.createDigitalDocument(
            post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY,
            targetStartDate, targetEndDate, referenceDate, jsonAttachOption
        );
        digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);
        post.setDigitalDocument(digitalDocument);
    }

    @Nested
    class WhenCreateDigitalDocumentUserData {
        private List<String> jwtList;

        @BeforeEach
        void setUp() {
            User user1 = itUtil.createUser();
            User user2 = itUtil.createUser();
            User user3 = itUtil.createUser();
            User user4 = itUtil.createUser();
            User user5 = itUtil.createUser();
            itUtil.createUserHoldingStock(stock.getCode(), user1);
            itUtil.createUserHoldingStock(stock.getCode(), user2);
            itUtil.createUserHoldingStock(stock.getCode(), user3);
            itUtil.createUserHoldingStock(stock.getCode(), user4);
            itUtil.createUserHoldingStock(stock.getCode(), user5);
            itUtil.createMyDataSummary(user1, stock.getCode(), referenceDate);
            itUtil.createMyDataSummary(user2, stock.getCode(), referenceDate);
            itUtil.createMyDataSummary(user3, stock.getCode(), referenceDate);
            itUtil.createMyDataSummary(user4, stock.getCode(), referenceDate);
            itUtil.createMyDataSummary(user5, stock.getCode(), referenceDate);
            jwtList = List.of(
                itUtil.createJwt(user1.getId()),
                itUtil.createJwt(user2.getId()),
                itUtil.createJwt(user3.getId()),
                itUtil.createJwt(user4.getId()),
                itUtil.createJwt(user5.getId())
            );
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldBeSuccess() {
            jwtList.forEach(jwt -> {
                try {
                    createUserDigitalDocument(jwt); // try 1
                    createUserDigitalDocument(jwt); // try 2 which will remove the try 1 and re-create try 2
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            final var digitalDocumentUsers = itUtil.findAllDigitalDocumentUsersByDigitalDocumentId(digitalDocument.getId());
            assertThat(digitalDocumentUsers.size(), is(jwtList.size()));

            final List<Long> issuedNumbers = digitalDocumentUsers.stream().map(DigitalDocumentUser::getIssuedNumber).sorted().toList();
            assertThat(issuedNumbers, is(List.of(2L, 4L, 6L, 8L, 10L)));
            assertThat(findDuplicates(issuedNumbers).size(), is(0));
        }

        private List<Long> findDuplicates(List<Long> list) {
            return list.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        }

        private void createUserDigitalDocument(String jwt) throws Exception {
            MockMultipartFile signImageFile = new MockMultipartFile(
                "signImage",
                someFilename(),
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            MockMultipartFile idCardImageFile = new MockMultipartFile(
                "idCardImage",
                someFilename(),
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            MockMultipartFile bankAccountFile1 = new MockMultipartFile(
                "bankAccountImages",
                someFilename(),
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            MockMultipartFile bankAccountFile2 = new MockMultipartFile(
                "bankAccountImages",
                someFilename(),
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            String answerData = genAnswerData();

            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .file(signImageFile)
                        .file(idCardImageFile)
                        .file(bankAccountFile1)
                        .file(bankAccountFile2)
                        .param("answerData", answerData)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.UserDigitalDocumentResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.UserDigitalDocumentResponse.class
            );

            assertThat(result.getId(), is(digitalDocument.getId()));
        }

        private String genAnswerData() {
            String answers = "";
            for (int k = 0; k < digitalDocumentItemList.size(); k++) {
                DigitalDocumentItem item = digitalDocumentItemList.get(k);
                if (item.getDefaultSelectValue() == null) {
                    continue;
                }
                if (k > 1) {
                    answers += ",";
                }
                answers += item.getId() + ":" + someEnum(DigitalAnswerType.class).name();
            }
            return answers;
        }
    }
}
