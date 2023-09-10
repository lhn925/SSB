package sky.board.domain.email.api;

import static org.springframework.util.StringUtils.hasText;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.board.domain.email.dto.AuthTimeResponseDto;
import sky.board.domain.email.dto.EmailAuthCodeDto;
import sky.board.domain.email.dto.EmailSendDto;
import sky.board.domain.email.dto.CodeCheckRequestDto;
import sky.board.domain.email.dto.HelpEmailSendDto;
import sky.board.domain.email.entity.Email;
import sky.board.domain.email.model.EmailSendType;
import sky.board.domain.email.service.EmailService;
import sky.board.domain.user.dto.help.UserHelpDto;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.exception.DuplicateCheckException;
import sky.board.domain.user.model.Enabled;
import sky.board.domain.user.model.HelpType;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.service.join.UserJoinService;
import sky.board.domain.user.service.UserQueryService;
import sky.board.global.error.dto.ErrorGlobalResultDto;
import sky.board.global.error.dto.ErrorResult;
import sky.board.global.error.dto.ErrorResultDto;
import sky.board.global.error.dto.Result;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/email")
public class EmailApiController {


    private final EmailService emailService;
    private final UserJoinService userJoinService;
    private final UserQueryService userQueryService;
    private final MessageSource ms;

    //

    /**
     * id:emailApi_1
     * <p>
     * 회원가입 이메일 인증 번호 생성 후
     * session에 유효시간과 인증번호 저장
     * body에는 인증발급시간,인증유효시간 전달
     *
     * @param emailSendDto
     * @param bindingResult
     * @param request
     * @return
     */
    @PostMapping("/join")
    public ResponseEntity sendJoinMail(
        @Validated @RequestBody EmailSendDto emailSendDto,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        try {
            // 이메일 중복 체크
            userJoinService.checkEmail(emailSendDto.getEmail());
        } catch (DuplicateCheckException e) {
            bindingResult.reject("duplication", new Object[]{e.getMessage()}, null);
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }

        return sendEmail(EmailSendType.JOIN, emailSendDto, request);
    }


    /**
     * id:emailApi_2
     * <p>
     * 회원가입 이메일 인증 번호 유효 체크
     *
     * @param authCode
     * @param bindingResult
     * @param request
     * @return
     */
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

        if (emailAuthCodeDto == null || !hasText(emailAuthCodeDto.getCode())) { // 인증코드가 발급되지 않았을 경우
            return getErrorResultResponseEntity(bindingResult, "join.code.error", request);
        }

        if (requestTime.isAfter(emailAuthCodeDto.getAuthTimeLimit())) { // 인증 유효시간 체크 후 error시 반환
            return getErrorResultResponseEntity(bindingResult, "join.code.timeOut", request);
        }

        if (!(authCode.getAuthCode().equals(emailAuthCodeDto.getCode()))) { // 인증 코드 체크
            return getErrorResultResponseEntity(bindingResult, "join.code.mismatch", request);
        }

        // 이메일 인증 성공시 성공 여부 값에 true
        emailAuthCodeDto.changeSuccess(true);
        session.setAttribute("emailAuthCodeDto", emailAuthCodeDto);

        return new ResponseEntity(new Result<>(emailAuthCodeDto), HttpStatus.OK);
    }

    private ResponseEntity<ErrorResult> getErrorResultResponseEntity(BindingResult bindingResult, String errorCode,
        HttpServletRequest request) {
        bindingResult.reject(errorCode);
        return Result.getErrorResult(
            new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
    }

    /**
     * id:emailApi_3
     * <p>
     * HelpEmailSendDto helpEmailSendDto,
     *
     * @param bindingResult
     * @param request
     * @return
     */
    @PostMapping("/find")
    public ResponseEntity sendFindMail(
        @Validated @RequestBody HelpEmailSendDto helpEmailSendDto,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        try {
            /**
             * 비밀번호 찾기 시
             */
            if (helpEmailSendDto.getHelpType().equals(HelpType.PW)) {

                HttpSession session = request.getSession();
                UserHelpDto userHelpDto = (UserHelpDto) session.getAttribute("userHelpDto");
                if (!userHelpDto.getHelpType().equals(HelpType.PW)
                    || !userHelpDto.getUserId().equals(helpEmailSendDto.getUserId())) {
                    throw new UsernameNotFoundException("code.error");
                }
                CustomUserDetails statusUserId = userQueryService.findStatusUserId(userHelpDto.getUserId(), Enabled.ENABLED);
                /**
                 * 비밀 번호 찾기시
                 *
                 * 찾을려는 계정과
                 * 인증 받을려는 이메일이 서로 다를경우
                 * throw new UsernameNotFoundException("email.notfound");
                 */
                if (!statusUserId.getEmail().equals(helpEmailSendDto.getEmail())) {
                    throw new UsernameNotFoundException("email.notfound");
                }
            }
            // 등록 이메일 체크
            userJoinService.checkEmail(helpEmailSendDto.getEmail());
        } catch (DuplicateCheckException e) {
            // 등록한 이메일이 있을경우에 이메일 발송
            return sendEmail(EmailSendType.ID, helpEmailSendDto, request);
        } catch (UsernameNotFoundException e) {
            // 없으면 찾을수 없다고 경고 뜸
            bindingResult.reject(e.getMessage(), null, null);
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }

        // 없으면 찾을수 없다고 경고 뜸
        bindingResult.reject("sky.email.notFind", null, null);
        return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
    }


    private ResponseEntity sendEmail(EmailSendType sendType, EmailSendDto emailSendDto,
        HttpServletRequest request) {

        String subject = "sky.email.subject";
        String subArgs = setArgs(sendType, request);
        Email email = Email.createJoinEmail(
            ms.getMessage(subject, new Object[]{subArgs}, request.getLocale()),
            emailSendDto);

        JSONObject msObject = new JSONObject();

        msObject.put("content", ms.getMessage("sky.email.content", null, request.getLocale()));
        msObject.put("subContent1", ms.getMessage("sky.email.subContent1", null, request.getLocale()));
        msObject.put("subContent2", ms.getMessage("sky.email.subContent2", null, request.getLocale()));

        Optional<String> optCode = emailService.sendMail(msObject, email, "/email/sendEmail");
        LocalDateTime issueTime = LocalDateTime.now(); // 인증발급시간
        LocalDateTime authTime = issueTime.plusSeconds(300); // 5분 인증 시간

        String code = optCode.orElse(null);

        EmailAuthCodeDto emailResponseDto = EmailAuthCodeDto.builder()
            .code(code)
            .authTimeLimit(authTime)
            .isSuccess(false)
            .email(emailSendDto.getEmail()).build();

        HttpSession session = request.getSession();

        session.setAttribute("emailAuthCodeDto", emailResponseDto);
        return new ResponseEntity(new Result(new AuthTimeResponseDto(authTime, issueTime)),
            HttpStatus.OK);
    }

    private String setArgs(EmailSendType sendType, HttpServletRequest request) {
        String subArgs = "";
        switch (sendType) {
            case JOIN:
                subArgs = ms.getMessage("sky.signup", null, request.getLocale());
                break;
            case ID:
                subArgs = ms.getMessage("sky.findId", null, request.getLocale());
                break;
            case PW:
                subArgs = ms.getMessage("sky.findPw", null, request.getLocale());
                break;
            case EMAIL:
                subArgs = ms.getMessage("sky.email", null, request.getLocale());
                break;
            default:
        }

        return subArgs;
    }

}
