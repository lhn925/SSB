package sky.Sss.domain.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.email.dto.EmailAuthCodeDto;
import sky.Sss.domain.user.dto.join.JoinEmailDuplicateDto;
import sky.Sss.domain.user.dto.join.JoinIdDuplicateDto;
import sky.Sss.domain.user.dto.join.JoinUserNameDuplicateDto;
import sky.Sss.domain.user.dto.join.UserJoinPostDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.PwSecLevel;
import sky.Sss.domain.user.exception.DuplicateCheckException;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.join.UserJoinService;
import sky.Sss.domain.user.utili.PwChecker;
import sky.Sss.global.error.dto.ErrorResultDto;
import sky.Sss.global.error.dto.FieldErrorCustom;
import sky.Sss.global.error.dto.Result;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/join")
public class JoinController {

    private final UserJoinService userJoinService;
    private final MessageSource ms;
    private final UserQueryService userQueryService;

    /*  *//**
     * id:join_1
     * 회원가입 페이지 이동 api
     *
     * @param userJoinAgreeDto
     * @param bindingResult
     * @param request
     * @return
     *//*
    @GetMapping
    public ResponseEntity joinForm(HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (session.getAttribute("emailAuthCodeDto") != null) {// 인증번호 삭제 (뒤로가기 버그 방지)
            session.removeAttribute("emailAuthCodeDto");
        }

        String agreeToken = CustomCookie.readCookie(request.getCookies(), "agreeToken");

        // agreeToken 쿠키가 만료됐거나 , 쿠키에 저장된 토큰과 요청한 토큰이 안 맞을 경우
        if (!StringUtils.hasText(agreeToken) || (!agreeToken.equals(userJoinAgreeDto.getAgreeToken()))) {
            bindingResult.reject("code.error");
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        // 약관 동의 데이터 session에 저장
        session.setAttribute(userJoinAgreeDto.getAgreeToken(), userJoinAgreeDto);

//        model.addAttribute("userJoinPostDto", new UserJoinPostDto());
        return new ResponseEntity<>(HttpStatus.OK);
    }*/

    /**
     * id:join_2
     * 회원가입 api
     *
     * @param userJoinPostDto
     * @param bindingResult
     * @return
     */
    @PostMapping
    public ResponseEntity join(@Validated @RequestBody UserJoinPostDto userJoinPostDto,
        BindingResult bindingResult, HttpServletRequest request) throws DuplicateCheckException {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        HttpSession session = request.getSession();

        // 비밀번호 보안 레벨 확인
        PwSecLevel pwSecLevel = PwChecker.checkPw(userJoinPostDto.getPassword());

        // 비밀번호 값이 유효하지 않은 경우
        if (pwSecLevel.equals(PwSecLevel.NOT)) {
            session.removeAttribute("emailAuthCodeDto");
            addError(bindingResult, "password", userJoinPostDto.getPassword(), "userJoinForm.password");
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        // 보안레벨 저장 나중에 -> 보안 위험 표시할 떄 유용
        userJoinPostDto.setPwSecLevel(pwSecLevel);

        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        //cookie 기간 확인
        EmailAuthCodeDto emailAuthCodeDto = (EmailAuthCodeDto) session.getAttribute("emailAuthCodeDto");

        if (emailAuthCodeDto == null || !emailAuthCodeDto.getIsSuccess()) {
            session.removeAttribute("emailAuthCodeDto");
            addError(bindingResult, "email", userJoinPostDto.getEmail(), "userJoinForm.email2");
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        // 중복 확인
        // 이메일 인증 체크후 인증되었던 이메일을 넣어줌
        userJoinPostDto.setEmail(emailAuthCodeDto.getEmail());
        userJoinService.duplicateCheckJoin(userJoinPostDto, bindingResult);
        userJoinService.join(userJoinPostDto);
        // 회원가입 성공
        return new ResponseEntity(HttpStatus.OK);
    }

    private void addError(BindingResult bindingResult, String email, String userJoinPostDto, String code) {
        bindingResult.addError(
            new FieldErrorCustom("userJoinPostDto",
                email, userJoinPostDto,
                code,
                null));
    }

    /*   *//**
     * id:join_3
     * 이용약관 동의 페이지 이동
     * 유효토큰 생성 api
     *
     * @param model
     * @return
     *//*
    @GetMapping("/agree")
    public String joinAgreeForm(Model model, HttpServletResponse response,HttpServletRequest request) {

        Cookie result = CustomCookie.getCookie(request.getCookies(), "agreeToken");

        // 뒤로가기 방지
        if (result != null) {
            result.setMaxAge(0);
            response.addCookie(result);
            return "redirect:/users/join/agree";
        }

        // 쿠키 생성
        UserJoinAgreeDto userJoinAgreeDto = UserJoinAgreeDto.createUserJoinAgree();

        CustomCookie.addCookie("/users/join","agreeToken",900,response,userJoinAgreeDto.getAgreeToken());

        //회원가입 토큰 발급
        model.addAttribute("userJoinAgreeDto", userJoinAgreeDto);
        return "users/join/joinAgreeForm";
    }*/


    /**
     * id:joinApi_1
     * 유저아이디 중복체크
     *
     * @param userId
     * @param bindingResult
     * @return
     */
    @GetMapping("/duplicate/userId")
    public ResponseEntity checkUserId(@Validated @ModelAttribute("userId") JoinIdDuplicateDto userId,
        BindingResult bindingResult,HttpServletRequest request) throws DuplicateCheckException {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        userJoinService.checkId(userId.getUserId(), bindingResult);
        return new ResponseEntity(userId, HttpStatus.OK);
    }


    /**
     * id:joinApi_2
     * 유저네임 중복체크
     *
     * @param userName
     * @param bindingResult
     * @return
     */
    @GetMapping("/duplicate/userName")
    public ResponseEntity checkUserName(@Validated @ModelAttribute("userName") JoinUserNameDuplicateDto userName,
        BindingResult bindingResult,HttpServletRequest request) throws DuplicateCheckException {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        userJoinService.checkUserName(userName.getUserName(), bindingResult);
        return new ResponseEntity(userName, HttpStatus.OK);
    }

    /**
     * 이메일 중복체크
     * id:joinApi_3
     *
     * @param email
     * @param bindingResult
     * @return
     */
    @GetMapping("/duplicate/email")
    public ResponseEntity checkEmail(@Validated @ModelAttribute("email") JoinEmailDuplicateDto email,
        BindingResult bindingResult,HttpServletRequest request) throws DuplicateCheckException {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        userJoinService.checkEmail(email.getEmail(), bindingResult);
        return new ResponseEntity(email, HttpStatus.OK);
    }

}

