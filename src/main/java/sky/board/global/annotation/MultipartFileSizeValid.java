package sky.board.global.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import sky.board.global.file.utili.MultipartFileSizeValidator;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = MultipartFileSizeValidator.class)
public @interface MultipartFileSizeValid {

    String message() default "{error.fileSize.Limit}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}