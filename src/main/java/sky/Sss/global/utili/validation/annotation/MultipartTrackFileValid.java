package sky.Sss.global.utili.validation.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import sky.Sss.global.utili.validation.annotation.validator.MultipartTrackFileValidator;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = MultipartTrackFileValidator.class)
public @interface MultipartTrackFileValid {

    String message() default "{file.error.NotBlank}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
