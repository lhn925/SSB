package sky.board.global.utili;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.util.StringUtils;

public class Alert {

    public static String waringAlert(String message, String url, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");

        StringBuffer script = new StringBuffer("<script>");
        PrintWriter out = response.getWriter();
        script.append("alert('" + message + "');");
        if (StringUtils.hasText(url)) {
            script.append(" location.href='" + url + "';");
        }
        script.append("</script>");
        out.println(script);
        out.flush();

        return url;
    }

}
