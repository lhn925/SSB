package sky.Sss.global.redis.dto;


public class RedisKeyDto {

    public static final String REDIS_SESSION_KEY = "spring:session:sessions:";

    public static final String REDIS_TAGS_KEY = "spring:tags:";
    public static final String REDIS_WS_SESSION_KEY = "spring:session:ws:sessions:";
    public static final String REDIS_USER_WS_LIST_SESSION_KEY = "spring:user:ws:list:session:";

    public static final String REDIS_REMEMBER_KEY = "spring:session:remember:";
    public static final String REDIS_LOGIN_KEY = "spring:redisToken:redis:";
    public static final String REDIS_TRACK_LIKES_MAP_KEY = "spring:track:likes:";

    public static final String REDIS_TRACK_REPLY_LIKES_MAP_KEY = "spring:track:reply:likes:";
    public static final String REDIS_PLY_REPLY_LIKES_MAP_KEY = "spring:ply:reply:likes:";
    public static final String REDIS_PLY_LIKES_MAP_KEY = "spring:ply:likes:";

    public static final String REDIS_PLY_POSITION_MAP_KEY = "spring:ply:position:";

    public static final String REDIS_USERS_INFO_MAP_KEY = "spring:users:info:map:";

    public static final String REDIS_USER_IDS_MAP_KEY = "spring:user:ids:";
    public static final String REDIS_USER_EMAILS_MAP_KEY = "spring:user:email:";
    public static final String REDIS_USER_PK_ID_MAP_KEY = "spring:user:pk:id:";
    public static final String REDIS_USER_NAMES_MAP_KEY = "spring:user:usernames:";
    public static final String REDIS_TRACKS_INFO_MAP_KEY = "spring:tracks:info:map:";
    public static final String REDIS_TRACK_REPOST_MAP_KEY = "spring:track:repost:";
    public static final String REDIS_PLY_REPOST_MAP_KEY = "spring:ply:repost:";


    public static final String REDIS_REPOST_IDS_INFO_MAP_KEY = "spring:repost:ids:";


    public static final String REDIS_PLY_REPLY_MAP_KEY = "spring:ply:reply:";


    public static final String REDIS_TRACK_REPLY_MAP_KEY = "spring:track:reply:";
    public static final String REDIS_TRACK_PLAY_LOG_MAP_KEY = "spring:track:play:log:";
    // 특정 유저가 팔로우 하고 있는 유저들의 목록
    public static final String REDIS_USER_FOLLOWING_MAP_KEY = "spring:user:following:";
    // 특정 유저를 팔로우 하고 있는 유저들의 목록
    public static final String REDIS_USER_FOLLOWER_MAP_KEY = "spring:user:follower:";
    public static final String REDIS_PUSH_MSG_LIST_KEY = "spring:push:msg:list:";
    public static final String REDIS_USER_KEY = "USER_ID";

    public static final String REDIS_USER_TOTAL_LENGTH_MAP_KEY = "spring:user:total:length";


    public static final String REDIS_USER_TRACK_UPLOAD_COUNT = "spring:user:track:upload:count:";
    public static final String REDIS_USER_MY_TRACK_UPLOAD_COUNT = "spring:user:my:track:upload:count:";

    public static final String REDIS_USER_TRACK_LIKES_LIST_KEY = "spring:user:track:likes:list";

    public static final String REDIS_TRACK_LIKES_USER_LIST_KEY = "spring:track:likes:user:list";

    public static final String REDIS_CACHE_INCLUDE_CHART_EXISTS_KEY = "spring:cache:include:chart:exists";
    public static final String REDIS_PLAY_LOG_DTO_MAP_KEY = "spring:play:log:dto:map:";


    public static final String REDIS_USER_TRACK_LIKED_LIST_MAP_KEY = "spring:user:track:liked:list:";
    public static final String REDIS_USER_PLY_LIKED_LIST_MAP_KEY = "spring:user:ply:liked:list:";
    public static final String REDIS_USER_TRACK_REPLY_LIKED_MAP_KEY = "spring:user:track:reply:liked:list:";
    public static final String REDIS_USER_PLY_REPLY_LIKED_MAP_KEY = "spring:user:ply:reply:liked:list:";


}
