package sky.Sss.domain.user.model;

import lombok.extern.slf4j.Slf4j;
import sky.Sss.global.redis.dto.RedisKeyDto;

/**
 * push message 에 구분 하는 enum
 */
@Slf4j
public enum ContentsType {
    TRACK("TRACK", "/tracks/"),
    PLAYLIST("PLAYLIST", "/tracks/ply/"),
    REPLY_TRACK("REPLY_TRACK", "/tracks/"),
    REPLY_PLAYLIST("REPLY_PLAYLIST", "/tracks/ply/"),
    REPOST("REPOST", "/tracks/ply/"),
    HASHTAG("HASHTAG", ""),
    USER("USER", "/users/");

    private String type;
    private String url;

    ContentsType(String type, String url) {
        this.type = type;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }


    public String getRepostKey() {
        String key = null;
        switch (this) {
            case TRACK -> key = RedisKeyDto.REDIS_TRACK_REPOST_MAP_KEY;
            case PLAYLIST -> key = RedisKeyDto.REDIS_PLY_REPOST_MAP_KEY;
            default -> throw new IllegalArgumentException();
        }
        return key;
    }
    
    public String getLikeKey() {
        String key = null;
        switch (this) {
            case TRACK -> key = RedisKeyDto.REDIS_TRACK_LIKES_MAP_KEY;
            case PLAYLIST -> key = RedisKeyDto.REDIS_PLY_LIKES_MAP_KEY;
            case REPLY_TRACK -> key = RedisKeyDto.REDIS_TRACK_REPLY_LIKES_MAP_KEY;
            case REPLY_PLAYLIST -> key = RedisKeyDto.REDIS_PLY_REPLY_LIKES_MAP_KEY;
        }
        return key;
    }

    public String getUserLikedKey() {
        String key = null;
        switch (this) {
            case TRACK -> key = RedisKeyDto.REDIS_USER_TRACK_LIKED_LIST_MAP_KEY;
            case PLAYLIST -> key = RedisKeyDto.REDIS_USER_PLY_LIKED_LIST_MAP_KEY;
            case REPLY_TRACK -> key = RedisKeyDto.REDIS_USER_TRACK_REPLY_LIKED_MAP_KEY;
            case REPLY_PLAYLIST -> key = RedisKeyDto.REDIS_USER_PLY_REPLY_LIKED_MAP_KEY;
        }
        return key;
    }
}
