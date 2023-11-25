package sky.Sss.global.utili.validation.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import sky.Sss.global.utili.validation.annotation.validator.MultipartFileSizeValidator;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = MultipartFileSizeValidator.class)
public @interface MultipartFileSizeValid {

    String message() default "{filesize.error.Limit}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}