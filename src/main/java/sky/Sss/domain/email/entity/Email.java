package sky.Sss.domain.email.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.email.dto.EmailSendDto;

@Getter
@Setter(value = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class Email {

    private String subject; // 제목
    private String toMail; // 유저
    public static final Integer TIME_LIMIT = 300;


    public static Email createJoinEmail(String subject, EmailSendDto emailSendDto) {
        Email email = new Email();

        email.setSubject(subject);
        email.setToMail(emailSendDto.getEmail());
        return email;
    }


}
