package ag.act.core.guard;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Repeatable(UseGuards.List.class)
@Documented
public @interface UseGuards {

    Class<? extends ActGuard>[] value() default {};

    @Target(ElementType.METHOD)
    @Retention(RUNTIME)
    @Documented
    @interface List {
        UseGuards[] value();
    }
}
