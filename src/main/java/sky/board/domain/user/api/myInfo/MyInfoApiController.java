package sky.board.domain.user.api.myInfo;


import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.board.domain.user.dto.myInfo.UserNameUpdateDto;
import sky.board.domain.user.dto.myInfo.UserPictureUpdateDto;
import sky.board.domain.user.exception.DuplicateCheckException;
import sky.board.domain.user.service.myInfo.UserMyInfoService;
import sky.board.global.error.dto.ErrorGlobalResultDto;
import sky.board.global.error.dto.ErrorResultDto;
import sky.board.global.error.dto.Result;
import sky.board.global.file.dto.UploadFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/myInfo/api")
public class MyInfoApiController {

    private final MessageSource ms;
    private final UserMyInfoService userMyInfoService;


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
    public ResponseEntity userPictureUpdate(@Validated @ModelAttribute UserPictureUpdateDto file,BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()){
            return Result.getErrorResult(new ErrorResultDto(bindingResult, ms, request.getLocale()));
        }

        UploadFile uploadFile = null;
        try {
            uploadFile = userMyInfoService.userPictureUpdate(request, file.getFile());

        } catch (FileSizeLimitExceededException e) {
            e.printStackTrace();
            bindingResult.reject("error.fileSize.Limit");
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            bindingResult.reject("error");
            return Result.getErrorResult(
                new ErrorGlobalResultDto(bindingResult, ms, request.getLocale()));
        }
        return new ResponseEntity(new Result<>(uploadFile), HttpStatus.OK);
    }





}
