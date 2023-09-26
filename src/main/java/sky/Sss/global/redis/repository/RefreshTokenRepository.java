package sky.Sss.global.redis.repository;


import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import sky.Sss.global.redis.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByAuthId(String authId);
}