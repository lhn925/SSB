package sky.Sss.domain.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.dto.myInfo.UserMyInfoDto;
import sky.Sss.domain.user.dto.myInfo.UserNameUpdateDto;
import sky.Sss.domain.user.dto.myInfo.UserPwUpdateFormDto;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.openapi.service.ApiExamCaptchaNkeyService;
import sky.Sss.global.redis.dto.RedisKeyDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user/myInfo")
public class MyInfoController {


    private final UserQueryService userQueryService;
    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;

    /**
     * id:myInfo_1
     * 유저 마이 페이지로 이동
     * @param request
     * @param model
     * @return
     */
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


    /**
     * id:myInfo_2
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/pw")
    public String pwUpdateForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        // 유저 정보 조회
        userQueryService.findOne(session);

        Map<String, Object> apiExamCaptchaNkey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();
        String key = (String) apiExamCaptchaNkey.get("key");
        String apiExamCaptchaImage = apiExamCaptchaNkeyService.getApiExamCaptchaImage(key);
        // 유저 정보 반환
        UserPwUpdateFormDto userPwUpdateFormDto = UserPwUpdateFormDto.builder()
            .captchaKey(key)
            .imageName(apiExamCaptchaImage).build();
        model.addAttribute("userPwUpdateFormDto", userPwUpdateFormDto);
        return "user/myInfo/pwUpdateForm";

    }
}
