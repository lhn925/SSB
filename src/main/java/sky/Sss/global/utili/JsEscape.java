package sky.Sss.global.utili;

public class JsEscape {

    public static String escapeJS(String text) {
        if (text == null || text.length() == 0) {
            return null;
        }
        return text.replace("<", "\\u003C")
            .replace(">", "\\u003E")
            .replace(":", "\\u003A")
            .replace("'", "\\u0027")
            .replace("\"", "\\u0022")
//            .replace(".", "\\u002E")
            .replace("{", "\\u007B")
            .replace("}", "\\u007D")
            .replace(",", "\\u002C")
            .replace("\n", "\\u000A")
            .replace(" ", "\\u0020");
    }
}
