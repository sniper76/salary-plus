package ag.act.test.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ApproximatelyEqual<T extends Number> extends TypeSafeMatcher<T> {
    private final T expected;
    private final double epsilon;

    public ApproximatelyEqual(T expected, double epsilon) {
        this.expected = expected;
        this.epsilon = epsilon;
    }

    @Override
    protected boolean matchesSafely(T actual) {
        double actualValue = actual.doubleValue();
        double expectedValue = expected.doubleValue();
        return Math.abs(actualValue - expectedValue) <= epsilon;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a value approximately equal to ").appendValue(expected);
        description.appendText(" with tolerance ").appendValue(epsilon);
    }

    public static <T extends Number> ApproximatelyEqual<T> approximatelyEqual(T expected, double epsilon) {
        return new ApproximatelyEqual<>(expected, epsilon);
    }
}
