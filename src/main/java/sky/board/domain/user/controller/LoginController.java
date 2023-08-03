package sky.board.domain.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sky.board.domain.user.dto.CustomUserDetails;
import sky.board.domain.user.dto.UserLoginReqDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

//https://nid.naver.com/nidlogin.login?mode=form&url=https://www.naver.com/


    private final MessageSource ms;

    /**
     * @param request
     * @return
     */
    @GetMapping
    public String loginForm(@ModelAttribute UserLoginReqDto userLoginReqDto,
        HttpServletRequest request) {

        String mode = userLoginReqDto.getMode();

        //Mode가 비어졌거나 form 이 아니면 로그인 실패 로직
        if (!StringUtils.hasText(mode) && mode.equals("form")) {

        }

        return "/user/login";
    }

    /**
     * 로그인 성공 후 호출되는 API
     * 0dksmf071
     * 0dlagksmf2
     *
     */


}
