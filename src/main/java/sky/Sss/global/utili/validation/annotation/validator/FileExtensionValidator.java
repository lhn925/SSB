package sky.Sss.global.utili.validation.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.global.file.utili.FileUtils;
import sky.Sss.global.utili.validation.annotation.FileExtensionConstraint;


public class FileExtensionValidator implements ConstraintValidator<FileExtensionConstraint, MultipartFile> {
    private String type;
    private String message;
    @Override
    public void initialize(FileExtensionConstraint constraintAnnotation) {
        this.type = constraintAnnotation.type();
        this.message = constraintAnnotation.message();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        boolean isValid = true;
        if (value != null) {
            switch (this.type) {
                case "image":
                    isValid = FileUtils.validImgFile(value);
                    break;
                case "track":
                    isValid = FileUtils.validTrackFile(value);
                    break;
                default:
                    isValid = false;
            }
        }
        return isValid;
    }
}