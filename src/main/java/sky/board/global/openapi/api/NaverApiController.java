package sky.board.global.openapi.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/openApi")
public class NaverApiController {

    /**
     * 키 발급
     */
    @GetMapping("/key")
    public ResponseEntity getApiExamCaptchaNkey() {

        return null;
    }




}
