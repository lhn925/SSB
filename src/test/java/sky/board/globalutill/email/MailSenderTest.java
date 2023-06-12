package sky.board.globalutill.email;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.board.globalutill.email.dto.EmailPostDto;
import sky.board.globalutill.email.entity.Email;
import sky.board.globalutill.email.service.EmailService;

@SpringBootTest
class MailSenderTest {


    @Autowired
    EmailService emailService;


    @Test
    void 메일보내기() {

        EmailPostDto emailPostDto = new EmailPostDto();
        emailPostDto.setEmail("2221325@naver.com");

        Email email = Email.createJoinEmail("[SAVIEW] 이메일 인증을 위한 인증 코드 발송", emailPostDto);
        String code = emailService.sendMail(email,"/email/joinSendEmail");
        System.out.println("code = " + code);
    }

}