package sky.Sss.domain.track.dto.playlist.req;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayListSettingDeleteDto {
    @NotNull
    private Long id;
    @NotBlank
    private String token;
}
