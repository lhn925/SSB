package sky.board.domain.user.utill;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import sky.board.domain.user.annotation.JoinValid;

public class JoinValidator implements ConstraintValidator<JoinValid, String> {

    private String message;
    private String regexp;

    @Override
    public void initialize(JoinValid constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.regexp = constraintAnnotation.regexp();
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // 빈값 message NotBlank 로 변경 함
        if (value == null || !StringUtils.hasText(value.trim())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{NotBlank}")
                .addConstraintViolation();
            return false;
        }

        boolean isMatches = value.matches(this.regexp);
        return isMatches;
    }
}
