package sky.board.domain.user.model;


/**
 * 각종 URL 정보
 */
public class ImagePathDetails {

    public static final String CAPTCHA_IMAGE_URL = "src/main/resources/static/image/";
    public static final String USER_DEFAULT_URL = "src/main/resources/static/css/myInfo/image/user/";
    public static final String USER_DEFAULT_IMAGE = "defaultImage.png";

    public static final String PATH_IMAGE_URL = "/image/";


    public static String getFilePath(String path, String filename, String ext) {
        return path + filename + "." + ext;
    }

    public static String getFileName(String file) {
        int first = 0;
        int end = file.length();

        if (file.contains("/")) {
            first = file.lastIndexOf("/") + 1;
        }
        if (file.contains(".")) {
            end = file.lastIndexOf(".");
        }
        return file.substring(first, end);
    }

    public static String getFileName(String file, String ext) {
        return file + "." + ext;
    }
}
