package sky.Sss.global.utili.validation.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.global.file.utili.FileUtils;
import sky.Sss.global.utili.validation.annotation.MultipartTrackFileValid;


@Slf4j
public class MultipartTrackFileValidator implements ConstraintValidator<MultipartTrackFileValid, MultipartFile> {
    private static final String ERROR_MESSAGE = "track.ext.error";
    private static final long FILE_SIZE = 4294967296L;
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
        boolean isSize = value.getSize() <= FILE_SIZE;
        if (!isSize) {
            context.buildConstraintViolationWithTemplate("{trackSize.error.limit}").addConstraintViolation();
            return false;
        }
        boolean isValid = FileUtils.validTrackFile(value);
        if (!isValid) {
            context.buildConstraintViolationWithTemplate(ERROR_MESSAGE).addConstraintViolation();
        }
        return isValid;
    }
}