package sky.board.domain.user.utill;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PwEncryptor {

    private final int SALT_SIZE = 15;

    // 비밀번호 해싱
    public String hashing(byte[] password, String Salt) {
        MessageDigest md = null;  // SHA3-256 해시함수를 사용

        try {
            md = MessageDigest.getInstance("SHA3-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        // key-stretching
        for (int i = 0; i < 10000; i++) {
            String temp = Byte_to_String(password) + Salt;  // 패스워드와 Salt 를 합쳐 새로운 문자열 생성
            md.update(temp.getBytes());            // temp 의 문자열을 해싱하여 md 에 저장해둔다
            password = md.digest();              // md 객체의 다이제스트를 얻어 password 를 갱신한다
        }

        return Byte_to_String(password);
    }

    // SALT 값 생성
    public String getSALT() {
        try {
            SecureRandom rnd = new SecureRandom();
            byte[] temp = new byte[SALT_SIZE];
            rnd.nextBytes(temp);

            return Byte_to_String(temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 바이트 값을 16진수로 변경해준다
    private String Byte_to_String(byte[] temp) {
        StringBuilder sb = new StringBuilder();
        for (byte a : temp) {
            sb.append(String.format("%02x", a));
        }
        return sb.toString();
    }
}
