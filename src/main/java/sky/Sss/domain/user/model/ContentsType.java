package sky.Sss.domain.user.model;

import org.bytedeco.javacpp.opencv_core.RefOrVoid.type;
import sky.Sss.global.redis.dto.RedisKeyDto;

/**
 * push message 에 구분 하는 enum
 */
public enum ContentsType {
    TRACK("TRACK","/tracks/info/"),PLAYLIST("PLAYLIST","/tracks/ply/info/"),REPLY_TRACK("REPLY_TRACK","/tracks/info/"),REPLY_PLAYLIST("REPLY_PLAYLIST","/tracks/ply/info/"),
    USER("USER", "/users/info/");

    private final String type;
    private final String url;

    ContentsType(String type,String url) {
        this.type = type;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }


    public String getLikeKeyByType () {
        String key = null;
        switch (this) {
            case TRACK -> key = RedisKeyDto.REDIS_TRACK_LIKES_MAP_KEY;
            case PLAYLIST -> key = RedisKeyDto.REDIS_PLY_LIKES_MAP_KEY;
            case REPLY_TRACK -> key = RedisKeyDto.REDIS_TRACK_REPLY_LIKES_MAP_KEY;
            case REPLY_PLAYLIST -> key = RedisKeyDto.REDIS_PLY_REPLY_LIKES_MAP_KEY;
        }
        return key;
    }
}
