package sky.Sss.global.redis.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RedisDataListDto <T>{

    private Map<String ,T> result;

    private Set<String> missingKeys;

    public RedisDataListDto(Map<String, T> result, Set<String> missingKeys) {
        this.result = result;
        this.missingKeys = missingKeys;
    }
}
