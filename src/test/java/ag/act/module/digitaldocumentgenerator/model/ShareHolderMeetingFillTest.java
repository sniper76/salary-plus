package ag.act.module.digitaldocumentgenerator.model;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class ShareHolderMeetingFillTest {
    @Test
    public void testGetRegularShareHolderMeetingExists() throws NoSuchMethodException {
        assertThat(
            ShareHolderMeetingFill.class.getDeclaredMethod("getRegularShareHolderMeeting"),
            notNullValue()
        );
    }
}

