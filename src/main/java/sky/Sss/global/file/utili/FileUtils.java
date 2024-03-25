package sky.Sss.global.file.utili;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 확장자 검증
 */
@Slf4j
public class FileUtils {

    private static final Tika tika = new Tika();
    public static final String[] IMAGE_EXT_LIST = {"image/jpg", "image/jpeg", "image/pjpeg", "image/png",
        "image/bmp", "image/x-windows-bmp"};
    public static final String[] TRACK_EXT_LIST = {"audio/mpeg"," audio/mp3", "audio/flac", "audio/ogg", "audio/mp4",
        "audio/wav","audio/x-m4a"};

    public static boolean validImgFile(MultipartFile multipartFile) {
        List<String> notValidTypeList = Arrays.asList(IMAGE_EXT_LIST);
        return valid(multipartFile, notValidTypeList);
    }
    public static boolean validTrackFile(MultipartFile multipartFile) {
        List<String> notValidTypeList = Arrays.asList(TRACK_EXT_LIST);
        return valid(multipartFile, notValidTypeList);
    }



    private static boolean valid(MultipartFile multipartFile, List<String> notValidTypeList) {
        try {
            String mimeType = tika.detect(multipartFile.getInputStream());
            return notValidTypeList.stream()
                .anyMatch(notValidType -> notValidType.equalsIgnoreCase(mimeType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
