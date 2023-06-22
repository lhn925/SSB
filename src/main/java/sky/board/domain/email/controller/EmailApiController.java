package sky.board.domain.email.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sky.board.domain.email.dto.EmailPostDto;
import sky.board.domain.email.dto.EmailResponseDto;
import sky.board.domain.email.entity.Email;
import sky.board.domain.email.service.EmailService;
import sky.board.globalutill.ex.ErrorResult;


@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/email")
public class EmailApiController {


    private final EmailService emailService;
    private final MessageSource ms;


    // 회원가입 이메일 인증 - 요청 시 body로 인증번호 반환하도록 작성하였음
    @ResponseBody
    @PostMapping("/join")
    public ResponseEntity<?> sendJoinMail(
        @Validated @RequestBody EmailPostDto emailPostDto,
        BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResult.builder()
                .errors(bindingResult)
                .messageSource(ms)
                .locale(request.getLocale()).build());
        }

        Email email = Email.createJoinEmail("[SKYBOARD] 회원가입시 이메일 인증을 위한 인증 코드 발송", emailPostDto);

        String code = emailService.sendMail(email, "/email/joinSendEmail");

        EmailResponseDto emailResponseDto = new EmailResponseDto();
        emailResponseDto.setCode(code);

        return ResponseEntity.ok().body(emailResponseDto);
    }

}
