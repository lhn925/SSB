package sky.board.domain.user.utill;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sky.board.domain.user.model.PwSecLevel;

// 비밀번호 보안 레벨 확인 서버 버전
public class PwChecker {

    public static PwSecLevel checkPw(String pw) {
        int secLevel = 0;
        PwSecLevel[] values = PwSecLevel.values();

        // 같은 문자 반복 및 한글 8글자 이하 차단
        Pattern regex1 = Pattern.compile("^(.)\\1{1,7}$");
        // 대문자 찾기
        Pattern regex2 = Pattern.compile("[A-Z]");
        // 특수표현식 찾기
        Pattern regex3 = Pattern.compile("[!@#$%^&*()\\-_=+\\\\|\\[\\]{};:'\",.<>/?]");
        // 숫자 찾기
        Pattern regex4 = Pattern.compile("\\d+");
        // 연속된 문자열 찾기
        Pattern regex5 = Pattern.compile("(.)\\1{7,}");
        // 한글 확인
        Pattern regex7 = Pattern.compile("[ㄱ-ㅎ가-힣]");
        // 이모지 확인
        Pattern regex8 = Pattern.compile("[\\p{So}]", Pattern.UNICODE_CHARACTER_CLASS);

        int pwLen = pw.length();

        pw = pw.trim();
        pw = pw.replaceAll(" ", "");

        if (pwLen < 8 || pwLen >= 17) {
            secLevel = 0;
            return values[secLevel];
        }

        if (regex8.matcher(pw).find() || regex7.matcher(pw).find() || regex1.matcher(pw).find()) {
            // 이모지, 한글, 같은 문자 반복일 경우 - 사용불가 (0점)
            secLevel = 0;
            return values[secLevel];
        }

        Matcher matcher = regex2.matcher(pw);
        if (matcher.find()) {
            // 대문자가 있을 경우 (1점)
            ++secLevel;
        }

        matcher = regex4.matcher(pw);
        if (matcher.find()) {
            // 숫자가 있을 경우 (1점)
            ++secLevel;
        }

        matcher = regex3.matcher(pw);
        if (matcher.find()) {
            // 특수표현식이 있을 경우 (1점)
            ++secLevel;
        }

        if (secLevel < 1) {
            matcher = regex5.matcher(pw);
            if (matcher.find()) {
                // 연속된 문자열이 있을 경우 (0점)
                secLevel = 0;
                return values[secLevel];
            }
        }

        return values[secLevel];
    }
}