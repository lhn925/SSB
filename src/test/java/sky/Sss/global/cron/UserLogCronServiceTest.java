package sky.Sss.global.cron;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import sky.Sss.domain.user.service.log.UserLoginLogService;

@SpringBootTest
class UserLogCronServiceTest {


    @Autowired
    UserLoginLogService userLoginLogService;

    @Test
    void userActivityCron() {

    }

    @Test
    @Rollback(value = true)
    void userLoginLogCron() {

        Integer result = userLoginLogService.expireLoginLogOff(3);

        System.out.println("result = " + result);
        // 데이터 100개를 했을 때 161ms
        // 데이터 1000개를 했을 때 188ms
        // 683
    }

    @Test
    @Rollback
    void userLoginLogCron2() {
        /**
         * 3개월이 지난 log id 리스트를 구하고 update
         * 데이터 십만개를 테스트 했을때 걸리는 시간 : 1sec 494ms
         */
//        Integer result = userLoginLogService.expireLoginLogOff2(3);

//        System.out.println("result = " + result);

        // 데이터 100개를 했을 때 127ms
        // 데이터 1000개를 했을 때 221ms
        // 1sec 494ms
    }
}