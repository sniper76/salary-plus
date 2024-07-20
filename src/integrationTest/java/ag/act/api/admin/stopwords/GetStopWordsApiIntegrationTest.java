package ag.act.api.admin.stopwords;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.StopWord;
import ag.act.entity.User;
import ag.act.enums.admin.StopWordActivationType;
import ag.act.model.GetStopWordResponse;
import ag.act.model.Status;
import ag.act.model.StopWordResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class GetStopWordsApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stop-words";
    private static final Long TOTAL_ELEMENTS = 4L;

    private String jwt;
    private Map<String, Object> params;
    private Integer pageNumber;
    private StopWord stopWord1;
    private StopWord stopWord2;
    private StopWord stopWord3;
    private StopWord stopWord4;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.someAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        cleanExistingStopWords();

        stopWord1 = itUtil.createStopWord(someAlphanumericString(20));
        stopWord2 = itUtil.createStopWord(someAlphanumericString(20));
        stopWord3 = itUtil.createStopWord(someAlphanumericString(20), Status.INACTIVE_BY_ADMIN);
        stopWord4 = itUtil.createStopWord(someAlphanumericString(20), Status.INACTIVE_BY_ADMIN);
    }

    private void cleanExistingStopWords() {
        itUtil.findAllStopWords()
            .forEach(stopWord -> itUtil.updateStopWord(stopWord, Status.DELETED));
    }

    @Nested
    class WhenGetAllStopWords {

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "filterType", StopWordActivationType.ALL.name(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnStopWords() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetStopWordResponse result) {
                final List<StopWordResponse> stopWordResponses = result.getData();

                assertThat(stopWordResponses.size(), is(SIZE));
                assertPaging(result.getPaging(), TOTAL_ELEMENTS, getSortsOrDefault(params));
                assertStopWordResponse(
                    stopWordResponses.get(0), stopWord4);
                assertStopWordResponse(
                    stopWordResponses.get(1), stopWord3);
            }
        }

        @Nested
        class AndSecondPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_2;
                params = Map.of(
                    "filterType", StopWordActivationType.ALL.name(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnStopWords() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetStopWordResponse result) {
                final List<StopWordResponse> stopWordResponses = result.getData();

                assertThat(stopWordResponses.size(), is(SIZE));
                assertPaging(result.getPaging(), TOTAL_ELEMENTS, getSortsOrDefault(params));
                assertStopWordResponse(
                    stopWordResponses.get(0), stopWord2);
                assertStopWordResponse(
                    stopWordResponses.get(1), stopWord1);
            }
        }
    }

    @Nested
    class WhenGetActiveStopWords {

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "filterType", StopWordActivationType.ACTIVE.name(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnStopWords() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetStopWordResponse result) {
                final List<StopWordResponse> stopWordResponses = result.getData();

                assertThat(stopWordResponses.size(), is(SIZE));
                assertPaging(result.getPaging(), 2, getSortsOrDefault(params));
                assertStopWordResponse(
                    stopWordResponses.get(0), stopWord2);
                assertStopWordResponse(
                    stopWordResponses.get(1), stopWord1);
            }
        }
    }

    @Nested
    class WhenGetInActiveStopWords {

        @Nested
        class AndFirstPage {

            @BeforeEach
            void setUp() {
                pageNumber = PAGE_1;
                params = Map.of(
                    "filterType", StopWordActivationType.INACTIVE.name(),
                    "page", pageNumber.toString(),
                    "size", SIZE.toString()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnStopWords() throws Exception {
                assertResponse(callApiAndGetResult());
            }

            private void assertResponse(GetStopWordResponse result) {
                final List<StopWordResponse> stopWordResponses = result.getData();

                assertThat(stopWordResponses.size(), is(SIZE));
                assertPaging(result.getPaging(), 2, getSortsOrDefault(params));
                assertStopWordResponse(
                    stopWordResponses.get(0), stopWord4);
                assertStopWordResponse(
                    stopWordResponses.get(1), stopWord3);
            }
        }
    }

    @Nested
    class WhenSearchStopWordsByKeyword {

        @BeforeEach
        void setUp() {
            final String keyword = someAlphanumericString(3, 10);
            stopWord1.setWord(someAlphanumericString(1, 3) + keyword + someAlphanumericString(1, 3));
            itUtil.updateStopWord(stopWord1);
            stopWord2.setWord(keyword + someAlphanumericString(1, 3));
            itUtil.updateStopWord(stopWord2);

            pageNumber = PAGE_1;
            params = Map.of(
                "filterType", StopWordActivationType.ALL.name(),
                "searchKeyword", keyword,
                "page", pageNumber.toString(),
                "size", SIZE.toString()
            );
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnStopWords() throws Exception {
            assertResponse(callApiAndGetResult());
        }

        private void assertResponse(GetStopWordResponse result) {
            final List<StopWordResponse> stopWordResponses = result.getData();

            assertThat(stopWordResponses.size(), is(SIZE));
            assertPaging(result.getPaging(), 2L, getSortsOrDefault(params));
            assertStopWordResponse(
                stopWordResponses.get(0), stopWord2);
            assertStopWordResponse(
                stopWordResponses.get(1), stopWord1);
        }
    }


    private GetStopWordResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .params(toMultiValueMap(params))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            GetStopWordResponse.class
        );
    }

    private void assertStopWordResponse(
        StopWordResponse stopWordResponse,
        StopWord stopWord
    ) {
        assertThat(stopWordResponse.getId(), is(stopWord.getId()));
        assertThat(stopWordResponse.getWord(), is(stopWord.getWord()));
        assertThat(stopWordResponse.getStatus(), is(stopWord.getStatus()));
        assertTime(stopWordResponse.getCreatedAt(), stopWord.getCreatedAt());
        assertTime(stopWordResponse.getUpdatedAt(), stopWord.getCreatedAt());
    }

    private void assertPaging(ag.act.model.Paging paging, long totalElements, Object sorts) {
        assertThat(paging.getPage(), is(pageNumber));
        assertThat(paging.getTotalElements(), is(totalElements));
        assertThat(paging.getTotalPages(), is((int) Math.ceil(totalElements / (SIZE * 1.0))));
        assertThat(paging.getSize(), is(SIZE));
        assertThat(paging.getSorts().size(), is(1));
        assertThat(paging.getSorts(), is(sorts));
    }

    private Object getSortsOrDefault(Map<String, Object> params) {
        return params.getOrDefault("sorts", List.of(CREATED_AT_DESC));
    }
}
