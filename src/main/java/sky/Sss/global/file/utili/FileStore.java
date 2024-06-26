package sky.Sss.global.file.utili;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import marvin.image.MarvinImage;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.global.exception.FileExtConstraintException;
import sky.Sss.global.file.dto.UploadFileDto;
import sky.Sss.global.file.dto.UploadTrackFileDto;

/**
 * 각종 URL 정보
 */

@Slf4j
@Component
@Getter
public class FileStore {


    // 2차 인증 사진 url
    public static final String CAPTCHA_IMAGE_DIR = "captcha/";
    public static final String IMAGE_DIR = "image/";
    public static final String TRACK_DIR = "track/";
    //default Image url및 사진
    public static final String USER_DEFAULT_IMAGE_URL = "picture-default.png";
    public static final String USER_DEFAULT_DIR = "default";

    public static final String PICTURE_TYPE = "picture-";
    public static final String COVER_TYPE = "cover-";

    public static final Long TRACK_UPLOAD_LIMIT = 10800L;

    private String captchaImageDir = CAPTCHA_IMAGE_DIR;
    private String userDefaultImageUrl = USER_DEFAULT_IMAGE_URL;
    private String trackFileDir = TRACK_DIR;
    private String imageDir = IMAGE_DIR;


    @Value("${file.dir}")
    public String fileDir;

    public String getFullPath(String path, String fileName) {
        return path + fileName;
    }

