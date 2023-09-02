package sky.board.domain.user.dto.myInfo;


import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserPictureUpdateDto {
    @NotBlank
    private MultipartFile file;
}
