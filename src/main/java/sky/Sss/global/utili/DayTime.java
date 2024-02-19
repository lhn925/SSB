package sky.Sss.global.utili;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import sky.Sss.domain.track.model.Hour;

public class DayTime {

    public static String DAY_TIME_FORMAT = "YYYYMMddHH";

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
}
