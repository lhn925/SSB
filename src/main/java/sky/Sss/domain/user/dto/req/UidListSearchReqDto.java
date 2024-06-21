package sky.Sss.domain.user.dto.req;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
//@Setter(AccessLevel.PRIVATE)
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UidListSearchReqDto {


//    @NotEmpty
    private List<Long> ids;
}
