package sky.Sss.domain.user.utili;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.util.StringUtils;

public class CustomCookie {

    public static String readCookie(Cookie[] cookies, String key) {
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        String value = Arrays.stream(cookies)
            .filter(c -> c.getName().equals(key))
            .map(Cookie::getValue).findFirst().orElse(null);
        return value;
    }


    public static void addCookie(String url, String name, int maxAge, HttpServletResponse response, String value) {
        Cookie cookie = null;
        if (StringUtils.hasText(name) && StringUtils.hasText(value)) {
            cookie = new Cookie(name, value);
        }
        if (maxAge != 0) {
            cookie.setMaxAge(maxAge); // 15분 유효
        }
        if (StringUtils.hasText(url)) {
            cookie.setPath(url);
        }
        // 쿠키에 agreeToken 저장
        response.addCookie(cookie);
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

    public static void delete(Cookie cookie,  HttpServletResponse response) {
        if (cookie != null) {
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }
}
