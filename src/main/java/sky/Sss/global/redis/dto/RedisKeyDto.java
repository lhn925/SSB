package sky.Sss.global.redis.dto;


import lombok.Getter;

public class RedisKeyDto {
    public static final String SESSION_KEY = "spring:session:sessions:";
    public static final String REMEMBER_KEY = "spring:session:remember:";
    public static final String CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
    public static final String REDIS_LOGIN_KEY = "spring:redisToken:redis:";
    public static final String REDIS_TRACK_LIKES_KEY = "spring:track:likes:";
    public static final String REDIS_PLY_LIKES_KEY = "spring:ply:likes:";
    public static final String REDIS_REPLY_LIKES_KEY = "spring:reply:likes:";
    public static final String REDIS_TRACK_LIKES_TOTAL_KEY = "spring:track:likes:total:";
    public static final String REDIS_PLY_LIKES_TOTAL_KEY = "spring:ply:likes:total:";
    public static final String REDIS_REPLY_LIKES_TOTAL_KEY = "spring:reply:likes:total";
    public static final String USER_KEY = "USER_ID";



}