    public void deleteFile(String dirType, String filename) {
        Path filePath = Paths.get(getFileDir() + dirType + "/" + filename);
        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 파일 폴더 + type + 파일이름  + 확장자명 생성
     */
    public String getFilePathAndExt(String type, String filename, String ext) {
        return fileDir + type + filename + "." + ext;
    }

    /**
     * 파일 저장
     *
     * @param multipartFile
     *     // 저장 위치
     * @return
     * @throws IOException
     */
    public UploadFileDto storeFileSave(MultipartFile multipartFile, String imageType, int targetWidth) {
        if (multipartFile.isEmpty()) {
            return null;
        }
        fileExtConstraint(multipartFile, imageDir);
        multipartFile.getOriginalFilename();

        // 확장자 jpeg 로 통일
        // 사진 이미지의 파일명은 전부다 랜덤아이디로
        String fileFormatName = "jpeg";

        String originalFilename = UUID.randomUUID() + "." + fileFormatName;
        String storeFileName = imageType + originalFilename;

        // 유저 프로필 사진
        try {
            MultipartFile resizingFile = resizeImage(targetWidth, multipartFile, fileFormatName, originalFilename);
            resizingFile.transferTo(new File(getFullPath(fileDir + imageDir, storeFileName)));
            return new UploadFileDto(originalFilename, storeFileName, null);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /**
     * track 파일 저장
     *
     * @param multipartFile
     *     // 저장 위치
     * @return
     * @throws IOException
     */
    public UploadFileDto storeTrackFileSave(MultipartFile multipartFile, String fileDir, String token) {
        if (multipartFile.isEmpty()) {
            return null;
        }
        fileExtConstraint(multipartFile, fileDir);
        String originalFilename = multipartFile.getOriginalFilename(); // 원본 파일명 (확장자포함)

        String storeFileName = createStoreFileName(originalFilename); // 서버 업로드 파일명 (확장자 포함)
        String dirPath = createDir(fileDir + token); // 유저 폴더 경로 생성
        // 유저 프로필 사진
        return getUploadTrackFileDto(multipartFile, originalFilename, storeFileName, dirPath);
    }

    /**
     * 확장자 검사
     *
     * @param multipartFile
     * @param fileDir
     */
    public void fileExtConstraint(MultipartFile multipartFile, String fileDir) {
        boolean isValid = false;
        if (fileDir.equals(FileStore.IMAGE_DIR)) {
            isValid = FileUtils.validImgFile(multipartFile);
        } else if (fileDir.equals(trackFileDir)) {
            isValid = FileUtils.validTrackFile(multipartFile);
        }
        if (!isValid) {
            throw new FileExtConstraintException();
        }
    }


    /**
     * tarck 재생시간 추출
     *
     * @param multipartFile
     * @param originalFilename
     * @param storeFileName
     * @param dirPath
     * @return
     * @throws IOException
     */
    private UploadTrackFileDto getUploadTrackFileDto(MultipartFile multipartFile, String originalFilename,
        String storeFileName, String dirPath) {

        AudioFile audioFile = null;
        try {
            File file = new File(getFullPath(dirPath, storeFileName));
            multipartFile.transferTo(file);
            audioFile = AudioFileIO.read(new File(file.getPath()));
        } catch (CannotReadException | ReadOnlyFileException | InvalidAudioFrameException | TagException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        // 길이
        Integer track_length = audioFile.getAudioHeader().getTrackLength();
        // 음질

        String bitRate = audioFile.getAudioHeader().getBitRate();//음질
        // 크기
        Long size = multipartFile.getSize();
        // 확장자
        String ext = audioFile.getExt();
        return new UploadTrackFileDto(originalFilename, storeFileName, null, track_length, bitRate, size, ext);
    }


    /**
     * 이미지 resize
     *
     * @param targetWidth
     * @param originalFile
     * @param fileFormatName
     * @param storeFileName
     * @return
     * @throws IOException
     */
    public MultipartFile resizeImage(int targetWidth, MultipartFile originalFile, String fileFormatName,
        String storeFileName) throws IOException {

        // MultipartFile -> BufferedImage Convert
        BufferedImage image = null;
        try {
            image = ImageIO.read(originalFile.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /**
         *
         // 작아도 500x500으로 유지할 예정으로 주석처리
         // newWidth : newHeight = originWidth : originHeight
         int originWidth = image.getWidth();
         int originHeight = image.getHeight();

         // 500 x 500 을 위한 주석처리
         int targetHeight = targetWidth * originHeight / originWidth;

         // 작아도 500x500으로 유지할 예정으로 주석처리
         // origin 이미지가 resizing 될 사이즈보다 작을 경우 resizing 작업 안함
         if (originWidth < targetWidth) {
         return originalFile;
         }
         */

        MarvinImage imageMarvin = new MarvinImage(image);

        Scale scale = new Scale();

        scale.load();
        scale.setAttribute("newWidth", targetWidth);
        scale.setAttribute("newHeight", targetWidth);
        scale.process(imageMarvin.clone(), imageMarvin, null, null, false);

        BufferedImage imageNoAlpha = imageMarvin.getBufferedImageNoAlpha();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageIO.write(imageNoAlpha, fileFormatName, baos);
        baos.flush();

        return new MockMultipartFile(storeFileName, baos.toByteArray());
    }

    /**
     * 파일 여러개 저장
     *
     * @param multipartFile
     *     // 저장 위치
     * @return
     */
    public List<UploadFileDto> storeTrackFiles(List<MultipartFile> multipartFile, String type, String token) {
        List<UploadFileDto> uploadFileDtos = new ArrayList<>();
        for (MultipartFile file : multipartFile) {
            if (!file.isEmpty()) {
                uploadFileDtos.add(storeTrackFileSave(file, type, token));
            }
        }
        return uploadFileDtos;
    }


    /**
     * 디렉터리 생성
     *
     * @param path
     *     type + token
     * @return
     */
    private String createDir(String path) {
        Path directoryPath = Paths.get(fileDir + path);
        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return fileDir + path + "/";
    }

    /**
     * 서버에 저장할 이름 생성
     *
     * @param originalFilename
     * @return
     */
    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);

        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }


    /**
     * 확장 추출
     *
     * @param originalFilename
     * @return
     */
    public String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf("."); // 확장자 위치
        return originalFilename.substring(pos + 1);// 추출
    }

    public UrlResource getCaptchaUrlResource(String fileName) {
        try {
            return new UrlResource("file:" + this.getFilePathAndExt(this.getCaptchaImageDir(), fileName, "jpg"));
        } catch (MalformedURLException e) {
            throw new RuntimeException("file.error.notFind");
        }
    }

    public UrlResource getUrlResource(String fileName) {
        UrlResource resource = null;
        try {
            resource = new UrlResource("file:" + fileDir + fileName);
        } catch (MalformedURLException e) {
            throw new RuntimeException("file.error.notFind");
        }
        return resource;
    }

}
