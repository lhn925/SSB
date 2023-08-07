package sky.board.global.openapi.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.board.global.openapi.service.ApiExamCaptchaNkeyService;

@RestController
@RequestMapping("/openApi")
@RequiredArgsConstructor
public class NaverApiController {


    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;


    /**
     * 키 발급
     */
    @GetMapping("/key")
    public ResponseEntity getApiExamCaptchaNkey() {

        return null;
    }




}
