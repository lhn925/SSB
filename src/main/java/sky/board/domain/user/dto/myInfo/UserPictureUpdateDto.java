package sky.board.domain.user.dto.myInfo;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import sky.board.global.annotation.MultipartFileSizeValid;

@Getter
@Setter
public class UserPictureUpdateDto {

    @MultipartFileSizeValid
    private MultipartFile file;
}
