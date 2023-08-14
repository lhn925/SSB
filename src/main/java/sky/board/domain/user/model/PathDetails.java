package sky.board.domain.user.model;


/**
 * 각종 URL 정보
 */
public class PathDetails {

    public static final String CAPTCHA_IMAGE_URL = "/src/main/resources/static/image/";


    public static String getFilePath(String path, String filename, String ext) {
        return path + filename + "." + ext;
    }

    public static String getFileName(String file) {
        int first = 0;
        int end = file.length();

        System.out.println("file.substring(first,) = " + file.substring(first + 1, end));

        if (file.contains("/")) {
            first = file.lastIndexOf("/") + 1;
        }
        if (file.contains(".")) {
            end = file.lastIndexOf(".");
        }
        return file.substring(first, end);
    }
}
