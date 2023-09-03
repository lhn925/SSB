package sky.board.global.file.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadFile {
    private String uploadFileName; // 고객이 업로드한 파일명
    private String storeFileName; // 서버 내부에 저장할 파일명

    public UploadFile (String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
