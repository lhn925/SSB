package sky.board.domain.email.api;

import static org.springframework.util.StringUtils.hasText;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sky.board.domain.email.dto.AuthTimeResponseDto;
import sky.board.domain.email.dto.EmailAuthCodeDto;
import sky.board.domain.email.dto.EmailPostDto;
import sky.board.domain.email.dto.CodeCheckRequestDto;
import sky.board.domain.email.entity.Email;
import sky.board.domain.email.service.EmailService;
import sky.board.domain.user.ex.DuplicateCheckException;
import sky.board.domain.user.service.UserJoinService;
import sky.board.global.dto.ErrorGlobalResultDto;
import sky.board.global.dto.ErrorResultDto;
import sky.board.global.dto.Result;


@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/email")
public class EmailApiController {


    private final EmailService emailService;
    private final UserJoinService userJoinService;
    private final MessageSource ms;

    //

    /**
     * 회원가입 이메일 인증 번호 생성 후
     * session에 유효시간과 인증번호 저장
     * body에는 인증발급시간,인증유효시간 전달
     *
     * @param emailPostDto
     * @param bindingResult
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/join")
    public ResponseEntity sendJoinMail(
        @Validated @RequestBody EmailPostDto emailPostDto,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        try {
            // 이메일 중복 체크
            userJoinService.checkEmail(emailPostDto.getEmail());
        } catch (DuplicateCheckException e) {
            bindingResult.reject("join.duplication", new Object[]{e.getMessage()}, null);
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }

        Email email = Email.createJoinEmail("[SKYBOARD] 회원가입시 이메일 인증을 위한 인증 코드 발송", emailPostDto);

        Optional<String> optCode = emailService.sendMail(email, "/email/joinSendEmail");
        LocalDateTime issueTime = LocalDateTime.now(); // 인증발급시간
        LocalDateTime authTime = issueTime.plusSeconds(300); // 5분 인증 시간

        String code = optCode.orElse(null);

        EmailAuthCodeDto emailResponseDto = new EmailAuthCodeDto(code, authTime, false);

        HttpSession session = request.getSession();

        session.setAttribute("emailAuthCodeDto", emailResponseDto);
        return new ResponseEntity(new Result(new AuthTimeResponseDto(authTime, issueTime)),
            HttpStatus.OK);
    }


    // 회원가입 이메일 인증 번호 유효 체크
    @ResponseBody
    @PostMapping("/codeCheck")
    public ResponseEntity sendJoinMail(
        @Validated @RequestBody CodeCheckRequestDto authCode,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        // 사용자가 인증을 요청한 시간
        LocalDateTime requestTime = LocalDateTime.now();

        // session에서 이메일 유효값 가져오기
        HttpSession session = request.getSession();
        EmailAuthCodeDto emailAuthCodeDto = (EmailAuthCodeDto) session.getAttribute(
            "emailAuthCodeDto");

        log.info("email 인증 코드{}", authCode.getAuthCode());
        log.info("과연? {}", hasText(emailAuthCodeDto.getCode()));
        log.info("과연? 코드는? {}", emailAuthCodeDto.getCode());
        if (emailAuthCodeDto == null || !hasText(emailAuthCodeDto.getCode())) { // 인증코드가 발급되지 않았을 경우
            bindingResult.reject("join.code.error");
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }

        if (requestTime.isAfter(emailAuthCodeDto.getAuthTimeLimit())) { // 인증 유효시간 체크 후 error시 반환
            bindingResult.reject("join.code.timeOut");
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }

        if (!authCode.getAuthCode().equals(emailAuthCodeDto.getCode())) { // 인증 코드 체크
            bindingResult.reject("join.code.mismatch");
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }

        // 이메일 인증 성공시 성공 여부 값에 true
        emailAuthCodeDto.changeSuccess(true);

        return new ResponseEntity(new Result<>(emailAuthCodeDto), HttpStatus.OK);
    }


}
