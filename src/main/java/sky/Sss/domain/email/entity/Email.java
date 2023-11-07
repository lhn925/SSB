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
    public static final String JOIN_TOKEN_KEY = "703771fa8aac9d498186";
    public static final String PW_TOKEN_KEY = "ccec46f8f67b1a32e334";
    public static final String ID_TOKEN_KEY = "635794abd5fa77a0b233";


    public static Email createJoinEmail(String subject, EmailSendDto emailSendDto) {
        Email email = new Email();

        email.setSubject(subject);
        email.setToMail(emailSendDto.getEmail());
        return email;
    }


}
