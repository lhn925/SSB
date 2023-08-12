package sky.board.domain.user.utill;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.lang.Nullable;

public class ReadCookie {

    public static Optional<String> readCookie(@Nullable Cookie[] cookies, String key) {

        if (cookies == null || cookies.length == 0) {
            return null;
        }

        Optional<String> value = Arrays.stream(cookies)
            .filter(c -> c.getName().equals(key))
            .map(Cookie::getValue)
            .findAny();
        return value;
    }
}
