package sky.board.domain.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sky.board.domain.user.dto.UserJoinDto;
import sky.board.domain.user.service.UserJoinService;


@Slf4j
@RequestMapping("/join")
@RequiredArgsConstructor
@Controller
public class JoinController {

    private final UserJoinService userJoinService;

    /**
     * 회원가입 페이지 이동 api
     *
     * @param model
     * @return
     */
    @GetMapping
    public String joinForm(Model model) {
        model.addAttribute("userJoinDto", new UserJoinDto());
        return "user/join/joinForm";
    }

    /**
     * 회원가입 api
     *
     * @param userJoinDto
     * @param bindingResult
     * @param redirectAttributes
     * @return
     */
    @PostMapping
    public String join(@Validated @ModelAttribute UserJoinDto userJoinDto,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes) {
        log.info("여기는 Join");
        if (bindingResult.hasErrors()) {
            return "user/join/joinForm";
        }
        userJoinService.join(userJoinDto);
        return "redirect:/";
    }

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
