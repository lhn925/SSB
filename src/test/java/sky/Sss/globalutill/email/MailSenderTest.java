package sky.Sss.globalutill.email;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.email.dto.EmailSendDto;
import sky.Sss.domain.email.entity.Email;
import sky.Sss.domain.email.service.EmailService;

@SpringBootTest
class MailSenderTest {


    @Autowired
    EmailService emailService;


    @Test
    void 메일보내기() {

        EmailSendDto emailSendDto = new EmailSendDto();
        emailSendDto.setEmail("2221325@naver.com");

        Email email = Email.createJoinEmail("[SAVIEW] 이메일 인증을 위한 인증 코드 발송", emailSendDto);
//        String code = emailService.sendMail(email,"/email/joinSendEmail");
//        System.out.println("code = " + code);
    }

}