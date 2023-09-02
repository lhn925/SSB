package sky.board.domain.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.dto.myInfo.UserMyInfoDto;
import sky.board.domain.user.dto.myInfo.UserNameUpdateDto;
import sky.board.domain.user.service.join.UserJoinService;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user/myInfo")
public class MyInfoController {

    @GetMapping
    public String myPageForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();

        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
        // 유저 정보 반환
        model.addAttribute("userMyInfo", UserMyInfoDto.createUserMyInfo(userInfoDto));
        return "user/myInfo/myInfoForm";
    }

}
