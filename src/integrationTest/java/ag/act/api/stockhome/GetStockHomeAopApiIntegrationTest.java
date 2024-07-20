package ag.act.api.stockhome;

import ag.act.entity.LatestUserPostsView;
import ag.act.enums.PostsViewType;
import ag.act.repository.LatestUserPostsViewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetStockHomeAopApiIntegrationTest extends GetStockHomeApiIntegrationTest {
    @Autowired
    private LatestUserPostsViewRepository latestUserPostsViewRepository;
    private LocalDateTime createTimestamp;

    @BeforeEach
    void cleanDatabase() {
        latestUserPostsViewRepository.deleteAll();
    }

    @Test
    void shouldCreateLatestUserPostsView() throws Exception {
        firstCall();
        secondCall();
    }

    private void firstCall() throws Exception {
        callApi(status().isOk());

        List<LatestUserPostsView> latestUserPostsViews = latestUserPostsViewRepository.findAll();
        assertThat(latestUserPostsViews.size(), is(1));

        LatestUserPostsView latestUserPostsView = latestUserPostsViews.get(0);
        createTimestamp = latestUserPostsView.getTimestamp();
        assertLatestUserPostsView(latestUserPostsView);
    }

    private void secondCall() throws Exception {
        callApi(status().isOk());

        List<LatestUserPostsView> latestUserPostsViews = latestUserPostsViewRepository.findAll();
        assertThat(latestUserPostsViews.size(), is(1));

        LatestUserPostsView latestUserPostsView = latestUserPostsViews.get(0);
        LocalDateTime updateTimestamp = latestUserPostsView.getTimestamp();
        assertThat(updateTimestamp.isAfter(createTimestamp), is(true));
        assertLatestUserPostsView(latestUserPostsView);
    }

    private void assertLatestUserPostsView(LatestUserPostsView latestUserPostsView) {
        assertThat(latestUserPostsView.getBoardGroup(), nullValue());
        assertThat(latestUserPostsView.getBoardCategory(), nullValue());
        assertThat(latestUserPostsView.getPostsViewType(), is(PostsViewType.STOCK_HOME));
        assertThat(latestUserPostsView.getStock().getCode(), is(stock.getCode()));
        assertThat(latestUserPostsView.getUser().getId(), is(currentUser.getId()));
    }
}
