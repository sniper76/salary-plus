package ag.act.enums.virtualboard;

import ag.act.enums.BoardGroup;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class VirtualBoardGroupTest {
    @Test
    void shouldNotHaveOverlappingNamesWithBoardGroup() {
        Stream<String> boardGroupNames = Arrays.stream(BoardGroup.values())
            .map(BoardGroup::name);

        boolean noOverlap = boardGroupNames
            .noneMatch(VirtualBoardGroup::isVirtualBoardGroupName);

        assertThat(noOverlap, is(true));
    }
}
