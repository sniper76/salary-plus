package ag.act.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface PageableOverrider {
    int defaultSize() default 20;

    int MAX_SIZE = 100;

    String defaultSort() default "createdAt:ASC";

    String[] possibleSortNames() default {};
}