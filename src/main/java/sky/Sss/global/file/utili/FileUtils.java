package sky.Sss.global.file.utili;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class FileUtils {

    private static final Tika tika = new Tika();

    public static boolean validImgFile(MultipartFile multipartFile) {
        List<String> notValidTypeList = Arrays.asList("image/jpg", "image/jpeg", "image/pjpeg", "image/png",
            "image/bmp", "image/x-windows-bmp");
        return valid(multipartFile, notValidTypeList);
    }

    public static boolean validTrackFile(MultipartFile multipartFile) {
        List<String> notValidTypeList = Arrays.asList("audio/mpeg"," audio/mp3", "audio/flac", "audio/ogg", "audio/mp4",
            "audio/wav");
        return valid(multipartFile, notValidTypeList);
    }

    private static boolean valid(MultipartFile multipartFile, List<String> notValidTypeList) {
        try {
            String mimeType = tika.detect(multipartFile.getInputStream());
            boolean isValid = notValidTypeList.stream()
                .anyMatch(notValidType -> notValidType.equalsIgnoreCase(mimeType));
            return isValid;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
