package sky.board.global.cron;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.board.domain.user.entity.UserActivityLog;
import sky.board.domain.user.service.log.UserActivityLogService;
import sky.board.domain.user.service.log.UserLoginLogService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/cron")
public class CronController {

    private final UserActivityLogService userActivityLogService;
    private final UserLoginLogService userLoginLogService;

    /**
     * 첫 번째 * 부터
     * 초(0-59)
     * 분(0-59)
     * 시간(0-23)
     * 일(1-31)
     * 월(1-12)
     * 요일(0-6) (0: 일, 1: 월, 2:화, 3:수, 4:목, 5:금, 6:토)
     * Spring @Scheduled cron은 6자리 설정만 허용하며 연도 설정을 할 수 없다.
     */
    /**
     *
     * 매일 00 시마다 호출
     * 6개월이 지난 userActivityLog log는 다 off
     */
    @PostMapping("/userActivityLog")
    @Scheduled(cron = "0 0 0 * * *")
    public void userActivityCron () {
        Integer result = userActivityLogService.expireActivityOff(6);
        log.info("result = {}", result);
    }

    /**
     * 매일 00 시마다 호출
     * 3개월이 지난 userLoginLog  log는 다 off
     */
    @PostMapping("/userLoginLog")
    @Scheduled(cron = "0 0 0 * * *") //    초 - 분 - 시 - 일 - 월 - 요일
    public void userLoginLogCron () {
        Integer result = userLoginLogService.expireLoginLogOff(3);
        log.info("userLoginLogCron result = {}", result);
    }

}

