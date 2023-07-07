package sky.board.domain.user.api;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sky.board.domain.user.ex.DuplicateCheckException;
import sky.board.domain.user.service.UserJoinService;
import sky.board.global.dto.ErrorGlobalResultDto;
import sky.board.global.dto.ErrorResult;

@RestController
@RequiredArgsConstructor
@RequestMapping("/join/api")
public class JoinApiController {

    private final UserJoinService userJoinService;


    /**
     * userId 중복 체크 Api
     * 중복x:  200 ok
     * 중복o: 400 error
     *
     * @param userId
     * @return
     */
    @GetMapping("/duplicate/id")
    public ResponseEntity checkUserId(@RequestParam("userId") String userId) {
        userJoinService.checkId(userId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * userName 중복 체크 Api
     * 중복x:  200 ok
     * 중복o: 400 error
     *
     * @param userName
     * @return
     */
    @GetMapping("/duplicate/userName")
    public ResponseEntity checkUserName(@RequestParam("userName") String userName) {
        userJoinService.checkUserName(userName);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * email 중복 체크 Api
     * 중복x:  200 ok
     * 중복o: 400 error
     *
     * @param email
     * @return
     */
    @GetMapping("/duplicate/email")
    public ResponseEntity checkUserEmail(@RequestParam("email") String email) {
        userJoinService.checkEmail(email);
        return new ResponseEntity(HttpStatus.OK);
    }

}
