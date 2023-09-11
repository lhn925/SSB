package sky.board.domain.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.dto.myInfo.UserMyInfoDto;
import sky.board.domain.user.dto.myInfo.UserNameUpdateDto;
import sky.board.domain.user.dto.myInfo.UserPwUpdateFormDto;
import sky.board.domain.user.service.UserQueryService;
import sky.board.global.openapi.service.ApiExamCaptchaNkeyService;
import sky.board.global.redis.dto.RedisKeyDto;
import sky.board.global.utili.Alert;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user/myInfo")
public class MyInfoController {


    private final UserQueryService userQueryService;
    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;

    @GetMapping
    public String myPageForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
        // 유저 정보 조회
        userQueryService.findOne(session);
        // 유저 정보 반환
        model.addAttribute("userMyInfo", UserMyInfoDto.createUserMyInfo(userInfoDto));
        model.addAttribute("userNameUpdateDto", new UserNameUpdateDto());
        return "user/myInfo/myInfoForm";
    }


    @GetMapping("/pw")
    public String pwUpdateForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
        // 유저 정보 조회
        userQueryService.findOne(session);

        Map<String, Object> apiExamCaptchaNkey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
        String key = (String) apiExamCaptchaNkey.get("key");
        String apiExamCaptchaImage = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);
        // 유저 정보 반환
        UserPwUpdateFormDto userPwUpdateFormDto = UserPwUpdateFormDto.builder()
            .captchaKey(key)
            .imageName(apiExamCaptchaImage).build();

        model.addAttribute("userMyInfo", UserMyInfoDto.createUserMyInfo(userInfoDto));
        model.addAttribute("userPwUpdateFormDto", userPwUpdateFormDto);
        return "user/myInfo/pwUpdateForm";

    }


}
