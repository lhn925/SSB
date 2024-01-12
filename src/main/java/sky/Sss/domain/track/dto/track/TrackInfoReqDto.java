package sky.Sss.domain.track.dto.track;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TrackInfoReqDto {

    @NotNull
    private Long id;
    @NotBlank
    private String token;
}
