package sky.Sss.domain.user.utili.jwt;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtDto {

    private final String accessToken;
    private final String refreshToken;

}
