package sky.Sss.global.file.dto;


import static lombok.AccessLevel.PROTECTED;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
public class UploadTrackFileDto extends UploadFileDto{
    // 재생길이
    private Integer trackLength;
    // 파일크기
    private Long size;
    // 음질
    private String bitRate;
    // 확장자
    private String ext;


    public UploadTrackFileDto(String uploadFileName, String storeFileName, String userId, Integer track_length,
        String bitRate,
        Long size,String ext) {
        super(uploadFileName, storeFileName, userId);
        this.trackLength = track_length;
        this.bitRate = bitRate;
        this.size = size;
        this.ext = ext;
    }
}
