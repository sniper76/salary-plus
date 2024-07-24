package ag.act.api.election;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.Board;
import ag.act.entity.Poll;
import ag.act.entity.PollItem;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.SelectionOption;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionAnswerType;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails;
import ag.act.model.CreatePollAnswerItemRequest;
import ag.act.model.PollAnswerDataArrayResponse;
import ag.act.model.PollAnswerResponse;
import ag.act.model.PostPollAnswerRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;

class CreateElectionPollAnswerApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/solidarity-leader-elections/{solidarityLeaderElectionId}/answers";

    private PostPollAnswerRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private User voter;
    private User candidateUser1;
    private User candidateUser2;
    private User candidateUser3;
    private User candidateUser4;
    private Post post;
    private Poll poll;
    private UserHoldingStock voterHoldingStock;
    private SolidarityLeaderElection solidarityLeaderElection;
    private List<SolidarityLeaderApplicant> solidarityLeaderApplicants;
    private Long solidarityLeaderElectionId;
    private Solidarity solidarity;
    private final int indexOne = 0;
    private final int indexTwo = 1;
    private final int indexThree = 2;
    private final int indexFour = 3;
    private final int indexFive = 4;
    private final int indexSix = 5;
    private final int indexSeven = 6;
    private final int indexEight = 7;
    private List<PollItem> pollItemList;

    @BeforeEach
    void setUp() {
        itUtil.init();
        voter = itUtil.createUser();
        jwt = itUtil.createJwt(voter.getId());
        stock = itUtil.createStock();
        board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardCategory.SOLIDARITY_LEADER_ELECTION);
        voterHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), voter);

        final LocalDateTime localDateTime = LocalDateTime.now();
        solidarity = itUtil.createSolidarity(stock.getCode());

        createUserAndHoldingStock();

        createElectionAndPost(localDateTime);
    }

    private void createElectionAndPost(LocalDateTime localDateTime) {
        final LocalDateTime endDateTime = localDateTime.plusDays(5);

        solidarityLeaderElection = itUtil.createSolidarityElection(stock.getCode(), localDateTime);
        solidarityLeaderElectionId = solidarityLeaderElection.getId();

        solidarityLeaderApplicants = List.of(
            itUtil.createSolidarityLeaderApplicant(
                solidarity.getId(), candidateUser1.getId(), SolidarityLeaderElectionApplyStatus.COMPLETE, solidarityLeaderElectionId),
            itUtil.createSolidarityLeaderApplicant(
                solidarity.getId(), candidateUser2.getId(), SolidarityLeaderElectionApplyStatus.COMPLETE, solidarityLeaderElectionId),
            itUtil.createSolidarityLeaderApplicant(
                solidarity.getId(), candidateUser3.getId(), SolidarityLeaderElectionApplyStatus.COMPLETE, solidarityLeaderElectionId),
            itUtil.createSolidarityLeaderApplicant(
                solidarity.getId(), candidateUser4.getId(), SolidarityLeaderElectionApplyStatus.COMPLETE, solidarityLeaderElectionId)
        );

        final User superAdminUser = itUtil.createSuperAdminUser();
        post = itUtil.createPost(board, superAdminUser.getId());
        post = itUtil.createPoll(post, solidarityLeaderApplicants.size() * 2, SelectionOption.MULTIPLE_ITEMS);
        poll = post.getFirstPoll();
        pollItemList = poll.getPollItemList();

        poll.setTargetStartDate(localDateTime);
        poll.setTargetEndDate(endDateTime);
        itUtil.updatePost(post);

        for (int j = 0; j < pollItemList.size(); j++) {
            itUtil.createSolidarityLeaderElectionPollItemMapping(
                solidarityLeaderElectionId,
                solidarityLeaderApplicants.get(j / 2).getId(),
                pollItemList.get(j).getId(),
                (j % 2 == 0) ? SolidarityLeaderElectionAnswerType.APPROVAL : SolidarityLeaderElectionAnswerType.REJECTION
            );
        }
        solidarityLeaderElection.setVoteStartDateTime(localDateTime);
        solidarityLeaderElection.setVoteEndDateTime(endDateTime);
        solidarityLeaderElection.setPostId(post.getId());
        solidarityLeaderElection.setElectionStatusGroup(
            new SolidarityLeaderElectionStatusGroup(
                SolidarityLeaderElectionStatus.VOTE_PERIOD,
                SolidarityLeaderElectionStatusDetails.IN_PROGRESS)
        );
        solidarityLeaderElection = itUtil.updateSolidarityElection(solidarityLeaderElection);
    }


    private void createUserAndHoldingStock() {
        candidateUser1 = itUtil.createUser();
        candidateUser2 = itUtil.createUser();
        candidateUser3 = itUtil.createUser();
        candidateUser4 = itUtil.createUser();
    }

    private PostPollAnswerRequest genRequest(int approvalIndex, int rejectionIndex1, int rejectionIndex2, int rejectionIndex3) {
        CreatePollAnswerItemRequest answerItemRequestOne = new CreatePollAnswerItemRequest();
        CreatePollAnswerItemRequest answerItemRequestTwo = new CreatePollAnswerItemRequest();
        CreatePollAnswerItemRequest answerItemRequestThree = new CreatePollAnswerItemRequest();
        CreatePollAnswerItemRequest answerItemRequestFour = new CreatePollAnswerItemRequest();
        answerItemRequestOne.setPollItemId(poll.getPollItemList().get(approvalIndex).getId());
        answerItemRequestTwo.setPollItemId(poll.getPollItemList().get(rejectionIndex1).getId());
        answerItemRequestThree.setPollItemId(poll.getPollItemList().get(rejectionIndex2).getId());
        answerItemRequestFour.setPollItemId(poll.getPollItemList().get(rejectionIndex3).getId());

        List<CreatePollAnswerItemRequest> answerItemRequestList = new ArrayList<>();
        answerItemRequestList.add(answerItemRequestOne);
        answerItemRequestList.add(answerItemRequestTwo);
        answerItemRequestList.add(answerItemRequestThree);
        answerItemRequestList.add(answerItemRequestFour);

        PostPollAnswerRequest request = new PostPollAnswerRequest();
        request.pollAnswer(answerItemRequestList);
        return request;
    }

    @Nested
    class WhenSuccess {

        @Nested
        class WhenCreate {

            @BeforeEach
            void setUp() {
                request = genRequest(indexOne, indexFour, indexSix, indexEight);
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callPostApi(post(TARGET_API, stock.getCode(), solidarityLeaderElectionId), status().isOk());

                final PollAnswerDataArrayResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PollAnswerDataArrayResponse.class
                );

                assertResponse(result);
            }
        }

        @Nested
        class WhenCreateAllRejection {

            @BeforeEach
            void setUp() {
                request = genRequest(indexTwo, indexFour, indexSix, indexEight);
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callPostApi(post(TARGET_API, stock.getCode(), solidarityLeaderElectionId), status().isOk());

                final PollAnswerDataArrayResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PollAnswerDataArrayResponse.class
                );

                assertResponse(result);
            }
        }

        @Nested
        class WhenUpdate {

            @BeforeEach
            void setUp() {
                request = genRequest(indexTwo, indexThree, indexSix, indexEight);

                itUtil.createPollAnswer(
                    voter.getId(), poll.getId(), poll.getPollItemList().get(indexOne).getId(), voterHoldingStock.getQuantity());
                itUtil.createPollAnswer(
                    voter.getId(), poll.getId(), poll.getPollItemList().get(indexFour).getId(), voterHoldingStock.getQuantity());
                itUtil.createPollAnswer(
                    voter.getId(), poll.getId(), poll.getPollItemList().get(indexSix).getId(), voterHoldingStock.getQuantity());
                itUtil.createPollAnswer(
                    voter.getId(), poll.getId(), poll.getPollItemList().get(indexEight).getId(), voterHoldingStock.getQuantity());
            }

            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callPostApi(put(TARGET_API, stock.getCode(), solidarityLeaderElectionId), status().isOk());

                final PollAnswerDataArrayResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PollAnswerDataArrayResponse.class
                );

                assertResponse(result);
            }
        }
    }

    @NotNull
    private MvcResult callPostApi(MockHttpServletRequestBuilder targetApi, ResultMatcher resultMatcher) throws Exception {
        return mockMvc.perform(
                targetApi
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt))))
            .andExpect(resultMatcher)
            .andReturn();
    }

    @Nested
    class WhenError {

        @Nested
        class WhenAlreadyClosed {

            @BeforeEach
            void setUp() {
                request = genRequest(indexOne, indexThree, indexFive, indexSeven);
                solidarityLeaderElection.setElectionStatus(SolidarityLeaderElectionStatus.FINISHED);
                itUtil.updateSolidarityElection(solidarityLeaderElection);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callPostApi(post(TARGET_API, stock.getCode(), solidarityLeaderElectionId), status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "주주대표 선출이 이미 마감 되었습니다.");
            }
        }

        @Nested
        class WhenNotFoundElection {

            @BeforeEach
            void setUp() {
                request = genRequest(indexOne, indexThree, indexFive, indexSeven);
                solidarityLeaderElectionId = somePositiveLong();
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callPostApi(post(TARGET_API, stock.getCode(), solidarityLeaderElectionId), status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "주주대표 선출 정보를 찾을 수 없습니다.");
            }
        }

        @Nested
        class WhenNotMatchPollItems {

            @BeforeEach
            void setUp() {
                request = genNotMatchRequest();
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callPostApi(post(TARGET_API, stock.getCode(), solidarityLeaderElectionId), status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "주주대표 선출 투표 항목과 요청 항목이 일치하지 않습니다.");
            }

            private PostPollAnswerRequest genNotMatchRequest() {
                CreatePollAnswerItemRequest answerItemRequestOne = new CreatePollAnswerItemRequest();
                CreatePollAnswerItemRequest answerItemRequestTwo = new CreatePollAnswerItemRequest();
                CreatePollAnswerItemRequest answerItemRequestThree = new CreatePollAnswerItemRequest();
                CreatePollAnswerItemRequest answerItemRequestFour = new CreatePollAnswerItemRequest();
                answerItemRequestOne.setPollItemId(somePositiveLong());
                answerItemRequestTwo.setPollItemId(somePositiveLong());
                answerItemRequestThree.setPollItemId(somePositiveLong());
                answerItemRequestFour.setPollItemId(somePositiveLong());

                List<CreatePollAnswerItemRequest> answerItemRequestList = new ArrayList<>();
                answerItemRequestList.add(answerItemRequestOne);
                answerItemRequestList.add(answerItemRequestTwo);
                answerItemRequestList.add(answerItemRequestThree);
                answerItemRequestList.add(answerItemRequestFour);

                PostPollAnswerRequest request = new PostPollAnswerRequest();
                request.pollAnswer(answerItemRequestList);
                return request;
            }
        }

        @Nested
        class WhenBothPollItemApprovalAndRejection {

            @BeforeEach
            void setUp() {
                request = genRequest(indexOne, indexTwo, indexThree, indexFive);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callPostApi(post(TARGET_API, stock.getCode(), solidarityLeaderElectionId), status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "주주대표 선출 후보자에 찬성/반대를 동시에 투표할 수 없습니다.");
            }
        }

        @Nested
        class WhenPollItemMoreThanTwoApproval {

            @BeforeEach
            void setUp() {
                request = genRequest(indexOne, indexThree, indexSix, indexEight);
            }

            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callPostApi(post(TARGET_API, stock.getCode(), solidarityLeaderElectionId), status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "주주대표 선출 후보자에 찬성을 2개 이상 투표할 수 없습니다.");
            }
        }
    }

    private void assertResponse(PollAnswerDataArrayResponse result) {
        final List<PollAnswerResponse> responseList = result.getData();

        for (int k = 0; k < responseList.size(); k++) {
            PollAnswerResponse response = responseList.get(k);

            assertThat(response.getId(), is(notNullValue()));
            assertThat(response.getPollId(), is(poll.getId()));
            assertThat(response.getUserId(), is(voter.getId()));
            assertThat(response.getPollItemId(), is(request.getPollAnswer().get(k).getPollItemId()));
            assertThat(response.getStockQuantity(), is(voterHoldingStock.getQuantity()));
        }
    }
}
