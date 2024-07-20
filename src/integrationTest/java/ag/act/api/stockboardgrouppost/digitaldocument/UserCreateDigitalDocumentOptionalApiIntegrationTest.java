package ag.act.api.stockboardgrouppost.digitaldocument;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.dto.DigitalDocumentUserAnswerDto;
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
import ag.act.itutil.ITUtil;
import ag.act.util.DecimalFormatUtil;
import ag.act.util.KoreanDateTimeUtil;
import ag.act.util.ObjectMapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ag.act.TestUtil.someFilename;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;

class UserCreateDigitalDocumentOptionalApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/digital-document/{digitalDocumentId}";

    @Autowired
    private ITUtil itUtil;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapperUtil objectMapperUtil;

    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private User acceptUser;
    private User postWriteUser;
    private Post post;
    private DigitalDocument digitalDocument;
    private List<DigitalDocumentItem> digitalDocumentItemList;

    private final LocalDate referenceDate = KoreanDateTimeUtil.getTodayLocalDate();
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime targetStartDate = now.minusDays(3);
    private final LocalDateTime targetEndDate = now.plusDays(1);

    @BeforeEach
    void setUp() {
        itUtil.init();
        postWriteUser = itUtil.createUser();
        user = itUtil.createUser();
        acceptUser = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();

        itUtil.createSolidarity(stock.getCode());
        itUtil.createStockAcceptorUser(stock.getCode(), acceptUser);
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private Map<Long, String> genItemIdAndAnswerMap(String answerData) {
        return Arrays.stream(answerData.split(","))
            .map(item -> item.split(":"))
            .collect(Collectors.toMap(item -> Long.parseLong(item[0].trim()), item -> item[1].trim()));
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

    @Nested
    class WhenCreateDigitalDocumentOptionalAllFiles {
        private MockMultipartFile signImageFile;
        private MockMultipartFile idCardImageFile;
        private MockMultipartFile bankAccountFile1;
        private MockMultipartFile bankAccountFile2;
        private String answerData;
        private Map<Long, String> itemIdAndAnswerMap;

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
            post = itUtil.createPost(board, postWriteUser.getId());
            ag.act.model.JsonAttachOption jsonAttachOption = new ag.act.model.JsonAttachOption()
                .idCardImage(AttachOptionType.OPTIONAL.name())
                .signImage(AttachOptionType.OPTIONAL.name())
                .bankAccountImage(AttachOptionType.OPTIONAL.name())
                .hectoEncryptedBankAccountPdf(AttachOptionType.OPTIONAL.name());
            digitalDocument = itUtil.createDigitalDocument(
                post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY,
                targetStartDate, targetEndDate, referenceDate, jsonAttachOption
            );
            digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);
            post.setDigitalDocument(digitalDocument);

            signImageFile = new MockMultipartFile(
                "signImage",
                someFilename(),
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            idCardImageFile = new MockMultipartFile(
                "idCardImage",
                someFilename(),
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            bankAccountFile1 = new MockMultipartFile(
                "bankAccountImages",
                someFilename(),
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            bankAccountFile2 = new MockMultipartFile(
                "bankAccountImages",
                someFilename(),
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );

            answerData = genAnswerData();
            itemIdAndAnswerMap = genItemIdAndAnswerMap(answerData);

            // MyDataSummary는 유저가 전자문서 SAVE를 할때에는 필요없지만 PDF를 만들때는 필요하다.
            itUtil.createMyDataSummary(user, stock.getCode(), referenceDate);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldCreateUserDigitalDocumentTwice() throws Exception {
            Long digitalDocumentUserId1 = createUserDigitalDocument();
            Long digitalDocumentUserId2 = createUserDigitalDocument();

            // 두번 호출해도 기존 저장된 데이터를 지우고, 새롭게 생성이 되므로, 항상 같은 digitalDocumentUserId가 나와야 한다.
            assertThat(digitalDocumentUserId1, is(digitalDocumentUserId2));
        }

        private Long createUserDigitalDocument() throws Exception {
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
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.UserDigitalDocumentResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.UserDigitalDocumentResponse.class
            );

            final DigitalDocument digitalDocumentFromDatabase = itUtil.findDigitalDocument(digitalDocument.getId());

            final DigitalDocumentUser digitalDocumentUser =
                itUtil.findDigitalDocumentByDigitalDocumentIdAndUserId(digitalDocumentFromDatabase.getId(), user.getId())
                    .orElseThrow();

            assertThat(result.getId(), is(digitalDocumentFromDatabase.getId()));
            assertThat(result.getAnswerStatus(), is(digitalDocumentUser.getDigitalDocumentAnswerStatus().name()));
            assertThat(result.getJoinUserCount(), is(digitalDocumentFromDatabase.getJoinUserCount()));
            assertThat(result.getJoinStockSum(), is(digitalDocumentFromDatabase.getJoinStockSum()));
            assertThat(result.getShareholdingRatio(), is(getShareholdingRatio()));
            assertThat(result.getDigitalDocumentType(), is(digitalDocumentFromDatabase.getType().name()));
            assertThat(result.getUser().getId(), is(user.getId()));
            assertThat(result.getStock().getCode(), is(stock.getCode()));
            assertThat(result.getAcceptUser().getId(), is(acceptUser.getId()));
            assertThat(result.getAttachOptions(), is(notNullValue()));

            List<DigitalDocumentItem> lastItemList = digitalDocumentItemList.stream()
                .filter(element -> element.getDefaultSelectValue() != null)
                .toList();
            List<DigitalDocumentUserAnswerDto> answerDtoList = itUtil.findUserAnswerList(digitalDocumentFromDatabase.getId(), user.getId());

            assertThat(lastItemList.size(), is(answerDtoList.size()));

            answerDtoList.forEach(answerDto -> {
                final String expectedAnswer = itemIdAndAnswerMap.get(answerDto.getDigitalDocumentItemId());
                assertThat(answerDto.getUserAnswerType().name(), is(expectedAnswer));
            });

            return result.getId();
        }

        private float getShareholdingRatio() {
            final double ratio = digitalDocument.getJoinStockSum() * 100.0 / stock.getTotalIssuedQuantity();
            return ((Double) DecimalFormatUtil.twoDecimalPlaceDouble(ratio)).floatValue();
        }
    }

    @Nested
    class WhenCreateDigitalDocumentOptionalNoneFiles {
        private String answerData;
        private Map<Long, String> itemIdAndAnswerMap;

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
            post = itUtil.createPost(board, postWriteUser.getId());
            ag.act.model.JsonAttachOption jsonAttachOption = new ag.act.model.JsonAttachOption()
                .idCardImage(AttachOptionType.OPTIONAL.name())
                .signImage(AttachOptionType.OPTIONAL.name())
                .bankAccountImage(AttachOptionType.OPTIONAL.name())
                .hectoEncryptedBankAccountPdf(AttachOptionType.OPTIONAL.name());
            digitalDocument = itUtil.createDigitalDocument(
                post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY,
                targetStartDate, targetEndDate, referenceDate, jsonAttachOption
            );
            digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);
            post.setDigitalDocument(digitalDocument);

            answerData = genAnswerData();
            itemIdAndAnswerMap = genItemIdAndAnswerMap(answerData);

            // MyDataSummary는 유저가 전자문서 SAVE를 할때에는 필요없지만 PDF를 만들때는 필요하다.
            itUtil.createMyDataSummary(user, stock.getCode(), referenceDate);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldCreateUserDigitalDocumentTwice() throws Exception {
            Long digitalDocumentUserId1 = createUserDigitalDocument();
            Long digitalDocumentUserId2 = createUserDigitalDocument();

            // 두번 호출해도 기존 저장된 데이터를 지우고, 새롭게 생성이 되므로, 항상 같은 digitalDocumentUserId가 나와야 한다.
            assertThat(digitalDocumentUserId1, is(digitalDocumentUserId2));
        }

        private Long createUserDigitalDocument() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .param("answerData", answerData)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.UserDigitalDocumentResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.UserDigitalDocumentResponse.class
            );

            final DigitalDocument digitalDocumentFromDatabase = itUtil.findDigitalDocument(digitalDocument.getId());

            final DigitalDocumentUser digitalDocumentUser =
                itUtil.findDigitalDocumentByDigitalDocumentIdAndUserId(digitalDocumentFromDatabase.getId(), user.getId())
                    .orElseThrow();

            assertThat(result.getId(), is(digitalDocumentFromDatabase.getId()));
            assertThat(result.getAnswerStatus(), is(digitalDocumentUser.getDigitalDocumentAnswerStatus().name()));
            assertThat(result.getJoinUserCount(), is(digitalDocumentFromDatabase.getJoinUserCount()));
            assertThat(result.getJoinStockSum(), is(digitalDocumentFromDatabase.getJoinStockSum()));
            assertThat(result.getShareholdingRatio(), is(getShareholdingRatio()));
            assertThat(result.getDigitalDocumentType(), is(digitalDocumentFromDatabase.getType().name()));
            assertThat(result.getUser().getId(), is(user.getId()));
            assertThat(result.getStock().getCode(), is(stock.getCode()));
            assertThat(result.getAcceptUser().getId(), is(acceptUser.getId()));
            assertThat(result.getAttachOptions(), is(notNullValue()));

            List<DigitalDocumentItem> lastItemList = digitalDocumentItemList.stream()
                .filter(element -> element.getDefaultSelectValue() != null)
                .toList();
            List<DigitalDocumentUserAnswerDto> answerDtoList = itUtil.findUserAnswerList(digitalDocumentFromDatabase.getId(), user.getId());

            assertThat(lastItemList.size(), is(answerDtoList.size()));

            answerDtoList.forEach(answerDto -> {
                final String expectedAnswer = itemIdAndAnswerMap.get(answerDto.getDigitalDocumentItemId());
                assertThat(answerDto.getUserAnswerType().name(), is(expectedAnswer));
            });

            return result.getId();
        }

        private float getShareholdingRatio() {
            final double ratio = digitalDocument.getJoinStockSum() * 100.0 / stock.getTotalIssuedQuantity();
            return ((Double) DecimalFormatUtil.twoDecimalPlaceDouble(ratio)).floatValue();
        }
    }

    @Nested
    class WhenCreateDigitalDocumentButNoHectoEncryptedBankAccountPdfOptionInDB {
        private String answerData;
        private Map<Long, String> itemIdAndAnswerMap;

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
            post = itUtil.createPost(board, postWriteUser.getId());
            ag.act.model.JsonAttachOption jsonAttachOption = new ag.act.model.JsonAttachOption()
                .idCardImage(AttachOptionType.OPTIONAL.name())
                .signImage(AttachOptionType.OPTIONAL.name())
                .bankAccountImage(AttachOptionType.OPTIONAL.name());
            digitalDocument = itUtil.createDigitalDocument(
                post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY,
                targetStartDate, targetEndDate, referenceDate, jsonAttachOption
            );
            digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);
            post.setDigitalDocument(digitalDocument);

            answerData = genAnswerData();
            itemIdAndAnswerMap = genItemIdAndAnswerMap(answerData);

            // MyDataSummary는 유저가 전자문서 SAVE를 할때에는 필요없지만 PDF를 만들때는 필요하다.
            itUtil.createMyDataSummary(user, stock.getCode(), referenceDate);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldCreateUserDigitalDocumentTwice() throws Exception {
            Long digitalDocumentUserId1 = createUserDigitalDocument();
            Long digitalDocumentUserId2 = createUserDigitalDocument();

            // 두번 호출해도 기존 저장된 데이터를 지우고, 새롭게 생성이 되므로, 항상 같은 digitalDocumentUserId가 나와야 한다.
            assertThat(digitalDocumentUserId1, is(digitalDocumentUserId2));
        }

        private Long createUserDigitalDocument() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .param("answerData", answerData)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.UserDigitalDocumentResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.UserDigitalDocumentResponse.class
            );

            final DigitalDocument digitalDocumentFromDatabase = itUtil.findDigitalDocument(digitalDocument.getId());

            final DigitalDocumentUser digitalDocumentUser =
                itUtil.findDigitalDocumentByDigitalDocumentIdAndUserId(digitalDocumentFromDatabase.getId(), user.getId())
                    .orElseThrow();

            assertThat(result.getId(), is(digitalDocumentFromDatabase.getId()));
            assertThat(result.getAnswerStatus(), is(digitalDocumentUser.getDigitalDocumentAnswerStatus().name()));
            assertThat(result.getJoinUserCount(), is(digitalDocumentFromDatabase.getJoinUserCount()));
            assertThat(result.getJoinStockSum(), is(digitalDocumentFromDatabase.getJoinStockSum()));
            assertThat(result.getShareholdingRatio(), is(getShareholdingRatio()));
            assertThat(result.getDigitalDocumentType(), is(digitalDocumentFromDatabase.getType().name()));
            assertThat(result.getUser().getId(), is(user.getId()));
            assertThat(result.getStock().getCode(), is(stock.getCode()));
            assertThat(result.getAcceptUser().getId(), is(acceptUser.getId()));
            assertThat(result.getAttachOptions(), is(notNullValue()));

            List<DigitalDocumentItem> lastItemList = digitalDocumentItemList.stream()
                .filter(element -> element.getDefaultSelectValue() != null)
                .toList();
            List<DigitalDocumentUserAnswerDto> answerDtoList = itUtil.findUserAnswerList(digitalDocumentFromDatabase.getId(), user.getId());

            assertThat(lastItemList.size(), is(answerDtoList.size()));

            answerDtoList.forEach(answerDto -> {
                final String expectedAnswer = itemIdAndAnswerMap.get(answerDto.getDigitalDocumentItemId());
                assertThat(answerDto.getUserAnswerType().name(), is(expectedAnswer));
            });

            return result.getId();
        }

        private float getShareholdingRatio() {
            final double ratio = digitalDocument.getJoinStockSum() * 100.0 / stock.getTotalIssuedQuantity();
            return ((Double) DecimalFormatUtil.twoDecimalPlaceDouble(ratio)).floatValue();
        }
    }
}
