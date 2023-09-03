package sky.board.global.file.utili;


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
import sky.board.global.file.dto.UploadFile;

/**
 * 각종 URL 정보
 */

@Slf4j
@Component
@Getter
public class FileStore {


    // 2차 인증 사진 url
    public static final String CAPTCHA_IMAGE_URL = "captcha/";
    public static final String USER_PICTURE_URL = "picture/";
    //default Image url및 사진
    public static final String USER_DEFAULT_URL = "default/";
    public static final String USER_DEFAULT_IMAGE = "defaultImage.png";
    private String userPictureUrl = USER_PICTURE_URL;
    private String captchaImageUrl = CAPTCHA_IMAGE_URL;
    private String userDefaultUrl = USER_DEFAULT_URL;
    private String userDefaultImage = USER_DEFAULT_IMAGE;

    @Value("${file.dir}")
    public String fileDir;

    public String getFilePath(String path, String filename, String ext) {
        return fileDir + path + filename + "." + ext;
    }

    public String getFullPath(String path, String fileName) {
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
     *
     * @param multipartFile
     * @param token
     *     // 저장 위치
     * @return
     * @throws IOException
     */
    public List<UploadFile> storeFiles(List<MultipartFile> multipartFile, String type, String token)
        throws IOException {
        List<UploadFile> uploadFiles = new ArrayList<>();
        for (MultipartFile file : multipartFile) {
            if (!file.isEmpty()) {
                uploadFiles.add(storeFile(file, type, token));

            }
        }
        return uploadFiles;
    }

    public void deleteFile(String path, String filename) throws IOException {
        Path filePath = Paths.get(this.getFullPath(path, filename));
        Files.delete(filePath);
    }

    /**
     * 하나의 이미지 저장
     *
     * @param multipartFile
     * @param token
     *     // 저장 위치
     * @return
     * @throws IOException
     */
    public UploadFile storeFile(MultipartFile multipartFile, String type, String token) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }
        String originalFilename = multipartFile.getOriginalFilename(); // 원본 파일명
        String storeFileName = createStoreFileName(originalFilename); // 서버 업로드 파일명

        String dirPath = createDir(type + token);

        log.info("dirPath = {}", dirPath);
        log.info("originalFilename = {} ", originalFilename);
        log.info("storeFileName = {} ", storeFileName);
        multipartFile.transferTo(new File(getFullPath(dirPath, storeFileName)));
        return new UploadFile(originalFilename, storeFileName);
    }


    private String createDir(String path) {
        Path directoryPath = Paths.get(fileDir + path);
        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileDir + path + "/";
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);

        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }


    public String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf("."); // 확장자 위치
        return originalFilename.substring(pos + 1);// 추출
    }
}
