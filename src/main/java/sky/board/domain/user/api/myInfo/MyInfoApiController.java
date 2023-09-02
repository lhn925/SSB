package sky.board.domain.user.api.myInfo;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.dto.myInfo.UserNameUpdateDto;
import sky.board.domain.user.exception.DuplicateCheckException;
import sky.board.domain.user.service.join.UserJoinService;
import sky.board.domain.user.service.myInfo.UserMyInfoService;
import sky.board.global.error.dto.ErrorGlobalResultDto;
import sky.board.global.error.dto.ErrorResultDto;
import sky.board.global.error.dto.FieldErrorCustom;
import sky.board.global.error.dto.Result;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/myInfo/api")
public class MyInfoApiController {

    private final MessageSource ms;
    private final UserMyInfoService userMyInfoService;
    private final UserJoinService userJoinService;


    @PutMapping
    public ResponseEntity userNameUpdate(@Validated @RequestBody UserNameUpdateDto userNameUpdateDto,
        BindingResult bindingResult,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        try {
            // 중복체크
            userMyInfoService.userNameUpdate(request, userNameUpdateDto);
            return new ResponseEntity(new Result<>(userNameUpdateDto), HttpStatus.OK);
        } catch (DuplicateCheckException e) {
            bindingResult.reject("duplication", new String[]{e.getMessage()}, null);
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        } catch (IllegalArgumentException e) {
            bindingResult.reject("change.isNotAfter", new String[]{e.getMessage()}, null);
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
    }


    @PostMapping
    public ResponseEntity UserPictureUpdate(@Validated @RequestBody UserNameUpdateDto userNameUpdateDto,
        BindingResult bindingResult,
        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }
        try {
            // 중복체크
            userMyInfoService.userNameUpdate(request, userNameUpdateDto);
            return new ResponseEntity(new Result<>(userNameUpdateDto), HttpStatus.OK);
        } catch (DuplicateCheckException e) {
            bindingResult.reject("duplication", new String[]{e.getMessage()}, null);
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        } catch (IllegalArgumentException e) {
            bindingResult.reject("change.isNotAfter", new String[]{e.getMessage()}, null);
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
    }






}
