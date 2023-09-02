package sky.board.global.file;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 각종 URL 정보
 */

@Slf4j
@Component
@Getter
public class FileStore {


    // 2차 인증 사진 url
    public static final String CAPTCHA_IMAGE_URL = "src/main/resources/static/image/";

    //default Image url및 사진
    public static final String USER_DEFAULT_URL = "src/main/resources/static/css/myInfo/image/user/";
    public static final String USER_DEFAULT_IMAGE = "defaultImage.png";


    @Value("${file.dir}")
    public String fileDir;




    public String getFilePath(String path, String filename, String ext) {
        return path + filename + "." + ext;
    }
    public String getFullPath (String path,String fileName) {
        return path + fileName;
    }

    public String getFileName(String file) {
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


    /**
     * 이미지 여러개 저장
     * @param multipartFile
     * @return
     * @throws IOException
     */
    public List<UploadFile> storeFiles (List<MultipartFile> multipartFile) throws IOException {
        List<UploadFile> uploadFiles = new ArrayList<>();
        for (MultipartFile file : multipartFile) {
            if (!file.isEmpty()) {
                uploadFiles.add(storeFile(file));

            }
        }
        return uploadFiles;
    }
    public void deleteFile(String path,String filename) throws IOException {
        Path filePath = Paths.get(this.getFullPath(path,filename));
        Files.delete(filePath);
    }

    /**
     * 하나의 이미지 저장
     * @param multipartFile
     * @return
     * @throws IOException
     */
    public UploadFile storeFile (MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }
        String originalFilename = multipartFile.getOriginalFilename(); // 원본 파일명
        String storeFileName = createStoreFileName(originalFilename); // 서버 업로드 파일명

        log.info("originalFilename = {} ",originalFilename);
        log.info("storeFileName = {} ",storeFileName);
        multipartFile.transferTo(new File(getFullPath(fileDir,storeFileName)));
        return new UploadFile(originalFilename, storeFileName);
    }


    public String getFileName(String file, String ext) {
        return file + "." + ext;
    }

    private String createStoreFileName (String originalFilename) {
        String ext = extractExt(originalFilename);

        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }


    public String extractExt (String originalFilename) {
        int pos = originalFilename.lastIndexOf("."); // 확장자 위치
        return originalFilename.substring(pos + 1);// 추출
    }
}
