
package sky.Sss.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import java.util.Random;
import org.thymeleaf.spring6.SpringTemplateEngine;
import sky.Sss.domain.email.entity.Email;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final MessageSource ms;


    public Optional<String> sendMail(JSONObject msObject,Email email,String type) {
        String authNum = createCode();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false,
                "UTF-8");
            mimeMessageHelper.setTo(email.getToMail()); // 메일 수신자
            mimeMessageHelper.setSubject(email.getSubject()); // 메일 제목
            mimeMessageHelper.setText(setContext(msObject,authNum, type), true); // 메일 본문 내용, HTML 여부
            javaMailSender.send(mimeMessage);

            log.info("Success");
            return Optional.ofNullable(authNum);
        } catch (MessagingException e) {
            log.info("fail");
            throw new RuntimeException(e);
        }
    }



    // 인증번호 및 임시 비밀번호 생성 메서드
    public String createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0:
                    key.append((char) ((int) random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) ((int) random.nextInt(26) + 65));
                    break;
                default:
                    key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }

    // thymeleaf를 통한 html 적용
    public String setContext(JSONObject msObject,String code, String type) {
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("content",msObject.get("content"));
        context.setVariable("subContent1",msObject.get("subContent1"));
        context.setVariable("subContent2",msObject.get("subContent2"));
        return templateEngine.process(type, context);
    }
}