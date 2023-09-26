package sky.Sss.global.redis.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;
/*
    @Id - 키(key) 값이 되며, refresh_token:{id} 위치에 auto-increment 된다.
    @RedisHash - 설정한 값을 Redis의 key 값 prefix로 사용한다.
    @Indexed - 값으로 검색을 할 시에 추가한다.
    @TimeToLive - 만료시간을 설정(초(second) 단위)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@RedisHash(value = "refresh_token")
public class RefreshToken {

    @Id
    private String authId;

    @Indexed
    private String token;

    private String role;

    @TimeToLive
    private long ttl;

    public RefreshToken update(String token, long ttl) {
        this.token = token;
        this.ttl = ttl;
        return this;
    }

}