package sky.Sss.global.file.utili;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.global.utili.annotation.MultipartPictureValid;


public class MultipartPictureValidator implements ConstraintValidator<MultipartPictureValid, MultipartFile> {
    private String message;
    @Override
    public void initialize(MultipartPictureValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value == null) {
            // 기본메세지 제거
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("{file.error.NotBlank}")
                .addConstraintViolation();
            return false;
        }
        return FileUtils.validImgFile(value);
    }
}