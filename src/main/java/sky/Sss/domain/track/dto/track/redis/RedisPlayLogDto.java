package sky.Sss.domain.track.dto.track.redis;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.log.SsbTrackAllPlayLogs;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class RedisPlayLogDto {
    private String token;
    private Long trackId;
    private String storeFileName;
    private String trackToken;
    private Long expireTime;
    @Builder
    public RedisPlayLogDto(String token, Long trackId, String storeFileName, String trackToken, Long expireTime) {
        this.token = token;
        this.trackId = trackId;
        this.storeFileName = storeFileName;
        this.trackToken = trackToken;
        this.expireTime = expireTime;
    }

    public static RedisPlayLogDto create(SsbTrackAllPlayLogs ssbTrackAllPlayLogs) {
        return RedisPlayLogDto.builder().token(ssbTrackAllPlayLogs.getToken()).storeFileName(ssbTrackAllPlayLogs.getSsbTrack().getStoreFileName()).trackId(ssbTrackAllPlayLogs.getSsbTrack().getId())
            .trackToken(ssbTrackAllPlayLogs.getSsbTrack().getToken()).expireTime(ssbTrackAllPlayLogs.getExpireTime()).build();
    }
}
