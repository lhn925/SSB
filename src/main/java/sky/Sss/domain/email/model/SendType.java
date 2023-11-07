package sky.Sss.domain.email.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum SendType {
    JOIN,ID,PW,EMAIL;
    @JsonCreator
    public static SendType from(String s) {
        return SendType.valueOf(s);
    }
}
