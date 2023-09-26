package sky.Sss.domain.user.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sky.Sss.domain.email.dto.EmailAuthCodeDto;
import sky.Sss.domain.user.dto.join.UserJoinAgreeDto;
import sky.Sss.domain.user.dto.join.UserJoinPostDto;
import sky.Sss.domain.user.model.PwSecLevel;
import sky.Sss.domain.user.exception.DuplicateCheckException;
import sky.Sss.domain.user.service.join.UserJoinService;
import sky.Sss.domain.user.utili.CustomCookie;
import sky.Sss.domain.user.utili.PwChecker;
import sky.Sss.global.error.dto.FieldErrorCustom;


@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user/join")
public class JoinController {

    private final UserJoinService userJoinService;

    /**
     * id:join_1
     * 회원가입 페이지 이동 api
     *
     * @param userJoinAgreeDto
     * @param bindingResult
     * @param model
     * @param request
     * @return
     */
    @GetMapping
    public String joinForm(@Validated @ModelAttribute UserJoinAgreeDto userJoinAgreeDto,
        BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "redirect:/user/join/agree";
        }
        log.info("접근");
        HttpSession session = request.getSession();

        if (session.getAttribute("emailAuthCodeDto") != null) {// 인증번호 삭제 (뒤로가기 버그 방지)
            session.removeAttribute("emailAuthCodeDto");
        }

        String agreeToken = CustomCookie.readCookie(request.getCookies(), "agreeToken");

        // agreeToken 쿠키가 만료됐거나 , 쿠키에 저장된 토큰과 요청한 토큰이 안 맞을 경우
        if (!StringUtils.hasText(agreeToken) || (!agreeToken.equals(userJoinAgreeDto.getAgreeToken()))) {
            bindingResult.reject("code.error");
            return "redirect:/user/join/agree";
        }

        // 약관 동의 데이터 session에 저장
        session.setAttribute(userJoinAgreeDto.getAgreeToken(), userJoinAgreeDto);

        model.addAttribute("userJoinPostDto", new UserJoinPostDto());
        return "user/join/joinForm";
    }

    /**
     * id:join_2
     * 회원가입 api
     *
     * @param userJoinPostDto
     * @param bindingResult
     * @return
     */
    @PostMapping
    public String join(@Validated @ModelAttribute UserJoinPostDto userJoinPostDto,
        BindingResult bindingResult, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (StringUtils.hasText(userJoinPostDto.getPassword())) { // 비밀번호 재전송
            model.addAttribute("rePw", userJoinPostDto.getPassword());
        }
        // 비밀번호 보안 레벨 확인
        PwSecLevel pwSecLevel = PwChecker.checkPw(userJoinPostDto.getPassword());
        log.info("pwSecLevel.name() = {}", pwSecLevel.name());

        // 비밀번호 값이 유효하지 않은 경우
        if (pwSecLevel.equals(PwSecLevel.NOT)) {
            session.removeAttribute("emailAuthCodeDto");
            bindingResult.addError(
                new FieldErrorCustom("userJoinPostDto",
                    "password", userJoinPostDto.getPassword(),
                    "userJoinForm.password",
                    null));
            return "user/join/joinForm";
        }
        // 보안레벨 저장 나중에 -> 보안 위험 표시할 떄 유용
        userJoinPostDto.setPwSecLevel(pwSecLevel);

        if (bindingResult.hasErrors()) {
            return "user/join/joinForm";
        }
        //cookie 기간 확인

        // 이메일 인증 여부 확인
        Cookie[] cookies = request.getCookies();

        String agreeToken = CustomCookie.readCookie(cookies, "agreeToken");

        // 동의 여부 token 이 없을 경우 다시 동의 폼으로
        // 세션에 저장해둔 동의 여부 갖고오기
        UserJoinAgreeDto userJoinAgreeDto = (UserJoinAgreeDto) session.getAttribute(agreeToken);
        if (userJoinAgreeDto == null) {
            return "redirect:/user/join/agree";
        }

        EmailAuthCodeDto emailAuthCodeDto = (EmailAuthCodeDto) session.getAttribute("emailAuthCodeDto");

        if (emailAuthCodeDto == null || !emailAuthCodeDto.getIsSuccess()) {
            session.removeAttribute("emailAuthCodeDto");
            bindingResult.addError(
                new FieldErrorCustom("userJoinPostDto",
                    "email", userJoinPostDto.getEmail(),
                    "userJoinForm.email2",
                    null));
            return "user/join/joinForm";
        }
        // 중복 확인
        // 이메일 인증 체크후 인증되었던 이메일을 넣어줌
        userJoinPostDto.setEmail(emailAuthCodeDto.getEmail());
        try {
            userJoinService.join(userJoinPostDto, userJoinAgreeDto);
        } catch (DuplicateCheckException e) { // 중복 시
            bindingResult.addError(
                new FieldErrorCustom(
                    "userJoinPostDto",
                    e.getFieldName(),
                    e.getRejectValue(),
                    "duplication",
                    new String[]{e.getMessage()}));
            return "user/join/joinForm";
        }

        // 회원가입 성공
        return "redirect:/";
    }

    /**
     * id:join_3
     * 이용약관 동의 페이지 이동
     * 유효토큰 생성 api
     *
     * @param model
     * @return
     */
    @GetMapping("/agree")
    public String joinAgreeForm(Model model, HttpServletResponse response,HttpServletRequest request) {

        Cookie result = CustomCookie.getCookie(request.getCookies(), "agreeToken");

        // 뒤로가기 방지
        if (result != null) {
            result.setMaxAge(0);
            response.addCookie(result);
            return "redirect:/user/join/agree";
        }

        // 쿠키 생성
        UserJoinAgreeDto userJoinAgreeDto = UserJoinAgreeDto.createUserJoinAgree();

        CustomCookie.addCookie("/user/join","agreeToken",900,response,userJoinAgreeDto.getAgreeToken());

        //회원가입 토큰 발급
        model.addAttribute("userJoinAgreeDto", userJoinAgreeDto);
        return "user/join/joinAgreeForm";
    }

}

