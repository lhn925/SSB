package sky.board.domain.user.utill;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.lang.Nullable;

public class ReadCookie {

    public static String readCookie(Cookie[] cookies, String key) {
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        String value = Arrays.stream(cookies)
            .filter(c -> c.getName().equals(key))
            .map(Cookie::getValue).findFirst().orElse(null);
        return value;
    }

    public static Cookie getCookie(Cookie[] cookies, String key) {
        if (cookies == null || cookies.length == 0) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                return cookie;
            }
        }
        return null;
    }
}
