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
