package sky.board.domain.user.api;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sky.board.domain.user.dto.JoinDuplicateDto;
import sky.board.domain.user.ex.DuplicateCheckException;
import sky.board.domain.user.service.UserJoinService;
import sky.board.global.dto.ErrorDetailDto;
import sky.board.global.dto.ErrorGlobalResultDto;
import sky.board.global.dto.ErrorResult;
import sky.board.global.dto.ErrorResultDto;
import sky.board.global.dto.FieldErrorCustom;
import sky.board.global.dto.Result;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/join/api")
public class JoinApiController {

    private final UserJoinService userJoinService;
    private final MessageSource ms;


    /**
     *  유저아이디 중복체크
     * @param userId
     * @param bindingResult
     * @param request
     * @return
     */
    @GetMapping("/duplicate/id")
    public ResponseEntity checkUserId(@ModelAttribute("userId") JoinDuplicateDto userId,
        BindingResult bindingResult, HttpServletRequest request) {

        log.info("id {}",userId.getUserId());
        try {
            userJoinService.checkId(userId.getUserId());
        } catch (DuplicateCheckException e) {
            bindingResult.addError(
                new FieldErrorCustom(
                    "JoinDuplicateDto",
                    e.getFieldName(), e.getRejectValue(),
                    "join.duplication",
                    new String[]{e.getMessage()}));

            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        return new ResponseEntity(new Result<>(userId), HttpStatus.OK);
    }


    /**
     * 유저네임 중복체크
     * @param userName
     * @param bindingResult
     * @param request
     * @return
     */
    @GetMapping("/duplicate/userName")
    public ResponseEntity checkUserName(@ModelAttribute("userName") JoinDuplicateDto userName,BindingResult bindingResult,HttpServletRequest request) {
        try {
            userJoinService.checkUserName(userName.getUserName());
        } catch (DuplicateCheckException e) {
            bindingResult.addError(
                new FieldErrorCustom(
                    "JoinDuplicateDto",
                    e.getFieldName(), e.getRejectValue(),
                    "join.duplication",
                    new String[]{e.getMessage()}));
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        return new ResponseEntity(new Result<>(userName), HttpStatus.OK);
    }

    /**
     *
     * @param email
     * @param bindingResult
     * @param request
     * @return
     */
    @GetMapping("/duplicate/email")
    public ResponseEntity checkEmail(@ModelAttribute("email") JoinDuplicateDto email,BindingResult bindingResult,HttpServletRequest request) {
        try {
            userJoinService.checkEmail(email.getUserName());
        } catch (DuplicateCheckException e) {
            bindingResult.addError(
                new FieldErrorCustom(
                    "JoinDuplicateDto",
                    e.getFieldName(), e.getRejectValue(),
                    "join.duplication",
                    new String[]{e.getMessage()}));
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        return new ResponseEntity(new Result<>(email), HttpStatus.OK);
    }
}
