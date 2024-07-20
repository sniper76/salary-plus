package ag.act.api.stockboardgrouppost.digitaldocument;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.FileContent;
import ag.act.entity.PostImage;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.entity.digitaldocument.HolderListReadAndCopy;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.FileContentType;
import ag.act.enums.FileType;
import ag.act.enums.digitaldocument.HolderListReadAndCopyItemType;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.HolderListReadAndCopyDigitalDocumentResponse;
import ag.act.model.HolderListReadAndCopyItemRequest;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.Status;
import ag.act.util.DateTimeFormatUtil;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import jakarta.annotation.Nullable;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.io.InputStream;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ag.act.TestUtil.createMockMultipartFile;
import static ag.act.TestUtil.someEmail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaString;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class UserCreateDigitalDocumentHolderListReadAndCopyApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/holder-list-read-and-copy";
    private static final BoardGroup HOLDER_LIST_READ_AND_COPY_BOARD_GROUP = BoardGroup.ANALYSIS;
    private static final BoardCategory HOLDER_LIST_READ_AND_COPY_BOARD_CATEGORY = BoardCategory.HOLDER_LIST_READ_AND_COPY;
    private User currentUser;
    private Solidarity solidarity;
    private Stock stock;
    private String jwt;
    private static final Instant todayInstant = DateTimeUtil.getTodayInstant();
    private List<HolderListReadAndCopyItemRequest> holderListReadAndCopyItemRequests;
    private RequestData requestData;

    @BeforeEach
    void setUp() {
        itUtil.init();

        currentUser = itUtil.createUser();
        jwt = itUtil.createJwt(currentUser.getId());

        stock = itUtil.createStock();
        solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), currentUser);
        createHolderListReadAndCopyBoard();

        holderListReadAndCopyItemRequests = genHolderListReadAndCopyItems();
    }

    @Nested
    @DisplayName("유저가 주주대표일때")
    class WhenUserIsSolidarityLeader {

        @BeforeEach
        void setUp() {
            itUtil.createSolidarityLeader(solidarity, currentUser.getId());

            requestData = genRequest(holderListReadAndCopyItemRequests);
        }

        @Test
        @DisplayName("주주명부 열람/등사 게시글을 생성할 수 있다.")
        void shouldReturnSuccess() throws Exception {
            MvcResult mvcResult = callApi(status().isOk());

            assertResponse(mvcResult);
        }
    }

    @Nested
    @DisplayName("유저가 주주대표가 아닌 경우")
    class WhenUserIsNotSolidarityLeader {

        @BeforeEach
        void setUp() {
            requestData = genRequest(holderListReadAndCopyItemRequests);
        }

        @Test
        @DisplayName("주주명부 열람/등사 게시글을 생성할 수 없다.")
        void shouldReturnBadRequest() throws Exception {
            MvcResult mvcResult = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(
                mvcResult,
                400,
                "주주대표만 가능한 기능입니다."
            );
        }
    }

    @Nested
    @DisplayName("유저가 관리자인 경우")
    class WhenUserIsAdmin {

        @Nested
        @DisplayName("다른 유저가 주주대표인 경우")
        class WhenLeaderExist {
            @BeforeEach
            void setUp() {
                createSolidarityLeader();

                currentUser = itUtil.createAdminUser();

                jwt = itUtil.createJwt(currentUser.getId());

                requestData = genRequest(holderListReadAndCopyItemRequests);
            }

            private void createSolidarityLeader() {
                final User user = itUtil.createUser();
                itUtil.createSolidarityLeader(solidarity, user.getId());
                itUtil.createUserHoldingStock(stock.getCode(), user);
            }

            @Test
            @DisplayName("주주명부 열람/등사 게시글을 생성할 수 없다.")
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "주주대표만 가능한 기능입니다.");
            }
        }

        @Nested
        @DisplayName("같은 유저가 주주대표인 경우")
        class WhenUserIsLeaderAsWell {
            @BeforeEach
            void setUp() {
                currentUser = itUtil.createAdminUser();
                jwt = itUtil.createJwt(currentUser.getId());

                itUtil.createSolidarityLeader(solidarity, currentUser.getId());
                itUtil.createUserHoldingStock(stock.getCode(), currentUser);

                requestData = genRequest(holderListReadAndCopyItemRequests);
            }

            @Test
            @DisplayName("주주명부 열람/등사 게시글을 생성할 수 있다.")
            void shouldReturnSuccess() throws Exception {
                MvcResult mvcResult = callApi(status().isOk());

                assertResponse(mvcResult);
            }
        }
    }

    @Nested
    @DisplayName("주주명부 열람/등사 내용이 옳지 않을 때")
    class WhenHolderListReadAndCopyItemIsInvalid {

        private HolderListReadAndCopyItemType someItemType;

        @Nested
        class WhenSomeTypeIsNotExist {

            @BeforeEach
            void setUp() {
                itUtil.createSolidarityLeader(solidarity, currentUser.getId());

                someItemType = someEnum(HolderListReadAndCopyItemType.class);
                holderListReadAndCopyItemRequests.stream()
                    .filter(it -> someItemType.name().equals(it.getItemType()))
                    .findFirst()
                    .ifPresent(it -> holderListReadAndCopyItemRequests.remove(it));

                requestData = genRequest(holderListReadAndCopyItemRequests);
            }

            @Test
            void shouldReturnError() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "%s 항목을 입력해주세요.".formatted(someItemType.getTitle()));
            }
        }

        @Nested
        class WhenValueNotExist {

            @BeforeEach
            void setUp() {
                itUtil.createSolidarityLeader(solidarity, currentUser.getId());

                someItemType = someEnum(HolderListReadAndCopyItemType.class);
                holderListReadAndCopyItemRequests.stream()
                    .filter(it -> someItemType.name().equals(it.getItemType()))
                    .findFirst()
                    .ifPresent(it -> it.setItemValue(null));

                requestData = genRequest(holderListReadAndCopyItemRequests);
            }

            @Test
            void shouldReturnError() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "%s 항목을 입력해주세요.".formatted(someItemType.getTitle()));
            }
        }


        @Nested
        @DisplayName("이메일 형태가 맞지 않을 때")
        class WhenEmailInvalid {
            @BeforeEach
            void setUp() {
                itUtil.createSolidarityLeader(solidarity, currentUser.getId());

                holderListReadAndCopyItemRequests.stream()
                    .filter(it -> HolderListReadAndCopyItemType.LEADER_EMAIL.name().equals(it.getItemType()))
                    .findFirst()
                    .ifPresent(it -> it.setItemValue("invalid email " + someString(10)));

                requestData = genRequest(holderListReadAndCopyItemRequests);
            }

            @Test
            @DisplayName("주주명부 열람/등사 게시글을 생성할 수 없다.")
            void shouldReturnError() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "이메일 형식이 잘못되었습니다.");
            }
        }

        @Nested
        @DisplayName("발신인이 정하는 기한 형태가 맞지 않을 때")
        class WhenDeadLineDateIsInvalid {

            @BeforeEach
            void setUp() {
                itUtil.createSolidarityLeader(solidarity, currentUser.getId());

                holderListReadAndCopyItemRequests.stream()
                    .filter(it -> HolderListReadAndCopyItemType.DEADLINE_DATE_BY_LEADER1.name().equals(it.getItemType()))
                    .findFirst()
                    .ifPresent(it -> it.setItemValue("2024-01-01T" + someString(5)));

                requestData = genRequest(holderListReadAndCopyItemRequests);
            }

            @Test
            @DisplayName("주주명부 열람/등사 게시글을 생성할 수 없다.")
            void shouldReturnError() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "발신인이 정하는 기한 값이 잘못되었습니다.");
            }
        }

        @Nested
        @DisplayName("발신인이 정하는 기준일이 한글과 숫자가 아닐 때")
        class WhenReferenceDateIsInvalid {

            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of(someAlphaString(100)),
                    Arguments.of("2024-03-83T00:00:00Z"),
                    Arguments.of("2024-03-29Z"),
                    Arguments.of("4월말A"),
                    Arguments.of("12월-말")
                );
            }

            @BeforeEach
            void setUp() {
                itUtil.createSolidarityLeader(solidarity, currentUser.getId());
            }

            @ParameterizedTest(name = "{index} => invalidReferenceDateByLeader=''{0}''")
            @MethodSource("valueProvider")
            void shouldReturnError(String invalidReferenceDateByLeader) throws Exception {
                holderListReadAndCopyItemRequests.stream()
                    .filter(it -> HolderListReadAndCopyItemType.REFERENCE_DATE_BY_LEADER.name().equals(it.getItemType()))
                    .findFirst()
                    .ifPresent(it -> it.setItemValue(invalidReferenceDateByLeader));

                requestData = genRequest(holderListReadAndCopyItemRequests);

                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "주주명부 기준일은 한글과 숫자만 입력 가능합니다.");
            }
        }
    }

    @Nested
    @DisplayName("유저가 주주대표일때")
    class WhenSomeAttachmentsAreMissing {

        @Nested
        @DisplayName("서명 이미지가 없을 때")
        class WhenSignImageIsMissing {

            @BeforeEach
            void setUp() {
                itUtil.createSolidarityLeader(solidarity, currentUser.getId());

                requestData = genRequestWithNoSignImage(holderListReadAndCopyItemRequests);
            }

            @Test
            @DisplayName("주주명부 열람/등사 게시글을 생성할 수 없다.")
            void shouldReturnError() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "전자문서 필수 파일 정보가 없습니다. -서명 이미지");
            }
        }

        @Nested
        @DisplayName("신분증 이미지가 없을 때")
        class WhenIdCardImageIsMissing {

            @BeforeEach
            void setUp() {
                itUtil.createSolidarityLeader(solidarity, currentUser.getId());

                requestData = genRequestWithNoIdCardImage(holderListReadAndCopyItemRequests);
            }

            @Test
            @DisplayName("주주명부 열람/등사 게시글을 생성할 수 없다.")
            void shouldReturnError() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "전자문서 필수 파일 정보가 없습니다. -신분증 이미지");
            }
        }

        @Nested
        @DisplayName("일반잔고증명서 그리고 헥토잔고증명서 모두 없을때")
        class WhenBankAccountImagesAndHectoEncryptedBankAccountAreMissing {

            @BeforeEach
            void setUp() {
                itUtil.createSolidarityLeader(solidarity, currentUser.getId());

                requestData = genRequestWithNoBankAccountImagesAndHectoEncryptedBankAccount(holderListReadAndCopyItemRequests);
            }

            @Test
            @DisplayName("주주명부 열람/등사 게시글을 생성할 수 없다.")
            void shouldReturnError() throws Exception {
                MvcResult mvcResult = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(mvcResult, 400, "전자문서 필수 파일 정보가 없습니다. -잔고증명서");
            }
        }
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        final MockMultipartHttpServletRequestBuilder requestBuilder = multipart(
            TARGET_API,
            stock.getCode(),
            HOLDER_LIST_READ_AND_COPY_BOARD_GROUP.name()
        );

        if (requestData.signImage != null) {
            requestBuilder.file(requestData.signImage);
        }
        if (requestData.idCardImage != null) {
            requestBuilder.file(requestData.idCardImage);
        }
        if (CollectionUtils.isNotEmpty(requestData.bankAccountImages)) {
            requestData.bankAccountImages.forEach(requestBuilder::file);
        }
        if (requestData.hectoEncryptedBankAccountPdf != null) {
            requestBuilder.file(requestData.hectoEncryptedBankAccountPdf);
        }

        return mockMvc.perform(
                requestBuilder
                    .param("createPostRequest", generateCreatePostRequestString(requestData))
                    .header(AUTHORIZATION, "Bearer " + jwt)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private RequestData genRequest(List<HolderListReadAndCopyItemRequest> holderListReadAndCopyItemRequests) {

        final Integer randomNumber = someIntegerBetween(0, 1);
        return genRequestWithAllParameters(
            holderListReadAndCopyItemRequests,
            createMockMultipartFile("signImage"),
            createMockMultipartFile("idCardImage"),
            randomNumber == 0
                ? createMockMultipartFileList("bankAccountImages")
                : List.of(),
            randomNumber == 1
                ? getHectoEncryptedStringFile()
                : null
        );
    }

    private RequestData genRequestWithNoSignImage(
        List<HolderListReadAndCopyItemRequest> holderListReadAndCopyItemRequests
    ) {
        return genRequestWithAllParameters(
            holderListReadAndCopyItemRequests,
            null,
            createMockMultipartFile("idCardImage"),
            createMockMultipartFileList("bankAccountImages"),
            getHectoEncryptedStringFile()
        );
    }

    private RequestData genRequestWithNoIdCardImage(
        List<HolderListReadAndCopyItemRequest> holderListReadAndCopyItemRequests
    ) {
        return genRequestWithAllParameters(
            holderListReadAndCopyItemRequests,
            createMockMultipartFile("signImage"),
            null,
            createMockMultipartFileList("bankAccountImages"),
            getHectoEncryptedStringFile()
        );
    }

    private RequestData genRequestWithNoBankAccountImagesAndHectoEncryptedBankAccount(
        List<HolderListReadAndCopyItemRequest> holderListReadAndCopyItemRequests
    ) {
        return genRequestWithAllParameters(
            holderListReadAndCopyItemRequests,
            createMockMultipartFile("signImage"),
            createMockMultipartFile("idCardImage"),
            null,
            null
        );
    }

    private RequestData genRequestWithAllParameters(
        List<HolderListReadAndCopyItemRequest> holderListReadAndCopyItemRequests,
        MockMultipartFile signImage,
        MockMultipartFile idCardImage,
        List<MockMultipartFile> bankAccountImages,
        MockMultipartFile hectoEncryptedBankAccountPdf
    ) {
        final CreateDigitalDocumentRequest createDigitalDocumentRequest = new CreateDigitalDocumentRequest()
            .holderListReadAndCopyItems(holderListReadAndCopyItemRequests);

        return new RequestData(
            createDigitalDocumentRequest,
            signImage,
            idCardImage,
            bankAccountImages,
            hectoEncryptedBankAccountPdf
        );
    }

    @SuppressWarnings("SameParameterValue")
    @NotNull
    private List<MockMultipartFile> createMockMultipartFileList(String fileKey) {
        return List.of(
            createMockMultipartFile(fileKey),
            createMockMultipartFile(fileKey)
        );
    }

    private List<HolderListReadAndCopyItemRequest> genHolderListReadAndCopyItems() {
        return Arrays.stream(HolderListReadAndCopyItemType.values())
            .map(holderListReadAndCopyItemType -> {
                HolderListReadAndCopyItemRequest item = new HolderListReadAndCopyItemRequest();
                item.setItemType(holderListReadAndCopyItemType.name());

                switch (holderListReadAndCopyItemType) {
                    case DEADLINE_DATE_BY_LEADER1, DEADLINE_DATE_BY_LEADER2 -> item.setItemValue(todayInstant.toString());
                    case REFERENCE_DATE_BY_LEADER -> item.setItemValue(someReferenceDateByLeaderValue());
                    case LEADER_EMAIL -> item.setItemValue(someEmail());
                    default -> item.setItemValue(someAlphanumericString(5, 20));
                }
                return item;
            })
            .collect(Collectors.toList());
    }

    private String someReferenceDateByLeaderValue() {
        return someThing(
            "%s월말".formatted(someThing(3, 6, 9, 12).toString()),
            "%s년%s월%s일".formatted(
                someThing(2024, 2025),
                someIntegerBetween(1, 12),
                someIntegerBetween(1, 30)
            )
        );
    }

    private void assertResponse(MvcResult result) throws Exception {
        final PostDetailsDataResponse response = itUtil.getResult(result, PostDetailsDataResponse.class);

        PostDetailsResponse postDetailsResponse = response.getData();

        assertPostResponse(postDetailsResponse);
        assertDigitalDocument(postDetailsResponse);
        assertDigitalDocumentUser(postDetailsResponse);
        assertHolderListReadAndCopyItems(postDetailsResponse);
        assertPdfPath(postDetailsResponse);
        assertUploadFilesToS3();
    }

    private void assertPdfPath(PostDetailsResponse response) {
        final HolderListReadAndCopyDigitalDocumentResponse actual = response.getHolderListReadAndCopyDigitalDocument();
        final Long digitalDocumentId = actual.getDigitalDocumentId();
        final DigitalDocument digitalDocument = itUtil.findDigitalDocument(digitalDocumentId);
        final DigitalDocumentUser digitalDocumentUser = getFirstDigitalDocumentUser(digitalDocumentId);
        final String referenceDateByLeader = getReferenceDateByLeaderFromDatabase(digitalDocumentId);

        final String expectedPdfPath = "contents/digitaldocument/%s/source/%s_%s_주주명부열람등사_청구.pdf".formatted(
            digitalDocument.getId(),
            stock.getName(),
            referenceDateByLeader
        );

        assertThat(digitalDocumentUser.getPdfPath(), is(expectedPdfPath));
    }

    private DigitalDocumentUser getFirstDigitalDocumentUser(Long digitalDocumentId) {
        final List<DigitalDocumentUser> digitalDocumentUsers = itUtil.findAllDigitalDocumentUsersByDigitalDocumentId(digitalDocumentId);
        return digitalDocumentUsers.get(0);
    }

    @Nullable
    private String getReferenceDateByLeaderFromDatabase(Long digitalDocumentId) {
        final List<HolderListReadAndCopy> holderListReadAndCopyList =
            itUtil.findAllHolderListReadAndCopyByDigitalDocumentId(digitalDocumentId);
        return holderListReadAndCopyList
            .stream()
            .filter(it -> it.getItemType() == HolderListReadAndCopyItemType.REFERENCE_DATE_BY_LEADER)
            .findFirst()
            .map(HolderListReadAndCopy::getItemValue)
            .orElse(null);
    }

    private void assertUploadFilesToS3() {
        then(itUtil.getS3ServiceMockBean())
            .should(times(3))
            .putObject(any(), any(InputStream.class));
    }

    private void assertPostResponse(PostDetailsResponse response) {
        assertThat(response.getTitle(), is("%s 주주명부 열람/등사 청구 공문".formatted(stock.getName())));
        assertThat(response.getBoardGroup(), is(HOLDER_LIST_READ_AND_COPY_BOARD_GROUP.name()));
        assertThat(response.getStatus(), is(Status.ACTIVE));

        assertThat(response.getUserProfile(), is(notNullValue()));
        assertThat(response.getUserProfile().getNickname(), is(currentUser.getNickname()));

        HolderListReadAndCopyDigitalDocumentResponse holderListReadAndCopyDigitalDocument = response.getHolderListReadAndCopyDigitalDocument();
        final Long digitalDocumentId = holderListReadAndCopyDigitalDocument.getDigitalDocumentId();

        itUtil.findPostImagesByPostId(response.getId())
            .forEach(postImage -> {
                assertThat(postImage.getPostId(), is(response.getId()));

                final FileContent fileContent = getFileContent(postImage);
                final String expectedImageFilename = getExpectedImageFilename(digitalDocumentId);

                assertThat(fileContent.getOriginalFilename(), is(expectedImageFilename));
                assertThat(fileContent.getFileType(), is(FileType.IMAGE));
                assertThat(fileContent.getFileContentType(), is(FileContentType.DEFAULT));
            });
    }

    private FileContent getFileContent(PostImage postImage) {
        return itUtil.getFileContent(postImage.getImageId());
    }

    private String getExpectedImageFilename(Long digitalDocumentId) {
        return "%s_%s_%s.png".formatted(
            stock.getCode(),
            digitalDocumentId,
            DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT
        );
    }

    private void assertDigitalDocument(PostDetailsResponse response) {
        HolderListReadAndCopyDigitalDocumentResponse actual = response.getHolderListReadAndCopyDigitalDocument();
        DigitalDocument digitalDocument = itUtil.findDigitalDocument(actual.getDigitalDocumentId());

        assertThat(digitalDocument.getTitle(), is("%s 주주명부 열람/등사 청구 공문".formatted(stock.getName())));
        assertThat(digitalDocument.getType(), is(DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT));
    }

    private void assertDigitalDocumentUser(PostDetailsResponse response) {
        final Long digitalDocumentId = response.getHolderListReadAndCopyDigitalDocument().getDigitalDocumentId();
        final List<DigitalDocumentUser> digitalDocumentUsers = itUtil.findAllDigitalDocumentUsersByDigitalDocumentId(digitalDocumentId);

        assertThat(digitalDocumentUsers.size(), is(1));

        final DigitalDocumentUser digitalDocumentUser = digitalDocumentUsers.get(0);
        final SolidarityLeader leader = itUtil.findSolidarityLeader(stock.getCode()).orElseThrow();
        final User leaderUser = itUtil.findUser(leader.getUserId());

        assertThat(digitalDocumentUser.getUserId(), is(leader.getUserId()));
        assertThat(digitalDocumentUser.getDigitalDocumentAnswerStatus(), is(DigitalDocumentAnswerStatus.COMPLETE));
        assertThat(digitalDocumentUser.getStockName(), is(stock.getName()));
        assertThat(digitalDocumentUser.getStockCode(), is(stock.getCode()));
        assertThat(digitalDocumentUser.getName(), is(leaderUser.getName()));
    }

    private void assertHolderListReadAndCopyItems(PostDetailsResponse response) {
        final List<HolderListReadAndCopy> holderListReadAndCopyList =
            itUtil.findAllHolderListReadAndCopyByDigitalDocumentId(response.getHolderListReadAndCopyDigitalDocument().getDigitalDocumentId());

        assertThat(holderListReadAndCopyList.size(), is(HolderListReadAndCopyItemType.values().length));

        holderListReadAndCopyList.forEach(holderListReadAndCopy -> {
            switch (holderListReadAndCopy.getItemType()) {
                case DEADLINE_DATE_BY_LEADER1, DEADLINE_DATE_BY_LEADER2 -> assertDeadLineDateByLeader(holderListReadAndCopy);
                case REFERENCE_DATE_BY_LEADER -> assertReferenceDateByLeader(holderListReadAndCopy);
                default -> assertRestHolderListReadAndCopy(holderListReadAndCopy);
            }
        });
    }

    private void assertRestHolderListReadAndCopy(HolderListReadAndCopy holderListReadAndCopy) {
        final HolderListReadAndCopyItemRequest itemRequest = findHolderListReadAndCopyFromItemRequests(holderListReadAndCopy);

        assertThat(holderListReadAndCopy.getItemValue(), is(itemRequest.getItemValue().trim()));
    }

    private void assertReferenceDateByLeader(HolderListReadAndCopy holderListReadAndCopy) {
        final HolderListReadAndCopyItemRequest itemRequest = findHolderListReadAndCopyFromItemRequests(holderListReadAndCopy);

        assertThat(
            holderListReadAndCopy.getItemValue(),
            is(itemRequest.getItemValue().trim())
        );
    }

    private HolderListReadAndCopyItemRequest findHolderListReadAndCopyFromItemRequests(HolderListReadAndCopy holderListReadAndCopy) {
        return holderListReadAndCopyItemRequests.stream()
            .filter(itemRequest -> Objects.equals(itemRequest.getItemType(), holderListReadAndCopy.getItemType().name()))
            .findFirst()
            .orElseThrow();
    }

    private void assertDeadLineDateByLeader(HolderListReadAndCopy holderListReadAndCopy) {
        final String expectedItemValue = getExpectedDeadLineDateByLeaderValue();

        assertThat(holderListReadAndCopy.getItemValue(), is(expectedItemValue));
    }

    private String getExpectedDeadLineDateByLeaderValue() {
        final ZonedDateTime todayLocalDateTime = KoreanDateTimeUtil.toKoreanTime(todayInstant);
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatUtil.yyyy_MM_dd_HH_korean();
        return dateTimeFormatter.format(todayLocalDateTime);
    }

    private void createHolderListReadAndCopyBoard() {
        itUtil.createBoard(stock, HOLDER_LIST_READ_AND_COPY_BOARD_GROUP, HOLDER_LIST_READ_AND_COPY_BOARD_CATEGORY);
    }

    public String generateCreatePostRequestString(RequestData requestData) {
        return objectMapperUtil.toJson(requestData.createDigitalDocumentRequest);
    }

    record RequestData(
        CreateDigitalDocumentRequest createDigitalDocumentRequest,
        MockMultipartFile signImage,
        MockMultipartFile idCardImage,
        List<MockMultipartFile> bankAccountImages,
        MockMultipartFile hectoEncryptedBankAccountPdf
    ) {
    }
}
