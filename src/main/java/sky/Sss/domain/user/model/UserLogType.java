package sky.Sss.domain.user.model;

import org.springframework.util.StringUtils;

public enum UserLogType {
    HISTORY_LOGIN_LOG("HISTORY_LOGIN_LOG"),HISTORY_ACTIVITY_LOG("HISTORY_ACTIVITY_LOG");

    private String type;
    UserLogType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public static UserLogType findByType (String type) {
        UserLogType userLogType;
        if (!StringUtils.hasText(type) || type.equals(HISTORY_LOGIN_LOG.getType())) {
            userLogType = UserLogType.HISTORY_LOGIN_LOG;
        } else {
            userLogType = UserLogType.HISTORY_ACTIVITY_LOG;
        }
        return userLogType;
    }


}
