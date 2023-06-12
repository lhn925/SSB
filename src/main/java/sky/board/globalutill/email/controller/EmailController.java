package sky.board.globalutill.email.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import sky.board.globalutill.email.dto.EmailPostDto;
import sky.board.globalutill.email.dto.EmailResponseDto;
import sky.board.globalutill.email.entity.Email;
import sky.board.globalutill.email.service.EmailService;


@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/email")
public class EmailController {


    private final EmailService emailService;

    // 회원가입 이메일 인증 - 요청 시 body로 인증번호 반환하도록 작성하였음
    @PostMapping("/join")
    public ResponseEntity sendJoinMail(@Validated @RequestBody EmailPostDto emailPostDto) {

        Email email = Email.createJoinEmail("[SKYBOARD] 회원가입시 이메일 인증을 위한 인증 코드 발송", emailPostDto);

        String code = emailService.sendMail(email,"/email/joinSendEmail");

        EmailResponseDto emailResponseDto = new EmailResponseDto();
        emailResponseDto.setCode(code);

        return ResponseEntity.ok(emailResponseDto);
    }

}
