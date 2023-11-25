package sky.Sss.global.utili;

public class JSEscape {

    public static String escapeJS(String text) {
        if (text == null || text.length() == 0) {
            return null;
        }
        return text.replaceAll("<", "\\u003C")
            .replaceAll(">", "\\u003E")
            .replaceAll(":", "\\u003A")
            .replaceAll("'", "\\u0027")
            .replaceAll("\"", "\\u0022")
            .replaceAll(".", "\\u002E");
    }
}
