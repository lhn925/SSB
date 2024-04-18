package sky.Sss.global.utili;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import sky.Sss.domain.track.model.Hour;

public class DayTime {

    public final static String DAY_TIME_FORMAT = "YYYYMMddHH";

    // 파일 제한 시간
    public final static int EXPIRE_FILE_HOUR_TIME = 1;

    public static int getDayTime(LocalDateTime playDateTime) {
        return Integer.parseInt(playDateTime.format(DateTimeFormatter.ofPattern(DAY_TIME_FORMAT)));
    }

    /**
     *
     * @param playDateTime
     * @param minusHour playDateTime 에 마이너스를 할 시간
     * @return
     */
    public static int getDayTime(LocalDateTime playDateTime, Hour minusHour) {
        return Integer.parseInt(playDateTime.minusHours(minusHour.getValue()).format(DateTimeFormatter.ofPattern(DAY_TIME_FORMAT)));
    }

    public static Instant localDateTimeToEpochMillis(LocalDateTime createdDateTime) {
        ZonedDateTime zonedDateTime = createdDateTime.atZone(ZoneId.systemDefault());

        return zonedDateTime.toInstant();
    }

    public static LocalDateTime millisToLocalDateTime(long nowTimeMillis) {
        // Instant 객체 생성
        Instant instant = Instant.ofEpochMilli(nowTimeMillis);

        // 시스템의 기본 시간대를 사용하여 nowTimeMillis -> LocalDateTime 으로 변환
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

}
