package sky.board.domain.user.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sky.board.domain.user.dto.UserJoinDto;
import sky.board.domain.user.service.UserJoinService;
import sky.board.domain.user.utill.ValidationSequence;


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
        RedirectAttributes redirectAttributes,Model model) {

        log.info("password {}",userJoinDto.getPassword());
        if (bindingResult.hasErrors()) {
            if (StringUtils.hasText(userJoinDto.getPassword())) { // 비밀번호 재전송
                log.info("password {}",userJoinDto.getPassword());
                model.addAttribute("rePw", userJoinDto.getPassword());
            }
            return "user/join/joinForm";
        }
        userJoinService.join(userJoinDto);
        return "redirect:/";
    }
}
