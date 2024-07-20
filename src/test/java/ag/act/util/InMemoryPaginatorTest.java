package ag.act.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@MockitoSettings(strictness = Strictness.LENIENT)
class InMemoryPaginatorTest {
    private InMemoryPaginator paginator;

    @BeforeEach
    void setUp() {
        paginator = new InMemoryPaginator();
    }

    @Test
    void shouldPaginate() {

        // Given
        List<String> testData = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            testData.add("Item " + i);
        }
        PageRequest pageRequest = PageRequest.of(0, 5);

        // When
        Page<String> resultPage = paginator.paginate(testData, pageRequest);

        // Then
        assertThat(resultPage.getTotalElements(), is(10L));
        assertThat(resultPage.getTotalPages(), is(2));
        assertThat(resultPage.getSize(), is(5));
        assertThat(resultPage.getNumber(), is(0));
        assertThat(resultPage.getContent().size(), is(5));
        assertThat(resultPage.getContent().get(0), is("Item 1"));
        assertThat(resultPage.getContent().get(4), is("Item 5"));
    }

    @Test
    void shouldPaginateWithEmptyData() {
        // Given
        List<String> emptyTestData = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(0, 5);

        // When
        Page<String> resultPage = paginator.paginate(emptyTestData, pageRequest);

        // Then
        assertThat(resultPage.getTotalElements(), is(0L));
        assertThat(resultPage.getTotalPages(), is(0));
        assertThat(resultPage.getSize(), is(5));
        assertThat(resultPage.getNumber(), is(0));
        assertThat(resultPage.getContent().isEmpty(), is(true));
    }
}