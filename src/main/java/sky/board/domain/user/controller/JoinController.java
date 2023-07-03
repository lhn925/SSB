package sky.board.domain.user.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sky.board.domain.user.dto.UserJoinAgreeDto;
import sky.board.domain.user.dto.UserJoinPostDto;
import sky.board.domain.user.service.UserJoinService;


@Slf4j
@RequestMapping("/join")
@RequiredArgsConstructor
@Controller
public class JoinController {

    private final UserJoinService userJoinService;

    /**
     * 회원가입 페이지 이동 api
     * 토큰 쿠키 생성
     *
     * @param model
     * @return
     */
    @GetMapping
    public String joinForm(@Validated @ModelAttribute("userJoinAgreeDto") UserJoinAgreeDto userJoinAgreeDto,
        BindingResult bindingResult, Model model, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "redirect:/join/agree";
        }
        HttpSession session = request.getSession();


        // 쿠키 생성
        Cookie cookie = new Cookie("agreeToken", userJoinAgreeDto.getAgreeToken());
        cookie.setMaxAge(600); // 10분 유효
        cookie.setPath("/join");

        // 쿠키에 agreeToken 저장
        response.addCookie(cookie);

        // 약관 동의 데이터 session에 저장
        session.setAttribute(userJoinAgreeDto.getAgreeToken(), userJoinAgreeDto);

        model.addAttribute("userJoinPostDto", new UserJoinPostDto());
        return "user/join/joinForm";
    }

    /**
     *
     * 이용약관 동의 페이지 이동
     * 유효토큰 생성 api
     * @param model
     * @return
     */
    @GetMapping("/agree")
    public String joinAgreeForm(Model model) {

        //회원가입 토큰 발급
        UserJoinAgreeDto userJoinAgreeDto = UserJoinAgreeDto.createUserJoinAgree();
        model.addAttribute("userJoinAgreeDto", userJoinAgreeDto);
        return "user/join/joinAgreeForm";
    }

    /**
     * 회원가입 api
     *
     * @param userJoinDto
     * @param bindingResult
     * @return
     */
    @PostMapping
    public String join(@Validated @ModelAttribute UserJoinPostDto userJoinDto,
        BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            if (StringUtils.hasText(userJoinDto.getPassword())) { // 비밀번호 재전송
                model.addAttribute("rePw", userJoinDto.getPassword());
            }
            return "user/join/joinForm";
        }

        //cookie 기간 확인
        Optional<String> agreeToken = Arrays.stream(request.getCookies())
            .map(c -> c.getAttribute("agreeToken")).findFirst();

        // 동의 여부 token이 없을 경우 다시 동의 폼으로
        // 세션에 저장해둔 동의 여부 갖고오기
        HttpSession session = request.getSession();
        UserJoinAgreeDto userJoinAgreeDto = (UserJoinAgreeDto) session.getAttribute(agreeToken.orElse(null));
        if (userJoinAgreeDto == null) {
            return "redirect:/join/agree";
        }

        userJoinService.join(userJoinDto, userJoinAgreeDto);
        return "redirect:/";
    }
}
