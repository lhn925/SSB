package sky.Sss.domain.user.utili;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class UserTokenUtil {

    // token 발급
    public static String hashing(byte[] value, String Salt) {
        MessageDigest md = null;  // SHA3-256 해시함수를 사용
        try {
            md = MessageDigest.getInstance("SHA3-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // key-stretching
        for (int i = 0; i < 10000; i++) {
            String temp = Byte_to_String(value) + Salt;
            md.update(temp.getBytes());
            value = md.digest();
        }
        return Byte_to_String(value);
    }   // token 발급

    // 바이트 값을 16진수로 변경해준다
    private static String Byte_to_String(byte[] temp) {
        StringBuilder sb = new StringBuilder();
        for (byte a : temp) {
            sb.append(String.format("%02x", a));
        }
        return sb.toString();
    }

    // SALT 값 생성
    public static String getToken() {
        try {
            SecureRandom rnd = new SecureRandom();
            byte[] temp = new byte[10];
            rnd.nextBytes(temp);
            return Byte_to_String(temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
