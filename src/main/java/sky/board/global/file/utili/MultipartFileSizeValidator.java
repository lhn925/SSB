package sky.board.global.file.utili;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import sky.board.global.annotation.MultipartFileSizeValid;

public class MultipartFileSizeValidator implements ConstraintValidator<MultipartFileSizeValid, MultipartFile > {

    private static final String ERROR_MESSAGE = "{error.fileSize.Limit}";

    private static final long FILE_SIZE = 5242880L;

    @Override
    public void initialize(MultipartFileSizeValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        context.buildConstraintViolationWithTemplate(ERROR_MESSAGE).addConstraintViolation();
            boolean isSize = value.getSize() > FILE_SIZE;
            if (isSize) {
                return false;
            }
        return true;
    }


}