package sky.Sss.domain.track.dto.temp;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TempTrackDeleteDto  {
    @NotNull
    private Long id;
    @NotBlank
    private String token;


}
