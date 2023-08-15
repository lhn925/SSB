package sky.board.domain.user.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import sky.board.domain.email.dto.EmailAuthCodeDto;
import sky.board.domain.user.dto.UserJoinAgreeDto;
import sky.board.domain.user.dto.UserJoinPostDto;
import sky.board.domain.user.model.PwSecLevel;
import sky.board.domain.user.exception.DuplicateCheckException;
import sky.board.domain.user.service.UserJoinService;
import sky.board.domain.user.utill.PwChecker;
import sky.board.domain.user.utill.ReadCookie;
import sky.board.global.error.dto.FieldErrorCustom;


@Slf4j
@RequestMapping("/join")
@RequiredArgsConstructor
@Controller
public class JoinController {

    private final UserJoinService userJoinService;
    private final MessageSource ms;

    /**
     * id:join_1
     * 회원가입 페이지 이동 api
     *
     * @param model
     *
     * @return
     */
    /**
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
            return "redirect:/join/agree";
        }
        HttpSession session = request.getSession();

        if (session.getAttribute("emailAuthCodeDto") != null) {// 인증번호 삭제 (뒤로가기 버그 방지)
            session.removeAttribute("emailAuthCodeDto");
        }

        String agreeToken = ReadCookie.readCookie(request.getCookies(), "agreeToken");

        // agreeToken 쿠키가 만료됐거나 , 쿠키에 저장된 토큰과 요청한 토큰이 안 맞을 경우
        if (!StringUtils.hasText(agreeToken) || (!agreeToken.equals(userJoinAgreeDto.getAgreeToken()))) {
            bindingResult.reject("code.error");
            return "redirect:/join/agree";
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
        if (pwSecLevel.name().equals(PwSecLevel.NOT)) {
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

        // 이메일 인증 여부 확인
        if (bindingResult.hasErrors()) {
            return "user/join/joinForm";
        }
        //cookie 기간 확인

        Cookie[] cookies = request.getCookies();

        String agreeToken = ReadCookie.readCookie(cookies, "agreeToken");

        // 동의 여부 token 이 없을 경우 다시 동의 폼으로
        // 세션에 저장해둔 동의 여부 갖고오기
        UserJoinAgreeDto userJoinAgreeDto = (UserJoinAgreeDto) session.getAttribute(agreeToken);
        if (userJoinAgreeDto == null) {
            return "redirect:/join/agree";
        }

        EmailAuthCodeDto emailAuthCodeDto = (EmailAuthCodeDto) session.getAttribute("emailAuthCodeDto");

        log.info("emailAuthCodeDto = {}", emailAuthCodeDto);
        log.info("emailAuthCodeDto.getIsSuccess() = {}", emailAuthCodeDto.getIsSuccess());

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
        try {
            userJoinService.join(userJoinPostDto, userJoinAgreeDto);
        } catch (DuplicateCheckException e) { // 중복 시
            bindingResult.addError(
                new FieldErrorCustom(
                    "userJoinPostDto",
                    e.getFieldName(),
                    e.getRejectValue(),
                    "join.duplication",
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

        Cookie result = ReadCookie.getCookie(request.getCookies(), "agreeToken");

        // 뒤로가기 방지
        if (result != null) {
            result.setMaxAge(0);
            response.addCookie(result);
            return "redirect:/join/agree";
        }

        // 쿠키 생성
        UserJoinAgreeDto userJoinAgreeDto = UserJoinAgreeDto.createUserJoinAgree();
        Cookie cookie = new Cookie("agreeToken", userJoinAgreeDto.getAgreeToken());
        cookie.setMaxAge(900); // 15분 유효
        cookie.setPath("/join");

        // 쿠키에 agreeToken 저장
        response.addCookie(cookie);

        //회원가입 토큰 발급
        model.addAttribute("userJoinAgreeDto", userJoinAgreeDto);
        return "user/join/joinAgreeForm";
    }

}
