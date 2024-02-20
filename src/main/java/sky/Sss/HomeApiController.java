package sky.Sss;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.global.redis.service.RedisQueryService;

@RestController("/")
@Slf4j
@RequiredArgsConstructor
public class HomeApiController {


    private final RedisQueryService redisQueryService;



    @GetMapping("/discover")
    public ResponseEntity<?> home() {

        /**
         * 최근 재생 트랙 및 앨범
         * Recently Played
         *
         * 나의 취향과 비슷한 트랙
         *
         *
         * 추천 트랙
         * Today's Mixes
         *
         */


        return null;
    }
}
