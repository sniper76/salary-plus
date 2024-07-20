package ag.act.service;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.post.poll.PollAnswerResponseConverter;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.dto.poll.PollAnswersByTypeDto;
import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.Post;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.enums.BoardGroup;
import ag.act.enums.SelectionOption;
import ag.act.service.poll.PollAnswerService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostPollAnswerService;
import ag.act.service.user.UserHoldingStockService;
import ag.act.util.PollAnswerGenerator;
import ag.act.util.StatusUtil;
import ag.act.validator.post.StockBoardGroupPostValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.someBoardGroupForStock;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockBoardGroupPostPollAnswerServiceTest {
    @InjectMocks
    private StockBoardGroupPostPollAnswerService service;

    @Mock
    private PollAnswerService pollAnswerService;
    @Mock
    private StockBoardGroupPostValidator stockBoardGroupPostValidator;
    @Mock
    private PollAnswerResponseConverter pollAnswerResponseConverter;
    @Mock
    private PollAnswerGenerator pollAnswerGenerator;
    @Mock
    private UserHoldingStockService userHoldingStockService;
    @Mock
    private UserHoldingStock userHoldingStock;
    @Mock
    private GlobalBoardManager globalBoardManager;

    private List<MockedStatic<?>> statics;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class WhenCreatePollAnswer {
        @Mock
        private ag.act.model.PostPollAnswerRequest postPollAnswerRequest;
        @Mock
        private User user;
        @Mock
        private Post post;
        @Mock
        private Poll poll;
        @Mock
        private List<ag.act.model.PollAnswerResponse> pollAnswerResponseList;
        @Mock
        private List<ag.act.model.CreatePollAnswerItemRequest> answerItemRequestList;
        @Mock
        private List<PollAnswer> pollAnswerList;
        @Mock
        private PollAnswer pollAnswer;

        private String stockCode;
        private Long postId;
        private Long pollId;
        private BoardGroup boardGroup;

        @BeforeEach
        void setUp() {
            final Long userId = someLong();
            postId = someLong();
            pollId = someLong();
            stockCode = someString(6);
            boardGroup = someBoardGroupForStock();
            Long pollItemId = someLong();
            Long quantity = someLong();

            final SelectionOption selectionOption = someEnum(SelectionOption.class);

            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);

            given(postPollAnswerRequest.getPollAnswer()).willReturn(answerItemRequestList);
            given(post.getFirstPoll()).willReturn(poll);
            given(post.getFirstPoll().getSelectionOption()).willReturn(selectionOption);
            given(poll.getId()).willReturn(pollId);
            given(post.getPolls()).willReturn(List.of(poll));

            given(stockBoardGroupPostValidator.validateBoardGroupPost(
                postId, stockCode, boardGroup.name(), StatusUtil.getDeleteStatuses()
            )).willReturn(post);

            willDoNothing().given(stockBoardGroupPostValidator).validatePollBeforeAnswer(poll, pollId);
            willDoNothing().given(stockBoardGroupPostValidator).validatePollSingle(
                answerItemRequestList, selectionOption);

            given(userHoldingStockService.findUserHoldingStock(userId, stockCode))
                .willReturn(Optional.of(userHoldingStock));

            given(pollAnswerService.makePollAnswer(pollId, userId, pollItemId, quantity))
                .willReturn(pollAnswer);
            given(pollAnswerService.saveAll(List.of(pollAnswer))).willReturn(pollAnswerList);

            given(pollAnswerResponseConverter.convert(pollAnswerList))
                .willReturn(pollAnswerResponseList);
            given(globalBoardManager.isGlobalStockCode(stockCode)).willReturn(false);
        }

        @Test
        void shouldBeCreatePollAnswer() {
            ag.act.model.PollAnswerDataArrayResponse response = service.createBoardGroupPostPollAnswer(
                stockCode, boardGroup.name(), postId, pollId, postPollAnswerRequest
            );

            assertThat(response.getData().size(), is(pollAnswerResponseList.size()));
        }
    }

    @Nested
    class WhenUpdatePollAnswer {
        @Mock
        private ag.act.model.PostPollAnswerRequest postPollAnswerRequest;
        @Mock
        private User user;
        @Mock
        private Post post;
        @Mock
        private Poll poll;
        @Mock
        private List<ag.act.model.PollAnswerResponse> pollAnswerResponseList;
        @Mock
        private List<PollAnswer> pollAnswerList;

        private String stockCode;
        private Long postId;
        private Long pollId;
        private BoardGroup boardGroup;

        @BeforeEach
        void setUp() {
            postId = someLong();
            pollId = someLong();
            stockCode = someString(6);
            boardGroup = someBoardGroupForStock();
            final Long userId = someLong();
            final SelectionOption selectionOption = someEnum(SelectionOption.class);
            final List<ag.act.model.CreatePollAnswerItemRequest> answerItemRequestList = List.of();
            final List<PollAnswer> beforeItemList = List.of();
            final PollAnswersByTypeDto pollAnswersByTypeDto = new PollAnswersByTypeDto(List.of(), List.of(), List.of());

            given(ActUserProvider.getNoneNull()).willReturn(user);
            given(user.getId()).willReturn(userId);

            given(postPollAnswerRequest.getPollAnswer()).willReturn(answerItemRequestList);
            given(post.getFirstPoll()).willReturn(poll);
            given(post.getFirstPoll().getSelectionOption()).willReturn(selectionOption);
            given(poll.getId()).willReturn(pollId);
            given(post.getPolls()).willReturn(List.of(poll));

            given(stockBoardGroupPostValidator.validateBoardGroupPost(
                postId, stockCode, boardGroup.name(), StatusUtil.getDeleteStatuses()
            )).willReturn(post);

            willDoNothing().given(stockBoardGroupPostValidator).validatePollBeforeAnswer(poll, pollId);
            willDoNothing().given(stockBoardGroupPostValidator).validatePollSingle(
                answerItemRequestList, selectionOption);

            given(pollAnswerService.getAllByPollIdAndUserId(pollId, userId))
                .willReturn(beforeItemList);

            given(pollAnswerGenerator.getPollAnswersByTypeDto(answerItemRequestList, beforeItemList)).willReturn(pollAnswersByTypeDto);

            willDoNothing().given(pollAnswerService).deleteAll(pollAnswersByTypeDto.deletePollAnswers());

            given(userHoldingStockService.findUserHoldingStock(userId, stockCode))
                .willReturn(Optional.of(userHoldingStock));

            given(pollAnswerService.saveAll(pollAnswersByTypeDto.insertPollAnswers())).willReturn(pollAnswerList);

            given(pollAnswerResponseConverter.convert(pollAnswerList))
                .willReturn(pollAnswerResponseList);
            given(globalBoardManager.isGlobalStockCode(stockCode)).willReturn(false);
        }

        @Test
        void shouldBeUpdatePollAnswer() {
            ag.act.model.PollAnswerDataArrayResponse response = service.updateBoardGroupPostPollAnswer(
                stockCode, boardGroup.name(), postId, pollId, postPollAnswerRequest
            );

            assertThat(response.getData().size(), is(pollAnswerResponseList.size()));
        }
    }

}
