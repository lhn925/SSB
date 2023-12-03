package sky.Sss.global.openapi;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.NoSuchFileException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.user.model.LoginSuccess;
import sky.Sss.global.file.utili.FileStore;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.log.UserLoginLogService;
import sky.Sss.global.error.dto.ErrorResultDto;
import sky.Sss.global.error.dto.Result;
import sky.Sss.global.openapi.dto.CaptchaNkeyDto;
import sky.Sss.global.openapi.service.ApiExamCaptchaNkeyService;


@Slf4j
@RequestMapping("/Nkey/open")
@RestController
@RequiredArgsConstructor
public class NkeyOpenApiController {

    private final UserLoginLogService userLoginLogService;
    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;
    private final MessageSource ms;
    private final FileStore fileStore;

    @GetMapping("/{userId}")
    public ResponseEntity loginFailCheck(@PathVariable String userId) throws InterruptedException {
        // 실패 횟수 가져온 다음
        Long loginLogCount = userLoginLogService.getCount(userId, LoginSuccess.FAIL, Status.ON);

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
        return fileStore.getCaptchaUrlResource(fileName);
    }


    @GetMapping("/again")
    public ResponseEntity captchaKEyAgain(@Validated @ModelAttribute CaptchaNkeyDto captchaNkeyDto,
        BindingResult bindingResult
        , HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        String fileName = captchaNkeyDto.getImageName();
        //받은 이미지 삭제
        try {
            apiExamCaptchaNkeyService.deleteImage(fileName);
        } catch (NoSuchFileException e) {
            log.error("deleteAllBatch image error ", captchaNkeyDto.getImageName());
        }
        String key = (String) apiExamCaptchaNkeyService.getApiExamCaptchaNkey().get("key");
        String image = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);

        CaptchaNkeyDto captcha = CaptchaNkeyDto.builder()
            .captchaKey(key)
            .imageName(image).build();

        return new ResponseEntity(captcha, HttpStatus.OK);
    }

}
