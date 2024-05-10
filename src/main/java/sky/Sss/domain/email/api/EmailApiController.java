package sky.Sss.domain.email.api;

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
import sky.Sss.domain.email.dto.AuthTimeResponseDto;
import sky.Sss.domain.email.dto.EmailAuthCodeDto;
import sky.Sss.domain.email.dto.EmailSendDto;
import sky.Sss.domain.email.dto.CodeCheckRequestDto;
import sky.Sss.domain.email.dto.HelpEmailSendDto;
import sky.Sss.domain.email.entity.Email;
import sky.Sss.domain.email.model.SendType;
import sky.Sss.domain.email.service.EmailService;
import sky.Sss.domain.user.dto.help.UserHelpDto;
import sky.Sss.domain.user.dto.login.CustomUserDetails;
import sky.Sss.domain.user.exception.DuplicateCheckException;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.model.HelpType;
import sky.Sss.domain.user.service.join.UserJoinService;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.utili.TokenUtil;
import sky.Sss.global.error.dto.ErrorGlobalResultDto;
import sky.Sss.global.error.dto.ErrorResult;
import sky.Sss.global.error.dto.ErrorResultDto;
import sky.Sss.global.error.dto.Result;


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
    public ResponseEntity<?> sendJoinMail(
        @Validated @RequestBody EmailSendDto emailSendDto,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        try {
            // 이메일 중복 체크
            userJoinService.checkEmail(emailSendDto.getEmail(), bindingResult);
        } catch (DuplicateCheckException e) {
            bindingResult.reject("duplication", new Object[]{e.getMessage()}, null);
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }

        return sendAuthEmail(SendType.JOIN, emailSendDto, request);
    }


    /**
     * id:emailApi_2
     * <p>
     * 이메일 인증 번호 유효 체크
     *
     * @param authCode
     * @param bindingResult
     * @param request
     * @return
     */
    @PostMapping("/code/check")
    public ResponseEntity verifyAuthCode(
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

        String hashingToken = getHashing(authCode);

        if (!(emailAuthCodeDto.getAuthToken().equals(hashingToken))
            || !(authCode.getAuthCode().equals(emailAuthCodeDto.getCode()))) { // 인증 코드 체크
            return getErrorResultResponseEntity(bindingResult, "join.code.mismatch", request);
        }

        // 이메일 인증 성공시 성공 여부 값에 true
        emailAuthCodeDto.changeSuccess(true);
        session.setAttribute("emailAuthCodeDto", emailAuthCodeDto);

        return ResponseEntity.ok(emailAuthCodeDto);
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
            HttpSession session = request.getSession(false);
            /**
             * 비밀번호 찾기 시
             */
            if (helpEmailSendDto.getHelpType().equals(HelpType.PW)) {
                UserHelpDto userHelpDto = (UserHelpDto) session.getAttribute("userHelpDto");
                /**
                 * 잘못된 접근일 경우 아이디가 다르거나 helpType이 다를 경우
                 */
                if (userHelpDto == null || !userHelpDto.getUserId().equals(helpEmailSendDto.getUserId())) {
                    throw new UsernameNotFoundException("code.error");
                }
                CustomUserDetails statusUserId = userQueryService.findStatusUserId(userHelpDto.getUserId(),
                    Enabled.ENABLED);
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
            userJoinService.checkEmail(helpEmailSendDto.getEmail(), bindingResult);
        } catch (DuplicateCheckException e) {
            // 등록한 이메일이 있을경우에 이메일 발송
            return sendAuthEmail(helpEmailSendDto.getSendType(), helpEmailSendDto, request);
        } catch (UsernameNotFoundException e) {
            bindingResult.reject(e.getMessage());
            return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
        // 없으면 찾을수 없다고 경고 뜸
        bindingResult.reject("sky.email.notFind");
        return Result.getErrorResult(new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
    }


    private ResponseEntity sendAuthEmail(SendType sendType, EmailSendDto emailSendDto,
        HttpServletRequest request) {

        String subject = "sky.email.subject";
        Email email = Email.createJoinEmail(ms.getMessage(subject, null, request.getLocale()),
            emailSendDto);

        JSONObject msObject = new JSONObject();

        msObject.put("content", ms.getMessage("sky.email.content", null, request.getLocale()));
        msObject.put("subContent1", ms.getMessage("sky.email.subContent1", null, request.getLocale()));
        msObject.put("subContent2", ms.getMessage("sky.email.subContent2", null, request.getLocale()));

        Optional<String> optCode = emailService.sendMail(msObject, email, "email/sendEmail");
        LocalDateTime issueTime = LocalDateTime.now(); // 인증발급시간
        LocalDateTime authTime = issueTime.plusSeconds(Email.TIME_LIMIT); // 5분 인증 시간

        String code = optCode.orElse(null);

        // authToken 요청 구분을 위해 pw,id,join 각각 키를 만들어 구분
        String authToken = TokenUtil.getToken(); // 유저에게 줄 token
        String salt = getTokeKey(sendType); // salt
        String hashingToken = TokenUtil.hashing(authToken.getBytes(), salt); // 서버가 가지고 있을 해싱 토큰
        EmailAuthCodeDto emailResponseDto = EmailAuthCodeDto.builder()
            .code(code)
            .authToken(hashingToken)
            .authTimeLimit(authTime)
            .isSuccess(false)
            .email(emailSendDto.getEmail()).build();

        HttpSession session = request.getSession();

        session.setAttribute("emailAuthCodeDto", emailResponseDto);
        return new ResponseEntity(new AuthTimeResponseDto(authTime, issueTime, authToken),
            HttpStatus.OK);
    }

    private String getTokeKey(SendType sendType) {
        String key = "";
        switch (sendType) {
            case JOIN:
                key = Email.JOIN_TOKEN_KEY;
                break;
            case ID:
                key = Email.ID_TOKEN_KEY;
                break;
            case PW:
                key = Email.PW_TOKEN_KEY;
                break;
        }
        return key;
    }

    /**
     * token 구분을 위해 요청한 sendType key와 token결합 후 비교
     *
     * @param authCode
     * @return
     */
    private String getHashing(CodeCheckRequestDto authCode) {
        String salt = getTokeKey(authCode.getSendType());
        String authToken = authCode.getAuthToken();
        String hashingToken = TokenUtil.hashing(authToken.getBytes(), salt);
        return hashingToken;
    }

    private ResponseEntity<ErrorResult> getErrorResultResponseEntity(BindingResult bindingResult, String errorCode,
        HttpServletRequest request) {
        bindingResult.reject(errorCode);
        return Result.getErrorResult(
            new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
    }

}
