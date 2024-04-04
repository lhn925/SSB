package sky.Sss.global.utili.validation.regex;

public class RegexPatterns {
    public static final String TRACK_TITLE_REGEX = "^[\\p{L}\\p{N} .,:()]{1,100}$";
    public static final String EMAIL_REGEX = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
    public static final String USER_ID_REGEX = "^[a-z0-9_-]{5,20} *$";
    public static final String USER_NAME_REGEX = "^(?!\\.|.*\\.\\.$)(?!.*\\.\\.)(?!.*\\._|.*_\\.)(?!_)[A-Za-z0-9_.]{1,28}(?<![_.])$";
//    public static final String USER_NAME_REGEX = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,8}$";
//    public static final String TAG_REGEX = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]$";
    public static final String REPLY_CONTENTS_REGEX = "^.{1,1000}$";
    public static final String TRACK_DESC_REGEX = "^.{0,1000}$";
    public static final String REPOST_COMMENT_REGEX = "^.{1,140}$";
    public static final String GENRE_REGEX =  "^[\\p{L}\\p{N} .,:()]{0,100}$";
}
