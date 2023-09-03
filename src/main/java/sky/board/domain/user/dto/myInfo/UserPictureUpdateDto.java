package sky.board.domain.user.dto.myInfo;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import sky.board.global.annotation.MultipartFileSizeValid;
import sky.board.global.annotation.MultipartPictureValid;

@Getter
@Setter
public class UserPictureUpdateDto {


    @MultipartPictureValid
    @MultipartFileSizeValid
    private MultipartFile file;
}
