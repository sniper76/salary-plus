package ag.act.validator;

import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.exception.BadRequestException;
import ag.act.validator.document.CampaignValidator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.someBoardCategoryExcluding;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@MockitoSettings(strictness = Strictness.LENIENT)
class CampaignValidatorTest {

    @InjectMocks
    private CampaignValidator validator;
    @Mock
    private DefaultRequestValidator defaultRequestValidator;

    @Nested
    class Validate {
        @Mock
        private ag.act.model.CreateCampaignRequest createCampaignRequest;
        @Mock
        private ag.act.model.CreatePostRequest createPostRequest;

        @Nested
        class AndBoardGroupName {
            @Test
            void shouldThrowException() {
                // Given
                given(createCampaignRequest.getBoardGroupName()).willReturn(someString(10));
                given(createCampaignRequest.getStockGroupId()).willReturn(somePositiveLong());
                given(createCampaignRequest.getCreatePostRequest()).willReturn(createPostRequest);
                given(createPostRequest.getBoardCategory()).willReturn(someThing(
                    BoardCategory.SURVEYS,
                    BoardCategory.ETC
                ).name());

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(createCampaignRequest),
                    "존재하지 않는 게시판 그룹 이름입니다."
                );
            }
        }

        @Nested
        class AndBoardCategory {
            @Test
            void shouldThrowException() {
                // Given
                given(createCampaignRequest.getBoardGroupName()).willReturn(someEnum(BoardGroup.class).name());
                given(createCampaignRequest.getStockGroupId()).willReturn(somePositiveLong());
                given(createCampaignRequest.getCreatePostRequest()).willReturn(createPostRequest);
                given(createPostRequest.getBoardCategory()).willReturn(
                    someBoardCategoryExcluding(BoardCategory.SURVEYS, BoardCategory.ETC)
                        .name()
                );

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> validator.validate(createCampaignRequest),
                    "캠페인 등록은 설문과 10초서명만 가능합니다."
                );
            }
        }
    }
}
