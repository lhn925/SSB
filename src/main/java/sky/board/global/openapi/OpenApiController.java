package sky.board.global.openapi;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.PathDetails;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.service.UserLogService;
import sky.board.global.error.dto.Result;
import sky.board.global.openapi.dto.CaptchaNkeyDto;
import sky.board.global.openapi.service.ApiExamCaptchaNkeyService;


@Slf4j
@RequestMapping("/open")
@RestController
@RequiredArgsConstructor
public class OpenApiController {

    private final UserLogService userLogService;
    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;


    @GetMapping("/{userId}")
    public ResponseEntity loginFailCheck(@PathVariable String userId) throws InterruptedException {
        // 실패 횟수 가져온 다음
        Long loginLogCount = userLogService.getLoginLogCount(userId, LoginSuccess.FAIL, Status.ON);

        // 실패횟수가 5번 이하면 200
        if (loginLogCount < 5) {
            return new ResponseEntity(new Result<>(userId), HttpStatus.OK);
        }

        Map mapKey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
        String key = (String) mapKey.get("key");
        String image = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);

        CaptchaNkeyDto captcha = CaptchaNkeyDto.builder()
            .captchaKey(key)
            .imageName(image).build();

        return new ResponseEntity(new Result<>(captcha), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/image/{fileName}")
    public Resource getImage(@PathVariable String fileName) throws MalformedURLException {
        return new UrlResource("file:" + PathDetails.getFilePath(PathDetails.CAPTCHA_IMAGE_URL, fileName, "jpg"));
    }
}
