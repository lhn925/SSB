package sky.Sss.global.file.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadFileDto {
    private String uploadFileName; // 고객이 업로드한 파일명
    private String storeFileName; // 서버 내부에 저장할 파일명
    private String userId;

    public UploadFileDto(String uploadFileName, String storeFileName,String userId) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.userId = storeFileName;
    }
}
