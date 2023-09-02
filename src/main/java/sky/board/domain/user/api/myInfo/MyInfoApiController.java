package sky.board.domain.user.api.myInfo;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.dto.myInfo.UserNameUpdateDto;
import sky.board.domain.user.dto.myInfo.UserPictureUpdateDto;
import sky.board.domain.user.exception.DuplicateCheckException;
import sky.board.domain.user.service.join.UserJoinService;
import sky.board.domain.user.service.myInfo.UserMyInfoService;
import sky.board.global.error.dto.ErrorGlobalResultDto;
import sky.board.global.error.dto.ErrorResultDto;
import sky.board.global.error.dto.Result;
import sky.board.global.file.FileStore;
import sky.board.global.file.UploadFile;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/myInfo/api")
public class MyInfoApiController {

    private final MessageSource ms;
    private final UserMyInfoService userMyInfoService;
    private final UserJoinService userJoinService;
    private final FileStore fileStore;


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


    @PostMapping("/picture")
    public ResponseEntity userPictureUpdate(@ModelAttribute UserPictureUpdateDto file,BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()){
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        UploadFile uploadFile = null;
        try {
            uploadFile = userMyInfoService.userPictureUpdate( request, file.getFile());
            HttpSession session = request.getSession();
            UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
            log.info("userInfoDto.getUserName() = {}", userInfoDto.getUserName());
            log.info("userInfoDto.getPictureUrl() = {}", userInfoDto.getPictureUrl());

        } catch (IOException e) {
            bindingResult.reject("error");
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
        return new ResponseEntity(new Result<>(uploadFile), HttpStatus.OK);
    }


}
