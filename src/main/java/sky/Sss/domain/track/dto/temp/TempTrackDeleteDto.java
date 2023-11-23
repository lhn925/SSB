package sky.Sss.domain.track.dto.temp;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TempTrackDeleteDto  {
    @NotBlank
    private Long id;
    @NotBlank
    private String token;


}
