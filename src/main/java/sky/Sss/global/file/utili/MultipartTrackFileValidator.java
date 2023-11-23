package sky.Sss.global.file.utili;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.global.utili.annotation.MultipartTrackFileValid;


public class MultipartTrackFileValidator implements ConstraintValidator<MultipartTrackFileValid, MultipartFile> {
    private String message;
    @Override
    public void initialize(MultipartTrackFileValid constraintAnnotation) {
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
        return FileUtils.validTrackFile(value);
    }
}