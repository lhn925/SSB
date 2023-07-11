package sky.board.domain.user.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import sky.board.domain.user.utill.JoinValidator;


// 회원가입시 데이터 검증
// notBlank + pattern
@Target({FIELD}) // 변수 위에 사용하는 어노테이션이기 때문에 Target은 FIELD로 설정해줍니다.
@Retention(RUNTIME) // 유지범위 설정
@Constraint(validatedBy = JoinValidator.class)
@Documented
public @interface JoinValid {


    /**
     * @return the regular expression to match
     */
    String regexp();

    /**
     * @return the error message template
     */
    String message() default "{NotBlank}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
