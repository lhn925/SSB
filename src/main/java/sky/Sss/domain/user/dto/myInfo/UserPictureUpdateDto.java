package sky.Sss.domain.user.dto.myInfo;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.global.utili.annotation.MultipartPictureValid;

@Getter
@Setter
public class UserPictureUpdateDto {


    @MultipartPictureValid
//    @MultipartFileSizeValid
    private MultipartFile file;
}
