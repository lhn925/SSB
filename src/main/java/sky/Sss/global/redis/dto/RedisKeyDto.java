package sky.Sss.global.redis.dto;


public class RedisKeyDto {
    public static final String REDIS_SESSION_KEY = "spring:session:sessions:";
    public static final String REDIS_WS_SESSION_KEY = "spring:session:ws:sessions:";
    public static final String REDIS_USER_CACHE_TOKEN_KEY = "spring:user:cache:token";
    public static final String REDIS_USER_WS_LIST_SESSION_KEY = "spring:user:ws:list:session:";

    public static final String REDIS_REMEMBER_KEY = "spring:session:remember:";
    public static final String REDIS_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
    public static final String REDIS_LOGIN_KEY = "spring:redisToken:redis:";
    public static final String REDIS_TRACK_LIKES_KEY = "spring:track:likes:";
    public static final String REDIS_PLY_LIKES_KEY = "spring:ply:likes:";
    public static final String REDIS_REPLY_LIKES_KEY = "spring:reply:likes:";
    public static final String REDIS_TRACK_LIKES_TOTAL_KEY = "spring:track:likes:total";
    public static final String REDIS_PLY_LIKES_TOTAL_KEY = "spring:ply:likes:total";
    public static final String REDIS_REPLY_LIKES_TOTAL_KEY = "spring:reply:likes:total";

    public static final String REDIS_PUSH_MSG_LIST_KEY = "spring:push:msg:list:";
    public static final String REDIS_USER_KEY = "USER_ID";



}
