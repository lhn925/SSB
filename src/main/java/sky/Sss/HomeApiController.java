package sky.Sss;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.global.redis.service.RedisService;

@RestController("/")
@Slf4j
@RequiredArgsConstructor
public class HomeApiController {


    private final RedisService redisService;



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
