package sky.Sss.domain.track.dto.playlist;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.BasePlayListDto;

@Getter
@Setter
public class PlayListSettingDeleteDto {
    @NotNull
    private Long id;
    @NotBlank
    private String token;
}
