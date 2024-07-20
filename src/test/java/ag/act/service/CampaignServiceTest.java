package ag.act.service;

import ag.act.dto.admin.GetCampaignsSearchDto;
import ag.act.entity.Post;
import ag.act.repository.CampaignRepository;
import ag.act.service.digitaldocument.campaign.CampaignService;
import ag.act.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
public class CampaignServiceTest {
    @InjectMocks
    private CampaignService campaignService;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private PostService postService;

    @Nested
    class WhenGetCampaigns {
        @Mock
        private PageRequest pageRequest;
        @Mock
        private GetCampaignsSearchDto getCampaignsSearchDto;

        @BeforeEach
        void setUp() {
            given(getCampaignsSearchDto.getPageRequest()).willReturn(pageRequest);
        }

        @Nested
        class WhenKeywordBlank {
            @Test
            void shouldFindAll() {
                // Given
                given(getCampaignsSearchDto.isBlankSearchKeyword()).willReturn(true);

                // When
                campaignService.getCampaignPage(getCampaignsSearchDto);

                // Then
                then(campaignRepository).should().findAllByBoardCategory(null, pageRequest);
            }
        }
    }

    @Nested
    class WhenGetCampaignPosts {
        @Mock
        private Post sourcePost;

        @Test
        void shouldGetCampaignPosts() {
            // Given
            Long sourcePostId = someLong();
            List<Post> duplicatedPosts = List.of(new Post(), new Post());

            given(sourcePost.getId()).willReturn(sourcePostId);
            given(postService.getAllPostsBySourcePostId(sourcePostId)).willReturn(duplicatedPosts);

            // When
            List<Post> actual = campaignService.getCampaignPosts(sourcePost);

            // Then
            List<Post> expectedPosts = new ArrayList<>(duplicatedPosts);
            expectedPosts.add(sourcePost);
            assertThat(actual, containsInAnyOrder(expectedPosts.toArray()));
        }
    }
}
