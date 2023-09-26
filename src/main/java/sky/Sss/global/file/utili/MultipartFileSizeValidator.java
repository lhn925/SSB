package sky.Sss.global.file.utili;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.global.annotation.MultipartFileSizeValid;

public class MultipartFileSizeValidator implements ConstraintValidator<MultipartFileSizeValid, MultipartFile> {

    private static final String ERROR_MESSAGE = "{fileSize.error.Limit}";

    private static final long FILE_SIZE = 5242880L;

    @Override
    public void initialize(MultipartFileSizeValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {

        if (value == null) {
            // 기본메세지 제거
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("file.error.NotBlank")
                .addConstraintViolation();
            return false;
        }
        context.buildConstraintViolationWithTemplate(ERROR_MESSAGE).addConstraintViolation();
        boolean isSize = value.getSize() > FILE_SIZE;
        if (isSize) {
            return false;
        }
        return true;
    }


}